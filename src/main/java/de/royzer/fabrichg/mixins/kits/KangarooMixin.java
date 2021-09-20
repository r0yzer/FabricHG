package de.royzer.fabrichg.mixins.kits;

import de.royzer.fabrichg.kit.KitItemKt;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketItem.class)
public class KangarooMixin {
    @Inject(
            method = "useOnBlock",
            at = @At("HEAD"),
            cancellable = true)
    public void onUseBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (KitItemKt.isKitItem(context.getStack())) {
            KitItemKt.onClick(context.getPlayer(), context.getStack());
            cir.setReturnValue(ActionResult.success(true));
        }
    }
}
