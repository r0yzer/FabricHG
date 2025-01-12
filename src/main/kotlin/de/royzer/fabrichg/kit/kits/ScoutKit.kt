package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import de.royzer.fabrichg.util.forceGiveItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
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
    count = 1
    setCustomName {
        text("Scout Potion")
        color = 0x64F0FF
    }
}

val scoutKit = kit("Scout") {

    val scoutJobKey = "${this.kit.name}JobKey"
    val scoutPotionPeriod by property(5.0, "scout potion period (minutes)")
    kitSelectorItem = scoutPotion.copy()

    description = "Recieve a speed potions every 5 minutes"

    kitItem {
        itemStack = scoutPotion.copy()
        droppable = false
    }

    onEnable { hgPlayer, kit, player ->
        if (hgPlayer.playerData[scoutJobKey] != null) return@onEnable
        val job = mcCoroutineTask(
            howOften = Long.MAX_VALUE,
            period = (scoutPotionPeriod * 60 * 1000L).milliseconds,
            delay = (scoutPotionPeriod * 60 * 1000L).milliseconds
        ) { hgPlayer.serverPlayer?.forceGiveItem(scoutPotion.copy()) }
        job.start()
        hgPlayer.playerData[scoutJobKey] = job
    }

    onDisable { hgPlayer, kit ->
        hgPlayer.getPlayerData<Job>(scoutJobKey)?.cancel("ich the oat killer")
        hgPlayer.playerData.remove(scoutJobKey)
    }
}
