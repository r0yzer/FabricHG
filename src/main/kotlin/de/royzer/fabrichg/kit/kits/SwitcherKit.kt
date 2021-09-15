package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.KitItem
import de.royzer.fabrichg.kit.kit
import net.axay.fabrik.core.item.itemStack
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.projectile.thrown.SnowballEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.hit.EntityHitResult
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

val switcherKit = kit("Switcher") {
    kitSelectorItem = itemStack(Items.SNOWBALL) {}
    addKitItem(itemStack(Items.SNOWBALL) {
            count = 16
        }, true
    )
}

fun switcherOnEntityHit(entityHitResult: EntityHitResult, ci: CallbackInfo, snowballEntity: SnowballEntity) {
    val owner = snowballEntity.owner as? ServerPlayerEntity ?: return
    val hitEntity = entityHitResult.entity
    if (owner.hgPlayer.canUseKit(switcherKit)) {
        val hitEntityPos = hitEntity.pos
        val ownerPos = owner.pos
        owner.teleport(hitEntityPos.x, hitEntityPos.y, hitEntityPos.z)
        hitEntity.teleport(ownerPos.x, ownerPos.y, ownerPos.z)
        hitEntity.damage(DamageSource.player(owner), 0.1F)
    }
}