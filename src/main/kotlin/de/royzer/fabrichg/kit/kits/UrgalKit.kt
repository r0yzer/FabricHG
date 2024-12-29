package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import net.minecraft.core.component.DataComponents
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.silkmc.silk.core.item.setCustomName
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.alchemy.Potions
import net.minecraft.world.item.component.CustomData
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setPotion
import net.silkmc.silk.nbt.dsl.nbtCompound
import java.util.*
import kotlin.time.Duration.Companion.minutes

val urgalTime = 1.minutes

val urgalPotion =
    Potion(
        MobEffectInstance(MobEffects.DAMAGE_BOOST, urgalTime.inWholeSeconds.toInt() * 20, 0)
    )


val urgalPotionItem = itemStack(Items.POTION) {
    set(
        DataComponents.POTION_CONTENTS, PotionContents(
            Optional.empty(), // wenn man da die potion reinmacht sucht er in der registry wenn der in der registry ist hat der client die nicht in der registry und wenn er mit fabric spielt gehts nicht danke frau merkel
            Optional.of(PotionContents.getColor(Potions.STRENGTH)),
            urgalPotion.effects
        )
    )

    count = 1

    setCustomName {
        text("Urgal Potion")
        color = 0x64F0FF
    }
}

val urgalKit = kit("Urgal") {
    kitSelectorItem = urgalPotionItem.copy()

    description = "Recieve a strength potion at the start of the round"

    kitItem {
        itemStack = urgalPotionItem.copy()
        droppable = false
    }
}
