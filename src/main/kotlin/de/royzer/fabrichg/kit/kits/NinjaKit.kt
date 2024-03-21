package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.mixins.world.CombatTrackerAcessor
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.task.mcCoroutineTask
import kotlin.time.Duration.Companion.seconds

val ninjaKit = kit("Ninja") {
    kitSelectorItem = Items.INK_SAC.defaultInstance

    cooldown = 14.0

    description = "Teleport behind your enemies"

    val lastHittedKey = "ninjaLastHitted"

    kitEvents {
        onSneak { hgPlayer, kit ->
            val serverPlayer = hgPlayer.serverPlayer ?: return@onSneak
            val lastHitted = hgPlayer.getPlayerData<ServerPlayer>(lastHittedKey) ?: return@onSneak
            val angle = Vec3(lastHitted.lookAngle.x, 0.0, lastHitted.lookAngle.z)
            val pos = lastHitted.pos.subtract(angle.normalize())

            serverPlayer.teleportTo(lastHitted.world as ServerLevel, pos.x, pos.y, pos.z, lastHitted.yRot, lastHitted.xRot)
            hgPlayer.activateCooldown(kit)
        }

        onHitPlayer { hgPlayer, kit, serverPlayer ->
            hgPlayer.playerData[lastHittedKey] = serverPlayer
            mcCoroutineTask(delay = 15.seconds) {
                if (hgPlayer.getPlayerData<ServerPlayer>(lastHittedKey) == serverPlayer) {
                    hgPlayer.playerData.remove(lastHittedKey)
                }
            }
        }
    }
}