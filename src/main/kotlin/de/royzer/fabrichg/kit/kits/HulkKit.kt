package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.mixins.entity.EntityAcessor
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.entity.vehicle.Minecart
import net.minecraft.world.item.Items
import net.silkmc.silk.core.logging.logInfo

val hulkKit = kit("Hulk") {
    kitSelectorItem = Items.PISTON.defaultInstance

    kitEvents {
        onRightClickEntity { hgPlayer, kit, clickedEntity ->
            val serverPlayer = hgPlayer.serverPlayer ?: return@onRightClickEntity
            if (clickedEntity is Boat || clickedEntity is Minecart) return@onRightClickEntity
            if (serverPlayer.mainHandItem.item == Items.AIR && serverPlayer.passengers.isEmpty())
                (serverPlayer as EntityAcessor).addEntityPassenger(clickedEntity)
        }
    }

    onDisable { hgPlayer, kit ->
        hgPlayer.serverPlayerOrException.ejectPassengers()
    }
}