package de.royzer.fabrichg.mixins.server.network;

import de.royzer.fabrichg.feast.Feast;
import de.royzer.fabrichg.game.GamePhaseManager;
import de.royzer.fabrichg.kit.events.kititem.invoker.OnDestroyBlockWithKitemKt;
import de.royzer.fabrichg.kit.kits.GladiatorKitKt;
import de.royzer.fabrichg.kit.kits.TurtleKitKt;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
abstract public class ServerPlayerInteractionManagerMixin {
    @Mutable
    @Final
    @Shadow
    protected final ServerPlayer player;

    protected ServerPlayerInteractionManagerMixin(ServerPlayer player) {
        this.player = player;
    }

    @Inject(
            method = "handleBlockBreakAction",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    public void onDestroyBlockAction(BlockPos pos, ServerboundPlayerActionPacket.Action action, Direction face, int maxBuildHeight, int sequence, CallbackInfo ci) {
        if (GamePhaseManager.INSTANCE.isBuildingForbidden()) {
            ci.cancel();
            return;
        }
        if (this.player.level().getBlockState(pos).getBlock() == Blocks.HONEY_BLOCK) { // das darf godtrouyvs nicht rausfinden
            ci.cancel();
            return;
        }
        if (this.player.level().getBlockState(pos).getBlock() == Blocks.GLASS && GladiatorKitKt.getGladiatorBlockPositions().contains(pos)) {
            ci.cancel();
            return;
        }
        if (this.player.level().getBlockState(pos).getBlock() == Blocks.GRASS_BLOCK && Feast.INSTANCE.getFeastBlockPositions().contains(pos)) {
            if (!Feast.INSTANCE.getStarted()) {
                ci.cancel();
            }
            return;
        }
        if (TurtleKitKt.getShellBlocks().contains(pos)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "destroyBlock",
            at = @At(
                    value = "HEAD"
            )
    )
    public void onDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        OnDestroyBlockWithKitemKt.onDestroyBlock(this.player, pos);
    }
}
