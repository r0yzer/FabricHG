package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.bots.HGBot
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.silkmc.silk.core.entity.modifyVelocity
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3
import org.joml.Vector2f
import kotlin.collections.set
import kotlin.math.absoluteValue
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

private const val KANGA_LAST_HIT_BY_KEY = "kangalasthitby"

enum class KangaState {
    Idle,
    JumpedHorizontal,
    JumpedVertical
}

data class HitInfo(
    val timestamp: Long,
    val player: ServerPlayer
)

infix fun Vector2f.dot(other: Vector2f): Float = this.x * other.x + this.y * other.y


operator fun Vector2f.minus(other: Vector2f): Vector2f =
    Vector2f(this.x - other.x, this.y - other.y)

fun lookAngleDiffernz(position: Vec3, lookAngle: Vec3, ziel: Vec3): Double {
    val pos1XZ = Vector2f(position.x.toFloat(), position.z.toFloat())
    val pos2XZ = Vector2f(ziel.x.toFloat(), ziel.z.toFloat())
    val lookAngleXZ = Vector2f(lookAngle.x.toFloat(), lookAngle.z.toFloat())

    val dirToPos2 = (pos2XZ - pos1XZ).normalize()

    val normalizedLook = lookAngleXZ.normalize()

    val dotProduct = normalizedLook dot dirToPos2

    val angle = acos(dotProduct)

    return Math.toDegrees(angle.toDouble())
}

fun isAgo(hitInfo: HitInfo, maxTime: Long): Boolean {
    return (System.currentTimeMillis() - hitInfo.timestamp) > maxTime
}

fun HGPlayer.isLookingWrong(hitInfo: HitInfo, maxLookDifference: Int): Boolean {
    val position = serverPlayer?.position() ?: return false
    val otherPlayerPosition = hitInfo.player.position()

    val lookAngle = serverPlayer!!.forward

    val diff = lookAngleDiffernz(position, lookAngle, otherPlayerPosition)

    if (diff.absoluteValue > maxLookDifference) {
        return true
    }

    return false
}

val kangarooKit = kit("Kangaroo") {
    val kangaJumpStateKey = "${this.kit.name}jumpState"

    kitSelectorItem = Items.FIREWORK_ROCKET.defaultInstance
    description = "Allows you to jump higher and longer"

    val jumpVelocity by property(0.9f, "jump velocity")
    val jumpShiftVelocity by property(0.6f, "jump velocity (shift)")
    val maxLookDifference by property(50, "look difference")
    val valMaxTimeDiff by property(3.0, "time diff for debuff to go away (s)")

    kitItem {
        itemStack = kitSelectorItem

        onClick { hgPlayer, _ ->
            val hitInfo = hgPlayer.getPlayerData<HitInfo>(KANGA_LAST_HIT_BY_KEY)?.let {
                if (isAgo(it, (valMaxTimeDiff * 1000).toLong())) {
                    hgPlayer.playerData.remove(KANGA_LAST_HIT_BY_KEY)
                    null
                }

                it
            }

            if (hitInfo != null) {
                if (hgPlayer.isLookingWrong(hitInfo, maxLookDifference)) {
                    hgPlayer.serverPlayer?.forceAddEffect(MobEffectInstance(MobEffects.WITHER, 5 * 20, 2), null)
                    hgPlayer.serverPlayer?.forceAddEffect(MobEffectInstance(MobEffects.BLINDNESS, 5 * 20, 2), null)
                    return@onClick
                }
            }

            val serverPlayerEntity = hgPlayer.serverPlayer ?: return@onClick
            val kangaState = hgPlayer.getPlayerData<KangaState>(kangaJumpStateKey)

            if (kangaState == KangaState.JumpedHorizontal) return@onClick


            if (serverPlayerEntity.isShiftKeyDown) {
                val vec = serverPlayerEntity.lookDirection
                val vec3d = Vec3(vec.x, 0.0, vec.z)
                serverPlayerEntity.modifyVelocity(vec3d.x, jumpShiftVelocity, vec3d.z, false)

                hgPlayer.playerData[kangaJumpStateKey] = KangaState.JumpedHorizontal
            } else if (kangaState != KangaState.JumpedVertical) {
                serverPlayerEntity.modifyVelocity(serverPlayerEntity.deltaMovement.x, jumpVelocity, serverPlayerEntity.deltaMovement.z, false)
                hgPlayer.playerData[kangaJumpStateKey] = KangaState.JumpedVertical
            }
        }
    }

    kitEvents {
        onMove { hgPlayer, kit ->
            if (hgPlayer.serverPlayer?.onGround() == true) hgPlayer.playerData[kangaJumpStateKey] = KangaState.Idle
        }

        onTakeDamage { player, kit, source, amount ->
            val hitByPlayer = when (source.entity) {
                is ServerPlayer -> source.entity as ServerPlayer
                is HGBot -> (source.entity as HGBot).serverPlayer
                else -> null
            }

            if (hitByPlayer == null) return@onTakeDamage amount

            player.playerData[KANGA_LAST_HIT_BY_KEY] = HitInfo(System.currentTimeMillis(), hitByPlayer)

            return@onTakeDamage amount
        }
    }
}

val ServerPlayer.lookDirection: Vec3
    get() {
        var v = Vec3.ZERO
        v = v.add(0.0, -sin(Math.toRadians(xRot.toDouble())), 0.0)
        val xz = cos(Math.toRadians(xRot.toDouble()))
        v = v.add(-xz * sin(Math.toRadians(yRot.toDouble())), 0.0, 0.0)
        v = v.add(0.0, 0.0, xz * cos(Math.toRadians(yRot.toDouble())))
        return v
    }