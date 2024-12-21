package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.entity.vehicle.Minecart
import net.minecraft.world.item.Items
import net.silkmc.silk.core.entity.modifyVelocity
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.task.mcCoroutineTask

val hulkKit = kit("Rühl24.de") {
    kitSelectorItem = Items.PISTON.defaultInstance
    description = "Carry other players"

    cooldown = 3.0

    val velocity by property(0.9, "Launch velocity")

    kitEvents {
        onRightClickEntity { hgPlayer, _, clickedEntity ->
            val serverPlayer = hgPlayer.serverPlayer ?: return@onRightClickEntity
            if (clickedEntity is Boat || clickedEntity is Minecart) return@onRightClickEntity
            if (serverPlayer.mainHandItem.item == Items.AIR && serverPlayer.passengers.isEmpty()) {
                clickedEntity.startRiding(serverPlayer)
                if (clickedEntity is Player) {
                    serverPlayer.connection.send(ClientboundSetPassengersPacket(serverPlayer))
                }
            }
        }
        onLeftClick { hgPlayer, kit ->
            val serverPlayer = hgPlayer.serverPlayer ?: return@onLeftClick
            serverPlayer.passengers.forEach {
                it.stopRiding()
                serverPlayer.connection.send(ClientboundSetPassengersPacket(serverPlayer))
                mcCoroutineTask(delay = 1.ticks) {task ->
                    it.modifyVelocity(serverPlayer.lookAngle.scale(velocity))
                }
                hgPlayer.activateCooldown(kit)
            }
        }
    }

    onDisable { hgPlayer, kit ->
        hgPlayer.serverPlayer?.ejectPassengers()
    }
}