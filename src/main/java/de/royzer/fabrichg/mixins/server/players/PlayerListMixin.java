package de.royzer.fabrichg.mixins.server.players;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Mutable
    @Final
    @Shadow private final MinecraftServer server;

    public PlayerListMixin(MinecraftServer server) {
        this.server = server;
    }

    @Inject(
            method = "placeNewPlayer",
            at = @At("HEAD")
    )
    public void placePlayer(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        GameProfile gameProfile = player.getGameProfile();
        GameProfileCache gameProfileCache = this.server.getProfileCache();
        if (gameProfileCache != null) {
            gameProfileCache.add(gameProfile);
        }
    }

    @Inject(method = "verifyChatTrusted", at = @At("RETURN"), cancellable = true)
    public void trust(PlayerChatMessage message, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
