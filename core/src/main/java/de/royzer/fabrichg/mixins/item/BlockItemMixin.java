package de.royzer.fabrichg.mixins.item;

import de.royzer.fabrichg.game.FabricHGRuntimeKt;
import de.royzer.fabrichg.kit.events.kititem.invoker.OnPlaceKitItemKt;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Inject(
            method = "place",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onPlaceBlock(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (FabricHGRuntimeKt.fabricHGRuntime.isBlockPlacingForbidden()) {
            cir.setReturnValue(InteractionResult.PASS);
        }
        OnPlaceKitItemKt.onPlace(context, cir);
    }
}
