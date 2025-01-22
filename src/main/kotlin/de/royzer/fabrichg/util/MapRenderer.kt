package de.royzer.fabrichg.util

import de.royzer.fabrichg.mixins.world.MapColorAccessor
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.MapItem
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.saveddata.maps.MapId
import net.minecraft.world.level.saveddata.maps.MapItemSavedData
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.nbt.dsl.nbtCompound
import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import kotlin.math.*


enum class DitherMode {
    NONE, FLOYD;

    companion object {
        fun fromString(string: String): DitherMode {
            if (string.equals("NONE", ignoreCase = true)) return NONE else if (string.equals(
                    "DITHER",
                    ignoreCase = true
                ) || string.equals("FLOYD", ignoreCase = true)
            ) return FLOYD
            throw IllegalArgumentException("invalid dither mode")
        }
    }
}

object MapRenderer {
    private val shadeCoeffs = doubleArrayOf(0.71, 0.86, 1.0, 0.53)
    private fun distance(vectorA: DoubleArray, vectorB: DoubleArray): Double {
        return sqrt(
            (vectorA[0] - vectorB[0]).pow(2.0) + Math.pow(vectorA[1] - vectorB[1], 2.0)
                    + (vectorA[2] - vectorB[2]).pow(2.0)
        )
    }

    private fun applyShade(color: DoubleArray, ind: Int): DoubleArray {
        val coeff = shadeCoeffs[ind]
        return doubleArrayOf(color[0] * coeff, color[1] * coeff, color[2] * coeff)
    }

    fun render(image: BufferedImage, player: ServerPlayer, mode: DitherMode = DitherMode.NONE)
            = render(image, mode, player.world, player.x, player.z, player)

    fun render(
        image: BufferedImage, mode: DitherMode, world: Level, x: Double, z: Double,
        player: ServerPlayer?
    ): ItemStack {
        val id = world.freeMapId
        val nbt = nbtCompound {
            put("dimension", "minecraft:overworld") // kp wie man das von world holt
            put("xCenter", x.toInt())
            put("zCenter", z.toInt())
            put("locked", true)
            put("unlimitedTracking", false)
            put("trackingPosition", false)
            put("scale", 3.toByte())
        }
        val state = MapItemSavedData.load(nbt, world.registryAccess())
        world.setMapData(id, state)


        val stack = itemStack(Items.FILLED_MAP) {
            set(DataComponents.MAP_ID, id)
        }
        val resizedImage = image.getScaledInstance(128, 128, Image.SCALE_DEFAULT)
        val resized = convertToBufferedImage(resizedImage)
        val width = resized.width
        val height = resized.height
        val pixels = convertPixelArray(resized)
        var mapColors =  MapColorAccessor.getMaterialColors()
        var imageColor: Color
        mapColors = mapColors.filterNotNull().toTypedArray()

        for (i in 0 until width) {
            for (j in 0 until height) {
                imageColor = Color(pixels[j][i], true)
                state.colors[i + j * width] = when (mode) {
                    DitherMode.FLOYD -> floydDither(mapColors, pixels, i, j, imageColor).toByte()
                    DitherMode.NONE -> nearestColor(mapColors, imageColor).toByte()
                }
            }
        }
        return stack
    }

    private fun mapColorToRGBColor(colors: Array<MapColor>, color: Int): Color {
        val mcColor = Color(colors[color shr 2].col)
        val mcColorVec = doubleArrayOf(mcColor.red.toDouble(), mcColor.green.toDouble(), mcColor.blue.toDouble())
        val coeff = shadeCoeffs[color and 3]
        return Color((mcColorVec[0] * coeff).toInt(), (mcColorVec[1] * coeff).toInt(), (mcColorVec[2] * coeff).toInt())
    }

