package de.royzer.fabrichg.kit.events.kit

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.Kit
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingRecipe
import net.minecraft.world.item.crafting.RecipeHolder
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

    fun afterHitEntity(ignoreCooldown: Boolean = false, action: (HGPlayer, Kit, Entity) -> Unit) {
        kit.events.afterHitEntityAction = action
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

    fun onSoupEat(action: (HGPlayer, Kit, Item) -> Unit) {
        kit.events.soupEatAction = action
    }

    fun onKillPlayer(ignoreCooldown: Boolean = false, action: (HGPlayer, Kit, killed: ServerPlayer) -> Unit) {
        kit.events.killPlayerAction = action
        if (ignoreCooldown) {
            kit.events.noCooldownActions.add(action)
        }
    }

    fun onKillEntity(ignoreCooldown: Boolean = false, action: (HGPlayer, Kit, killed: Entity) -> Unit) {
        kit.events.killEntityAction = action
        if (ignoreCooldown) {
            kit.events.noCooldownActions.add(action)
        }
    }

    fun onSneak(ignoreCooldown: Boolean = false, action: (HGPlayer, Kit) -> Unit) {
        kit.events.sneakAction = action
        if (ignoreCooldown) {
            kit.events.noCooldownActions.add(action)
        }
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

    fun onLeftClick(action: (HGPlayer, Kit) -> Unit) {
        kit.events.leftClickAction = action
    }

    // true returnen wenn gecancelt werden soll
    fun onAttackedByPlayer(action: (HGPlayer, Kit, attacker: ServerPlayer) -> Boolean) {
        kit.events.attackedByPlayerAction = action
    }

    fun onCraft(ignoreCooldown: Boolean = false, action: (HGPlayer, ItemStack, RecipeHolder<CraftingRecipe>, Kit) -> Unit) {
        kit.events.craftAction = action
        if (ignoreCooldown) {
            kit.events.noCooldownActions.add(action)
        }
    }

    fun afterDamagePlayer(action: (HGPlayer, Kit, ServerPlayer) -> Unit) {
        kit.events.afterDamagePlayerAction = action
    }
}