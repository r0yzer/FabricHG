package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import net.axay.fabrik.core.entity.modifyVelocity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Vec3d
import kotlin.math.cos
import kotlin.math.sin

val kangarooKit = kit("Kangaroo") {
    val canJumpKey = "${this.kit.name}canJump"
    kitSelectorItem = Items.FIREWORK_ROCKET.defaultStack
    kitItem {
        itemStack = kitSelectorItem
        onClick { hgPlayer, _ ->
            val serverPlayerEntity = hgPlayer.serverPlayerEntity ?: return@onClick
            if (hgPlayer.getPlayerData<Boolean>(canJumpKey) == false) return@onClick
            if (serverPlayerEntity.isSneaking) {
                val vec = serverPlayerEntity.direction
                val vec3d = Vec3d(vec.x, 0.0, vec.z).multiply(1.0)
                serverPlayerEntity.modifyVelocity(vec3d.x, 0.6, vec3d.z, false)
            } else {
                serverPlayerEntity.modifyVelocity(serverPlayerEntity.velocity.x, 0.9, serverPlayerEntity.velocity.z, false)
            }
            hgPlayer.playerData[canJumpKey] = false
        }
    }

    events {
        onMove { hgPlayer, kit ->
            if (hgPlayer.getPlayerData<Boolean>(canJumpKey) == false) {
                if (hgPlayer.serverPlayerEntity?.isOnGround == true) hgPlayer.playerData[canJumpKey] = true
            }
        }
    }
}

val ServerPlayerEntity.direction: Vec3d
    get() {
        var v = Vec3d.ZERO
        v = v.add(0.0, -sin(Math.toRadians(pitch.toDouble())), 0.0)
        val xz = cos(Math.toRadians(pitch.toDouble()))
        v = v.add(-xz * sin(Math.toRadians(yaw.toDouble())), 0.0, 0.0)
        v = v.add(0.0, 0.0, xz * cos(Math.toRadians(yaw.toDouble())))
        return v
    }