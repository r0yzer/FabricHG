package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import net.silkmc.silk.core.item.setCustomName
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potions
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setPotion
import net.silkmc.silk.core.task.mcCoroutineTask
import kotlin.time.Duration.Companion.milliseconds


//private val scoutPotion = PotionUtils.setPotion(
//    ItemStack(Items.SPLASH_POTION, 2),
//    Potion(MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120, 1))
//).also {
//    it.setCustomName{
//        text("Scout Potion")
//        color = 0x64F0FF
//    }
//    it.setLore(listOf(literalText("Kititem")))
//}

val scoutPotion = itemStack(Items.SPLASH_POTION) {
    setPotion(Potions.SWIFTNESS)
    count = 2
    setCustomName {
        text("Scout Potion")
        color = 0x64F0FF
    }
}

val scoutKit = kit("Scout") {

    val scoutJobKey = "${this.kit.name}JobKey"
    val scoutPotionPeriod = 5 * 60 * 1000L
    kitSelectorItem = scoutPotion.copy()

    description = "Recieve two speed potions every 5 minutes"

    kitItem {
        itemStack = scoutPotion.copy()
        droppable = false
    }

    onEnable { hgPlayer, kit, player ->
        if (hgPlayer.playerData[scoutJobKey] != null) return@onEnable
        val job = mcCoroutineTask(
            howOften = Long.MAX_VALUE,
            period = scoutPotionPeriod.milliseconds,
            delay = scoutPotionPeriod.milliseconds
        ) { hgPlayer.serverPlayer?.inventory?.add(scoutPotion.copy()) }
        job.start()
        hgPlayer.playerData[scoutJobKey] = job
    }
}
