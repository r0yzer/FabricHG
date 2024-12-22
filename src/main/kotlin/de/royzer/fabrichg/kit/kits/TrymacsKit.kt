package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3
import net.silkmc.silk.core.entity.modifyVelocity

val trymacsKit = kit("Trymacs") {
    kitSelectorItem = Items.IRON_GOLEM_SPAWN_EGG.defaultInstance

    description = "You are trymacs"

    val horizontalLaunchStrength by property(1.1f, "horizontal launch strength")
    val verticalLaunchStrength by property(0.9f, "vertical launch strength")

    val trymacsSpeed by property(5.00f, "trymacs speed")

    kitEvents {
        afterHitEntity { player, kit, entity ->
            val playerLook = player.serverPlayer?.forward?.normalize() ?: return@afterHitEntity

            entity.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
            val launch = Vec3(playerLook.x * horizontalLaunchStrength, verticalLaunchStrength.toDouble(), playerLook.z * horizontalLaunchStrength)

            entity.modifyVelocity(launch)
        }

        onEnable { hgPlayer, kit, serverPlayer ->
            serverPlayer.getAttribute(Attributes.MAX_HEALTH)?.baseValue = 40.0
            serverPlayer.attributes.getInstance(Attributes.MOVEMENT_SPEED)?.baseValue = trymacsSpeed.toDouble() / 100
            serverPlayer.attributes.getInstance(Attributes.JUMP_STRENGTH)?.baseValue = 0.40
            serverPlayer.health += 20f
        }

        onDisable { hgPlayer, kit ->
            val serverPlayer = hgPlayer.serverPlayer ?: return@onDisable
            serverPlayer.attributes.getInstance(Attributes.MOVEMENT_SPEED)?.baseValue = 0.1
            serverPlayer.attributes.getInstance(Attributes.JUMP_STRENGTH)?.baseValue = 0.42
            serverPlayer.getAttribute(Attributes.MAX_HEALTH)?.baseValue = 20.0
        }
    }
}