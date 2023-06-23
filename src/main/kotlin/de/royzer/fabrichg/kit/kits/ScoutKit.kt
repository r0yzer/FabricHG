package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import net.silkmc.silk.core.item.setCustomName
import net.silkmc.silk.core.item.setLore
import net.silkmc.silk.core.task.coroutineTask
import net.silkmc.silk.core.text.literalText
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.alchemy.PotionUtils

private val scoutPotion = PotionUtils.setPotion(
    ItemStack(Items.SPLASH_POTION, 2),
    Potion(MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120, 1))
).also {
    it.setCustomName{
        text("Scout Potion")
        color = 0x64F0FF
    }
    it.setLore(listOf(literalText("Kititem")))
}

val scoutKit = kit("Scout") {
    val scoutJobKey = "${this.kit.name}JobKey"
    val scoutPotionPeriod = 5 * 60 * 1000L
    kitSelectorItem = scoutPotion

    kitItem {
        itemStack = scoutPotion
        droppable = false
    }

    onEnable { hgPlayer, kit ->
        val job = coroutineTask(howOften = Long.MAX_VALUE, period = scoutPotionPeriod, delay = scoutPotionPeriod) {
            hgPlayer.serverPlayerEntity?.inventory?.add(scoutPotion.copy())
        }
        job.start()
        hgPlayer.playerData[scoutJobKey] = job
    }
}
