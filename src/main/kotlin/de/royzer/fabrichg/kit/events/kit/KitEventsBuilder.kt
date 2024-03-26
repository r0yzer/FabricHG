package de.royzer.fabrichg.kit.events.kit

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.Kit
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.EntityHitResult

class KitEventsBuilder(val kit: Kit) {
    fun onHitPlayer(action: (HGPlayer, Kit, ServerPlayer) -> Unit) {
        kit.events.hitPlayerAction = action
    }

    fun onHitEntity(ignoreCooldown: Boolean = false, action: (HGPlayer, Kit, Entity) -> Unit) {
        kit.events.hitEntityAction = action
        if (ignoreCooldown) {
            kit.events.noCooldownActions.add(action)
        }
    }

    fun onMove(action: (HGPlayer, Kit) -> Unit) {
        kit.events.moveAction = action
    }

    fun onRightClickEntity(action: (HGPlayer, Kit, Entity) -> Unit) {
        kit.events.rightClickEntityAction = action
    }

    fun onDrink(action: (HGPlayer, Kit, ItemStack) -> Unit) {
        kit.events.drinkAction = action
    }

    fun onSoupEat(action: (HGPlayer, Kit) -> Unit) {
        kit.events.soupEatAction = action
    }

    fun onKillPlayer(action: (HGPlayer, Kit, killed: ServerPlayer) -> Unit) {
        kit.events.killPlayerAction = action
    }

    fun onSneak(action: (HGPlayer, Kit) -> Unit) {
        kit.events.sneakAction = action
    }

    fun onHitByProjectile(action: (hgPlayer: HGPlayer, kit: Kit, entityHitResult: EntityHitResult, projectileEntity: Projectile) -> Unit) {
        kit.events.projectileHitByAction = action
    }

    fun onHitProjectile(action: (hgPlayer: HGPlayer, kit: Kit, entityHitResult: EntityHitResult, projectileEntity: Projectile) -> Unit) {
        kit.events.projectileHitAction = action
    }

    fun onTick(action: (HGPlayer, Kit) -> Unit) {
        kit.events.tickAction = action
    }

    /**
     * action returns the new damage to deal to the player
     * das muss man evtl anders gestalten aber keine ahnung wie
     */
    fun onTakeDamage(action: (player: HGPlayer, kit: Kit, source: DamageSource, amount: Float) -> Float) {
        kit.events.takeDamageAction = action
    }
}