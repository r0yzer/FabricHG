package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.cooldown.checkUsesForCooldown
import de.royzer.fabrichg.kit.kit
import net.silkmc.silk.core.entity.pos
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.silkmc.silk.core.entity.blockPos

val blinkKit = kit("Blink") {
    maxUses = 5
    val blinkDistance = 4
    kitSelectorItem = Items.NETHER_STAR.defaultInstance
    cooldown = 15.0
    description = "Teleport in the direction you are looking"

    kitItem {
        itemStack = kitSelectorItem
        onClick { hgPlayer, kit ->
            val player = hgPlayer.serverPlayer ?: return@onClick
            hgPlayer.checkUsesForCooldown(kit, maxUses!!)
            val p = player.lookDirection.normalize().scale(blinkDistance.toDouble())
            val newPos = player.pos.add(p.x, p.y, p.z)
            player.teleportTo(
                newPos.x, newPos.y, newPos.z
            )
            player.level().setBlockAndUpdate(BlockPos(player.blockPos.subtract(Vec3i(0,1,0))), Blocks.OAK_LEAVES.defaultBlockState())
            player.playNotifySound(SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.MASTER, 100F, 100F)
        }
    }
}