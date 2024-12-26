package de.royzer.fabrichg.mixins.server.network;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.ServerStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mixin(ServerStatus.class)
class ServerStatusMixin {
    @Unique
    private GameProfile text(String text) {
        return new GameProfile(UUID.randomUUID(), text);
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    public void init(
            Component motd,
            Optional<ServerStatus.Players> players,
            Optional<ServerStatus.Version> version,
            Optional<ServerStatus.Favicon> favicon,
            boolean secureChat,
            CallbackInfo ci
    ) {
        players.ifPresent((playerList) -> {
            ServerStatus.Version serverVersion = version.orElse(new ServerStatus.Version("brain", 187));
            List<GameProfile> playersSample = playerList.sample();
            List<GameProfile> playersCopy = List.copyOf(playersSample);

            playersSample.clear();

            playersSample.add(text(" §b Fabric HG"));
            playersSample.add(text(" §2 in der " + serverVersion.name()));
            playersSample.add(text(" §b currently under §4Development"));

            if (playersCopy.size() < 5 && !playersCopy.isEmpty()) {
                playersSample.add(text(" §e Diese " + playersCopy.size() + " Spieler sind online!"));
                playersCopy.forEach((onlinePlayer) -> playersSample.add(new GameProfile(UUID.randomUUID(), " - §5 " + onlinePlayer.getName())));
            } else {
                playersSample.add(text(" §e Aktuell " + playersCopy.size() + " Spieler online!"));
            }
        });
    }
}
