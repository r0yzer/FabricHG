package de.royzer.fabrichg.kit.kits

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
import net.silkmc.silk.core.logging.logInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

val anvil = itemStack(Items.ANVIL) {
    enchant(Enchantments.BINDING_CURSE, 1)
    count = 1
}

val anchorKit = kit("Anchor") {
    kitSelectorItem = ItemStack(Items.ANVIL)

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


fun onAnchorKnockback(strength: Double, x: Double, z: Double, ci: CallbackInfo, attackedEntity: LivingEntity) {
    val attackingEntity = (attackedEntity.combatTracker as CombatTrackerAcessor).entries.lastOrNull()?.source?.entity ?: return
    val attacker = attackingEntity as? ServerPlayer ?: return
    if (attackedEntity !is ServerPlayer) {
        attackedEntity.applyAnchorKnockback(ci)
        return
    }
    val attackedPlayer = attackedEntity as ServerPlayer
    // attacked player is anchor
    if (attackedPlayer.hgPlayer.canUseKit(anchorKit)) {
        if (attacker.hgPlayer.canUseKit(neoKit)) return
        attacker.applyAnchorKnockback(ci)
    }

    // attacker is anchor
    if (attacker.hgPlayer.canUseKit(anchorKit)) {
        if (attackedPlayer.hgPlayer.canUseKit(neoKit)) return
        attackedPlayer.applyAnchorKnockback(ci)
    }

}

private fun Entity.applyAnchorKnockback(ci: CallbackInfo) {
    world.playLocalSound(posUnder, SoundEvents.ANVIL_FALL, SoundSource.BLOCKS, 100f, 100f, false)
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