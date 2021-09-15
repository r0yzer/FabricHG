package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.kit.KitItem
import de.royzer.fabrichg.kit.cooldown.startCooldown
import de.royzer.fabrichg.kit.kit
import net.minecraft.block.Material
import net.minecraft.entity.Entity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffectType
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import kotlin.random.Random

val magmaKit = kit("Magma") {
    kitSelectorItem = ItemStack(Items.MAGMA_BLOCK)
    addKitItem(
        KitItem(ItemStack(Items.MAGMA_BLOCK)) { hgPlayer, kit ->
            hgPlayer.serverPlayerEntity?.addStatusEffect(StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 100))
            hgPlayer.startCooldown(kit)
        }
    )
    cooldown = 3.5
}

fun onAttackEntity(target: Entity, serverPlayerEntity: ServerPlayerEntity) {
    if (serverPlayerEntity.hgPlayer.canUseKit(magmaKit, true))
        if (Random.nextInt(4) == 3)
            target.fireTicks = 40
}