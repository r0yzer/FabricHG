package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.kit.kit
import net.axay.fabrik.core.task.coroutineTask
import net.axay.fabrik.core.text.literalText
import net.axay.fabrik.core.text.sendText
import net.minecraft.block.MapColor
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.FilledMapItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.map.MapState
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.net.URL
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt


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

    fun render(
        image: BufferedImage, mode: DitherMode, world: ServerWorld, x: Double, z: Double,
        player: PlayerEntity?
    ): ItemStack {
        // mojang removed the ability to set a map as locked via the "locked" field in
        // 1.17, so we create and apply our own MapState instead
        val stack = ItemStack(Items.FILLED_MAP)
        val id = world.nextMapId
        val nbt = NbtCompound()
        nbt.putString("dimension", world.registryKey.value.toString())
        nbt.putInt("xCenter", x.toInt())
        nbt.putInt("zCenter", z.toInt())
        nbt.putBoolean("locked", true)
        nbt.putBoolean("unlimitedTracking", false)
        nbt.putBoolean("trackingPosition", false)
        nbt.putByte("scale", 3.toByte())
        val state = MapState.fromNbt(nbt)
        world.putMapState(FilledMapItem.getMapName(id), state)
        stack.orCreateNbt.putInt("map", id)
        val resizedImage = image.getScaledInstance(128, 128, Image.SCALE_DEFAULT)
        val resized = convertToBufferedImage(resizedImage)
        val width = resized.width
        val height = resized.height
        val pixels = convertPixelArray(resized)
        var mapColors = MapColor.COLORS
        var imageColor: Color
        mapColors = mapColors.filterNotNull().toTypedArray()
        for (i in 0 until width) {
            for (j in 0 until height) {
                imageColor = Color(pixels[j][i], true)
                if ((mode == DitherMode.FLOYD)) state.colors[i + j * width] =
                    floydDither(mapColors, pixels, i, j, imageColor).toByte() else state.colors[i + j * width] =
                    nearestColor(mapColors, imageColor).toByte()
            }
        }
        return stack
    }

    private fun mapColorToRGBColor(colors: Array<MapColor>, color: Int): Color {
        val mcColor = Color(colors[color shr 2].color)
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
        // double[] imageVec = { (double) imageColor.getRed() / 255.0, (double)
        // imageColor.getGreen() / 255.0,
        // (double) imageColor.getBlue() / 255.0 };
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

    private fun nearestColor(colors: Array<MapColor>, imageColor: Color): Int {
        val imageVec = doubleArrayOf(
            imageColor.red.toDouble() / 255.0, imageColor.green.toDouble() / 255.0,
            imageColor.blue.toDouble() / 255.0
        )
        var best_color = 0
        var lowest_distance = 10000.0
        for (k in colors.indices) {
            val mcColor = Color(colors[k].color)
            val mcColorVec = doubleArrayOf(
                mcColor.red.toDouble() / 255.0, mcColor.green.toDouble() / 255.0,
                mcColor.blue.toDouble() / 255.0
            )
            for (shadeInd in shadeCoeffs.indices) {
                val distance = distance(imageVec, applyShade(mcColorVec, shadeInd))
                if (distance < lowest_distance) {
                    lowest_distance = distance
                    // todo: handle shading with alpha values other than 255
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

val flerKit = kit("Fler") {
    val flerImages = listOf(
        "https://media1.faz.net/ppmedia/aktuell/1516460241/1.7036321/default-retina/der-rapper-fler-am-mittwoch-im.jpg",
        "https://www.watson.de/imgdb/957c/Qx,A,0,0,1080,720,450,300,180,120/6833823809424002",
        "https://www.24hamburg.de/bilder/2020/08/13/90023148/23681144-fler-deutschrap-anzeige-gericht-polizei-berlin-instagram-fanboy-anwalt-video-freundin-2IlAZaBg2We9.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/7/77/Fler_2011_by_NicoJenner.JPG/1200px-Fler_2011_by_NicoJenner.JPG",
        "https://www.musikexpress.de/wp-content/uploads/2020/11/10/14/fler_pr_katja_kuhl_1600_2020.jpg",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRuJem4rwPbl5yjhmqxyFn7-HAqUKmECGy--Q&usqp=CAU",
        "https://upload.wikimedia.org/wikipedia/commons/2/2a/Fler_2011_%28cropped%29.JPG",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSwg7038B6vQwesFv1RGQUdM0vNojX0dtv3fA&usqp=CAU",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQsAQse6MZT78bFLCFWQ6arGOo77uvy9rlf5g&usqp=CAU"
    )
    val mode = DitherMode.NONE // das andere sieht scheiße aus
    val flerJobKey = "${this.kit.name}JobKey"
    val defaultPeriod = 10 * 1000L
    kitSelectorItem = Items.FILLED_MAP.defaultStack
    usableInInvincibility = true

    onEnable { hgPlayer, _ ->
        if (hgPlayer.playerData[flerJobKey] != null) return@onEnable

        val job = coroutineTask(howOften = Long.MAX_VALUE, period = defaultPeriod) {
            hgPlayer.serverPlayerEntity?.giveItemStack(
                MapRenderer.render(
                    ImageIO.read(URL(flerImages.random())),
                    mode,
                    hgPlayer.serverPlayerEntity!!.serverWorld,
                    hgPlayer.serverPlayerEntity!!.pos.x,
                    hgPlayer.serverPlayerEntity!!.pos.y,
                    hgPlayer.serverPlayerEntity!!
                ).setCustomName(
                    literalText("fler") {
                        color = TEXT_GRAY
                        bold = true
                    }
                )
            )
        }
        job.start()
        hgPlayer.playerData[flerJobKey] = job
    }

    events {
        onHitPlayer { fler, _, serverPlayerEntity ->
            fler.serverPlayerEntity?.sendText {
                text("fler") {
                    color = TEXT_BLUE
                }
            }

            // ich hab das hier nicht getestet
            serverPlayerEntity.equipStack(
                EquipmentSlot.OFFHAND,
                MapRenderer.render(
                    ImageIO.read(URL(flerImages.random())),
                    mode,
                    fler.serverPlayerEntity!!.serverWorld,
                    fler.serverPlayerEntity!!.pos.x,
                    fler.serverPlayerEntity!!.pos.y,
                    fler.serverPlayerEntity!!
                ).setCustomName(
                    literalText("fler") {
                        color = TEXT_GRAY
                        bold = true
                    }
                )
            )
        }
    }
}