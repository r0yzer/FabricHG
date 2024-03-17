package de.royzer.fabrichg.mixins.server.network;

import de.royzer.fabrichg.game.phase.phases.LobbyPhase;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {

//    @Inject(
//            method = "handleMovePlayer",
//            at = @At("HEAD")
//    )
//    public void stopMove(ServerboundMovePlayerPacket packet, CallbackInfo ci) {
//        if (LobbyPhase.INSTANCE.isStarting()) {
////            ServerPlayer serverPlayer = ((ServerGamePacketListenerImpl) (Object) this).getPlayer();
////            serverPlayer.connection.send(new ClientboundPlayerPositionPacket(0.0, 0.0, 0.0, 0.0f, 0.0f, RelativeMovement.ALL, 1));
//
//        }
//    }
}
