package de.royzer.fabrichg.mixins.server.network;

import de.royzer.fabrichg.game.GamePhaseManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class ServerPlayerInteractionManagerMixin {
    @Inject(
            method = "useItemOn",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    public void onUse(LocalPlayer player, ClientLevel world, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (GamePhaseManager.INSTANCE.isBuildingForbidden()) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
