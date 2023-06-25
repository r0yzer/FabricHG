package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import net.silkmc.silk.core.entity.modifyVelocity
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.sin

val kangarooKit = kit("Kangaroo") {
    val canJumpKey = "${this.kit.name}canJump"
    kitSelectorItem = Items.FIREWORK_ROCKET.defaultInstance
    kitItem {
        itemStack = kitSelectorItem
        onClick { hgPlayer, _ ->
            val serverPlayerEntity = hgPlayer.serverPlayer ?: return@onClick
            if (hgPlayer.getPlayerData<Boolean>(canJumpKey) == false) return@onClick
            if (serverPlayerEntity.isShiftKeyDown) {
                val vec = serverPlayerEntity.lookDirection
                val vec3d = Vec3(vec.x, 0.0, vec.z)
                serverPlayerEntity.modifyVelocity(vec3d.x, 0.6, vec3d.z, false)
            } else {
                serverPlayerEntity.modifyVelocity(serverPlayerEntity.deltaMovement.x, 0.9, serverPlayerEntity.deltaMovement.z, false)
            }
            hgPlayer.playerData[canJumpKey] = false
        }
    }

    kitEvents {
        onMove { hgPlayer, kit ->
            if (hgPlayer.getPlayerData<Boolean>(canJumpKey) == false) {
                if (hgPlayer.serverPlayer?.onGround() == true) hgPlayer.playerData[canJumpKey] = true
            }
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