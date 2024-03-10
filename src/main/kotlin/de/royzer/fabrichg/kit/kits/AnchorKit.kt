package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.feast.Feast
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.mixins.world.CombatTrackerAcessor
import net.silkmc.silk.core.entity.modifyVelocity
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.phys.Vec3
import net.silkmc.silk.core.entity.posUnder
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.item.itemStack
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

val anvil = itemStack(Items.ANVIL) {
    enchant(Enchantments.BINDING_CURSE, 1)
    count = 1
}

val anchorKit = kit("Anchor") {
    kitSelectorItem = ItemStack(Items.ANVIL)
    description = "You and other players do not take knockback"

    onEnable { hgPlayer, kit, serverPlayer ->
        if (!Feast.started) {
            serverPlayer.inventory?.armor?.set(2, anvil.copy())
            serverPlayer.inventory?.armor?.set(3, anvil.copy())
        }
    }

    onDisable { hgPlayer, kit ->
        if (!Feast.started) {
            hgPlayer.serverPlayer?.inventory?.armor?.set(2, Items.AIR.defaultInstance)
            hgPlayer.serverPlayer?.inventory?.armor?.set(3, Items.AIR.defaultInstance)
        }
    }
}


fun onAnchorAttack(strength: Double, x: Double, z: Double, ci: CallbackInfo, attackedEntity: LivingEntity) {
    val attackingEntity = (attackedEntity.combatTracker as CombatTrackerAcessor).entries.lastOrNull()?.source?.entity ?: return
    if (attackedEntity !is ServerPlayer && attackingEntity !is ServerPlayer) return

    if (attackedEntity is ServerPlayer && attackingEntity !is ServerPlayer) {
        if (attackedEntity.hgPlayer.isAnchor) attackedEntity.applyAnchorKnockback(ci)
        return
    }

    val attacker = attackingEntity as? ServerPlayer ?: return
    if (attackedEntity !is ServerPlayer && attacker.hgPlayer.isAnchor) {
        attackedEntity.applyAnchorKnockback(ci)
        return
    }
    val attackedPlayer = attackedEntity as? ServerPlayer ?: return
    // attacked player is anchor
    if (attackedPlayer.hgPlayer.isAnchor) {
        if (attacker.hgPlayer.isNeo) return
        attackedPlayer.applyAnchorKnockback(ci)
    }

    // attacker is anchor
    if (attacker.hgPlayer.isAnchor) {
        if (attackedPlayer.hgPlayer.isNeo) return
        attackedPlayer.applyAnchorKnockback(ci)
    }

}

fun onAnchorAttacks(attackedEntity: LivingEntity, attackingEntity: LivingEntity, ci: CallbackInfo) {

}
fun onAnchorGetsAttacked(attackedEntity: LivingEntity, attackingEntity: LivingEntity, ci: CallbackInfo) {

}

private fun Entity.applyAnchorKnockback(ci: CallbackInfo) {
    world.playSound(null, posUnder, SoundEvents.ANVIL_FALL, SoundSource.BLOCKS, 1f, 1f)
    ci.cancel()
    deltaMovement = Vec3.ZERO
    modifyVelocity(0,-0.1,0, false)
}

fun onAnchorJoin(serverPlayer: ServerPlayer) {
    if (Feast.started) {
        if (serverPlayer.inventory?.armor?.get(2) == anvil.copy()) {
            serverPlayer.inventory?.armor?.set(2, Items.AIR.defaultInstance)
        }
        if (serverPlayer.inventory?.armor?.get(3) == anvil.copy()) {
            serverPlayer.inventory?.armor?.set(3, Items.AIR.defaultInstance)
        }
    }

}

private val HGPlayer.isAnchor get() = canUseKit(anchorKit)