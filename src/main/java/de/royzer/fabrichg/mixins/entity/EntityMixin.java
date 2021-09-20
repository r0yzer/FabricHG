package de.royzer.fabrichg.mixins.entity;

import de.royzer.fabrichg.kit.KitItemKt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(
            method = "dropItem(Lnet/minecraft/item/ItemConvertible;)Lnet/minecraft/entity/ItemEntity;",
            at = @At("HEAD")
    )
    public void onDropItem(ItemConvertible item, CallbackInfoReturnable<ItemEntity> cir) {}

    @Inject(
            method = "interact",
            at = @At("HEAD")
    )
    public void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        KitItemKt.onClickAtEntity(player, hand, (Entity) (Object) this, cir);
    }
}
