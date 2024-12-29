package de.royzer.fabrichg.mixins.server.network;

import de.royzer.fabrichg.mixinskt.ServerGamePacketListenerMixinKt;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerMixin {
    @Shadow public ServerPlayer player;

    @Inject(method = "handleContainerClick", at = @At("HEAD"), cancellable = true)
    public void onContainerClick(ServerboundContainerClickPacket packet, CallbackInfo ci) {
        ServerGamePacketListenerMixinKt.INSTANCE.onClickSlot(packet, player, ci);
    }
}
