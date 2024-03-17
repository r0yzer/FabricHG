package de.royzer.fabrichg.mixins.server.network;

import de.royzer.fabrichg.game.GamePhaseManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerInteractionManagerMixin {
//    @Inject(
//            method = "useItem",
//            at = @At(
//                    value = "RETURN"
//            ),
//            cancellable = true
//    )
//    public void onUse(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
//        System.out.println("hod");
//        if (GamePhaseManager.INSTANCE.isBuildingForbidden()) {
//            cir.setReturnValue(InteractionResult.FAIL);
//        }
//    }
//    @Inject(
//            method = "useItemOn",
//            at = @At(
//                    value = "RETURN"
//            ),
//            cancellable = true
//    )
//    public void onUseOn(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
//        System.out.println("hod22");
//        if (GamePhaseManager.INSTANCE.isBuildingForbidden()) {
//            cir.setReturnValue(InteractionResult.FAIL);
//        }
//    }

    @Inject(
            method = "handleBlockBreakAction",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    public void onDestroyBlock(BlockPos pos, ServerboundPlayerActionPacket.Action action, Direction face, int maxBuildHeight, int sequence, CallbackInfo ci) {
        if (GamePhaseManager.INSTANCE.isBuildingForbidden()) {
            ci.cancel();
        }
    }
}
