package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.cooldown.checkUsesForCooldown
import de.royzer.fabrichg.kit.kit
import net.minecraft.block.Blocks
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos

val blinkKit = kit("Blink") {
    val maxUses = 5
    val blinkDistance = 4.0
    kitSelectorItem = Items.NETHER_STAR.defaultStack
    cooldown = 15.0

    kitItem {
        itemStack = kitSelectorItem
        onClick { hgPlayer, kit ->
            val player = hgPlayer.serverPlayerEntity ?: return@onClick
            hgPlayer.checkUsesForCooldown(kit, maxUses)
            val newPos = player.pos.add(player.direction.normalize().multiply(blinkDistance))
            hgPlayer.serverPlayerEntity?.teleport(
                newPos.x, newPos.y, newPos.z
            )
            player.world.setBlockState(BlockPos(player.pos.subtract(0.0,1.0,0.0)), Blocks.OAK_LEAVES.defaultState)
            hgPlayer.serverPlayerEntity?.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, 100F, 100F)
        }
    }
}