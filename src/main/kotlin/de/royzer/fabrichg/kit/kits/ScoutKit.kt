package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.kit.kit
import net.axay.fabrik.core.item.setCustomName
import net.axay.fabrik.core.item.setLore
import net.axay.fabrik.core.task.coroutineTask
import net.axay.fabrik.core.text.literalText
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionUtil

private val scoutPotion = PotionUtil.setPotion(
    ItemStack(Items.SPLASH_POTION, 2),
    Potion(StatusEffectInstance(StatusEffects.SPEED, 120, 1))
).also {
    it.setCustomName {
        text("Scout Potion")
        color = 0x64F0FF
    }
    it.setLore(listOf(literalText("Kititem")))
}

val scoutKit = kit("Scout") {
    val scoutJobKey = "${this.kit.name}JobKey"
    val scoutPotionPeriod = 5 * 60 * 1000L
    kitSelectorItem = scoutPotion
    addKitItem(scoutPotion, false)

    onEnable { hgPlayer, kit ->
        val job = coroutineTask(howOften = Long.MAX_VALUE, period = scoutPotionPeriod, delay = scoutPotionPeriod) {
            hgPlayer.serverPlayerEntity?.inventory?.insertStack(scoutPotion.copy())
        }
        job.start()
        hgPlayer.playerData[scoutJobKey] = job
    }
}
