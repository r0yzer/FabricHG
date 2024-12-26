package de.royzer.fabrichg.mixins.item;

import de.royzer.fabrichg.kit.events.kititem.KitItemKt;
import de.royzer.fabrichg.kit.events.kititem.invoker.OnClickKitItemKt;
import de.royzer.fabrichg.kit.events.kititem.invoker.OnUseKitItemOnBlockKt;
import de.royzer.fabrichg.mixinskt.SoupHealingKt;
import de.royzer.fabrichg.mixinskt.Tracker;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(
            method = "use",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onUse(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack itemStack = (ItemStack) (Object) this;
        OnClickKitItemKt.onClick(user, itemStack, cir);
        SoupHealingKt.INSTANCE.onPotentialSoupUse(user, itemStack.getItem(), cir, world, hand);
        if (itemStack.getItem() == Items.COMPASS) {
            Tracker.INSTANCE.onTrackerUse(user, itemStack, cir, world, hand);
        }
    }

    @Inject(
            method = "useOn",
            at = @At("HEAD")
    )
    public void useOnBlock(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir){
        OnUseKitItemOnBlockKt.onUseOnBlock(context.getPlayer(), context);
    }

    @Inject(
            method = "hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onTrymacs(int amount, LivingEntity entity, EquipmentSlot slot, CallbackInfo ci) {
        ItemStack itemStack = (ItemStack) (Object) this;
        if (KitItemKt.isKitItem(itemStack)) {
            ci.cancel();
        }

    }
}
