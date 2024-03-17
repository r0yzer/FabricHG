package de.royzer.fabrichg.mixins.server.network;

import de.royzer.fabrichg.game.GamePhaseManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerGameMode.class)
abstract public class ServerPlayerInteractionManagerMixin {
    @Mutable
    @Final
    @Shadow
    protected final ServerPlayer player;

    protected ServerPlayerInteractionManagerMixin(ServerPlayer player) {
        this.player = player;
    }
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
//        ServerPlayerGameMode serverPlayerGameMode = (ServerPlayerGameMode) (Object) this;
        if (this.player.level().getBlockState(pos).getBlock() == Blocks.HONEY_BLOCK) {
            ci.cancel();
        }
    }
}