    private fun floydDither(
        mapColors: Array<MapColor>,
        pixels: Array<IntArray>,
        x: Int,
        y: Int,
        imageColor: Color
    ): Int {
        val colorIndex = nearestColor(mapColors, imageColor)
        val palletedColor = mapColorToRGBColor(mapColors, colorIndex)
        val error = NegatableColor(
            imageColor.red - palletedColor.red,
            imageColor.green - palletedColor.green, imageColor.blue - palletedColor.blue
        )
        if (pixels[0].size > x + 1) {
            val pixelColor = Color(pixels[y][x + 1], true)
            pixels[y][x + 1] = applyError(pixelColor, error, 7.0 / 16.0)
        }
        if (pixels.size > y + 1) {
            if (x > 0) {
                val pixelColor = Color(pixels[y + 1][x - 1], true)
                pixels[y + 1][x - 1] = applyError(pixelColor, error, 3.0 / 16.0)
            }
            var pixelColor = Color(pixels[y + 1][x], true)
            pixels[y + 1][x] = applyError(pixelColor, error, 5.0 / 16.0)
            if (pixels[0].size > x + 1) {
                pixelColor = Color(pixels[y + 1][x + 1], true)
                pixels[y + 1][x + 1] = applyError(pixelColor, error, 1.0 / 16.0)
            }
        }
        return colorIndex
    }

    private fun applyError(pixelColor: Color, error: NegatableColor, quantConst: Double): Int {
        val pR = clamp(pixelColor.red + (error.r.toDouble() * quantConst).toInt(), 0, 255)
        val pG = clamp(pixelColor.green + (error.g.toDouble() * quantConst).toInt(), 0, 255)
        val pB = clamp(pixelColor.blue + (error.b.toDouble() * quantConst).toInt(), 0, 255)
        return Color(pR, pG, pB, pixelColor.alpha).rgb
    }

    private fun clamp(i: Int, min: Int, max: Int): Int {
        if (min > max) throw IllegalArgumentException("max value cannot be less than min value")
        if (i < min) return min
        return if (i > max) max else i
    }

    fun rgbToLab(rgb: Int): Triple<Double, Double, Double> {
        val r = ((rgb shr 16) and 0xFF) / 255.0
        val g = ((rgb shr 8) and 0xFF) / 255.0
        val b = (rgb and 0xFF) / 255.0

        fun pivot(value: Double): Double {
            return if (value > 0.04045) {
                ((value + 0.055) / 1.055).pow(2.4)
            } else {
                value / 12.92
            }
        }

        val x = pivot(r) * 0.4124564 + pivot(g) * 0.3575761 + pivot(b) * 0.1804375
        val y = pivot(r) * 0.2126729 + pivot(g) * 0.7151522 + pivot(b) * 0.0721750
        val z = pivot(r) * 0.0193339 + pivot(g) * 0.1191920 + pivot(b) * 0.9503041

        // Normalize for D65 illumination
        val xn = x / 0.95047
        val yn = y / 1.00000
        val zn = z / 1.08883

        fun labPivot(value: Double): Double {
            return if (value > 0.008856) {
                value.pow(1.0 / 3.0)
            } else {
                (7.787 * value) + (16.0 / 116.0)
            }
        }

        val l = (116 * labPivot(yn)) - 16
        val a = 500 * (labPivot(xn) - labPivot(yn))
        val bLab = 200 * (labPivot(yn) - labPivot(zn))

        return Triple(l, a, bLab)
    }

