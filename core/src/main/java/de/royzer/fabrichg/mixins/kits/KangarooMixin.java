package de.royzer.fabrichg.mixins.kits;

import de.royzer.fabrichg.kit.events.kititem.KitItemKt;
import de.royzer.fabrichg.kit.events.kititem.invoker.OnClickKitItemKt;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketItem.class)
public class KangarooMixin {
    @Inject(
            method = "useOn",
            at = @At("HEAD"),
            cancellable = true)
    public void onUseBlock(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (KitItemKt.isKitItem(context.getItemInHand())) {
            OnClickKitItemKt.onClick(context.getPlayer(), context.getItemInHand());
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
