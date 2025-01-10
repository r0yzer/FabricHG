package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks

object PressurePlateStorage {
    val pressurePlateOwners = mutableMapOf<BlockPos, ServerPlayer>()
}

val demomanKit = kit (name = "Demoman") {
    kitSelectorItem = ItemStack(Items.GRAVEL)
    description = "Stone Pressure Plate on top of gravel = boom."

    val gravelStack = ItemStack(Items.GRAVEL, 16)
    val pressurePlateStack = ItemStack(Items.STONE_PRESSURE_PLATE, 16)

    kitItem {
        itemStack = gravelStack.copy()

        onPlace { hgPlayer, kit, stack, blockPos, world ->
            if (world is ServerLevel) {
                world.setBlockAndUpdate(blockPos, Blocks.GRAVEL.defaultBlockState())

                stack.count -= 1

                if (stack.count <= 0) {
                    hgPlayer.serverPlayer?.inventory?.removeItem(stack)
                }
            }
        }
    }

    kitItem {
        itemStack = pressurePlateStack.copy()
        droppable = false

        onPlace { hgPlayer, kit, stack, blockPos, world ->
            if (world is ServerLevel) {

                world.setBlockAndUpdate(blockPos, Blocks.STONE_PRESSURE_PLATE.defaultBlockState())

                PressurePlateStorage.pressurePlateOwners[blockPos] = hgPlayer.serverPlayer!!

                stack.count -= 1
                if (stack.count <= 0) {
                    hgPlayer.serverPlayer?.inventory?.removeItem(stack)
                }
            }
        }
    }

}