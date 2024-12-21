package de.royzer.fabrichg.kit.events.kit

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.cooldown.hasCooldown
import de.royzer.fabrichg.kit.cooldown.sendCooldown
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.EntityHitResult


class KitEvents(
    var hitPlayerAction: ((HGPlayer, Kit, ServerPlayer) -> Unit)? = null,
    var hitEntityAction: ((HGPlayer, Kit, Entity) -> Unit)? = null,
    var afterHitEntityAction: ((HGPlayer, Kit, Entity) -> Unit)? = null,
    var moveAction: ((HGPlayer, Kit) -> Unit)? = null,
    var rightClickEntityAction: ((HGPlayer, Kit, clickedEntity: Entity) -> Unit)? = null,
    var drinkAction: ((HGPlayer, Kit, ItemStack) -> Unit)? = null,
    var soupEatAction: ((HGPlayer, Kit) -> Unit)? = null,
    var killPlayerAction: ((HGPlayer, Kit, ServerPlayer) -> Unit)? = null,
    var sneakAction: ((HGPlayer, Kit) -> Unit)? = null,
    var projectileHitByAction: ((hgPlayer: HGPlayer, kit: Kit, entityHitResult: EntityHitResult, projectileEntity: Projectile) -> Unit)? = null,
    var projectileHitAction: ((hgPlayer: HGPlayer, kit: Kit, entityHitResult: EntityHitResult, projectileEntity: Projectile) -> Unit)? = null,
    var tickAction: ((hgPlayer: HGPlayer, kit: Kit) -> Unit)? = null, // should ignore cooldown (only for checking things like in pnatom)
    var takeDamageAction: ((hgPlayer: HGPlayer, kit: Kit, source: DamageSource, amount: Float) -> Float)? = null,
    val noCooldownActions: MutableList<Any> = mutableListOf()
)

fun HGPlayer.invokeKitAction(kit: Kit, sendCooldown: Boolean = true, ignoreCooldown: Boolean = false, action: () -> Unit) {
    if (this.canUseKit(kit, ignoreCooldown)) {
        action.invoke()
    } else if (this.hasCooldown(kit)) {
        if (sendCooldown) {
            this.serverPlayer?.sendCooldown(kit)
        }
    }
}