    fun deltaE(lab1: Triple<Double, Double, Double>, lab2: Triple<Double, Double, Double>): Double {
        val (l1, a1, b1) = lab1
        val (l2, a2, b2) = lab2

        val avgL = (l1 + l2) / 2.0
        val c1 = sqrt(a1 * a1 + b1 * b1)
        val c2 = sqrt(a2 * a2 + b2 * b2)
        val avgC = (c1 + c2) / 2.0

        val g = 0.5 * (1 - sqrt(avgC.pow(7.0) / (avgC.pow(7.0) + 25.0.pow(7.0))))
        val a1Prime = (1 + g) * a1
        val a2Prime = (1 + g) * a2

        val c1Prime = sqrt(a1Prime * a1Prime + b1 * b1)
        val c2Prime = sqrt(a2Prime * a2Prime + b2 * b2)

        val avgCPrime = (c1Prime + c2Prime) / 2.0

        val h1Prime = if (b1 == 0.0 && a1Prime == 0.0) 0.0 else atan2(b1, a1Prime)
        val h2Prime = if (b2 == 0.0 && a2Prime == 0.0) 0.0 else atan2(b2, a2Prime)

        val deltahPrime = if (abs(h1Prime - h2Prime) > Math.PI) {
            h2Prime - h1Prime + 2 * Math.PI
        } else {
            h2Prime - h1Prime
        }

        val avgHPrime = if (abs(h1Prime - h2Prime) > Math.PI) {
            (h1Prime + h2Prime + 2 * Math.PI) / 2.0
        } else {
            (h1Prime + h2Prime) / 2.0
        }

        val deltaL = l2 - l1
        val deltaC = c2Prime - c1Prime
        val deltaH = 2 * sqrt(c1Prime * c2Prime) * sin(deltahPrime / 2.0)

        val sL = 1.0 + ((0.015 * (avgL - 50).pow(2.0)) / sqrt(20 + (avgL - 50).pow(2.0)))
        val sC = 1.0 + 0.045 * avgCPrime
        val sH = 1.0 + 0.015 * avgCPrime * (1 - 0.17 * cos(avgHPrime - Math.PI / 6) +
                0.24 * cos(2 * avgHPrime) + 0.32 * cos(3 * avgHPrime + Math.PI / 30) -
                0.20 * cos(4 * avgHPrime - 63 * Math.PI / 180))

        val rt = -2 * sqrt(avgCPrime.pow(7.0) / (avgCPrime.pow(7.0) + 25.0.pow(7.0))) * sin(Math.PI / 3 * exp(-((avgHPrime - 275 * Math.PI / 180) / (25 * Math.PI / 180)).pow(2.0)))

        return sqrt((deltaL / sL).pow(2.0) + (deltaC / sC).pow(2.0) + (deltaH / sH).pow(2.0) + rt * (deltaC / sC) * (deltaH / sH))
    }

    fun DoubleArray.toTriple(): Triple<Double, Double, Double> {
        return Triple(this[0], this[1], this[2])
    }

    private fun nearestColor(colors: Array<MapColor>, imageColor: Color): Int {
        val imageVec = doubleArrayOf(
            imageColor.red.toDouble() / 255.0, imageColor.green.toDouble() / 255.0,
            imageColor.blue.toDouble() / 255.0
        )
        var best_color = 0
        var lowest_distance = 10000.0
        for (k in colors.indices) {
            val mcColor = Color(colors[k].col)
            val mcColorVec = doubleArrayOf(
                mcColor.red.toDouble() / 255.0,
                mcColor.green.toDouble() / 255.0,
                mcColor.blue.toDouble() / 255.0
            )
            for (shadeInd in shadeCoeffs.indices) {
                val distance = deltaE(imageVec.toTriple(), applyShade(mcColorVec, shadeInd).toTriple())
                if (distance < lowest_distance) {
                    lowest_distance = distance
                    best_color = if (k == 0 && imageColor.alpha == 255) {
                        119
                    } else {
                        k * shadeCoeffs.size + shadeInd
                    }
                }
            }
        }
        return best_color
    }

    private fun convertPixelArray(image: BufferedImage): Array<IntArray> {
        val pixels = (image.raster.dataBuffer as DataBufferByte).data
        val width = image.width
        val height = image.height
        val result = Array(height) { IntArray(width) }
        val pixelLength = 4
        var pixel = 0
        var row = 0
        var col = 0
        while (pixel + 3 < pixels.size) {
            var argb = 0
            argb += ((pixels[pixel].toInt() and 0xff) shl 24) // alpha
            argb += (pixels[pixel + 1].toInt() and 0xff) // blue
            argb += ((pixels[pixel + 2].toInt() and 0xff) shl 8) // green
            argb += ((pixels[pixel + 3].toInt() and 0xff) shl 16) // red
            result[row][col] = argb
            col++
            if (col == width) {
                col = 0
                row++
            }
            pixel += pixelLength
        }
        return result
    }

    private fun convertToBufferedImage(image: Image): BufferedImage {
        val newImage = BufferedImage(
            image.getWidth(null), image.getHeight(null),
            BufferedImage.TYPE_4BYTE_ABGR
        )
        val g = newImage.createGraphics()
        g.drawImage(image, 0, 0, null)
        g.dispose()
        return newImage
    }

    private data class NegatableColor(val r: Int, val g: Int, val b: Int)
}