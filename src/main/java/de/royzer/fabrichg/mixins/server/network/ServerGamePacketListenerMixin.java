package de.royzer.fabrichg.mixins.server.network;

import de.royzer.fabrichg.commands.TeamChatCommandKt;
import de.royzer.fabrichg.data.hgplayer.HGPlayer;
import de.royzer.fabrichg.data.hgplayer.HGPlayerKt;
import de.royzer.fabrichg.game.teams.HGTeam;
import de.royzer.fabrichg.game.teams.TeamsKt;
import de.royzer.fabrichg.kit.kits.StalaktitKitKt;
import de.royzer.fabrichg.mixinskt.ServerGamePacketListenerMixinKt;
import de.royzer.fabrichg.mixinskt.SoupHealingKt;
import de.royzer.fabrichg.settings.ConfigManager;
import de.royzer.fabrichg.settings.SoupMode;
import net.minecraft.network.Connection;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.*;
import net.minecraft.util.FutureChain;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerMixin
        extends ServerCommonPacketListenerImpl
        implements ServerGamePacketListener,
        ServerPlayerConnection,
        TickablePacketListener {
    @Shadow public ServerPlayer player;

    public ServerGamePacketListenerMixin(MinecraftServer server, Connection connection, CommonListenerCookie cookie) {
        super(server, connection, cookie);
    }

    @Shadow protected abstract void tryHandleChat(String message, Runnable handler);

    @Shadow public abstract ServerPlayer getPlayer();

    @Shadow protected abstract CompletableFuture<FilteredText> filterTextPacket(String text);

    @Shadow @Final private FutureChain chatMessageChain;

    @Shadow protected abstract void broadcastChatMessage(PlayerChatMessage message);

    @Inject(method = "handleContainerClick", at = @At("HEAD"), cancellable = true)
    public void onContainerClick(ServerboundContainerClickPacket packet, CallbackInfo ci) {
        ServerGamePacketListenerMixinKt.INSTANCE.onClickSlot(packet, player, ci);
    }

    // neimals geht das auf hglabor
    @Inject(method = "handleChat", at = @At("HEAD"), cancellable = true)
    public void teamChat(ServerboundChatPacket packet, CallbackInfo ci) {
        HGPlayer hgPlayer = HGPlayerKt.getHgPlayer(player);
        HGTeam hgTeam = TeamsKt.getHgTeam(hgPlayer);

        if (hgTeam == null) return;
        if (!hgPlayer.getTeamChat()) return;

        TeamChatCommandKt.sendTeamMessage(player, packet.message());

        ci.cancel();
    }


    @Inject(method = "handlePlayerAction", at = @At("HEAD"), cancellable = true)
    public void handleStartBlockBreak(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
        if (ConfigManager.INSTANCE.getGameSettings().getSoupMode() != SoupMode.EatAndDestroyBlock) return;
        if (packet.getAction() != ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) return;

        Player player = getPlayer();
        ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);

        boolean usedSoup = SoupHealingKt.INSTANCE.potentialUseSoup(player, itemInHand.getItem());

        if (usedSoup) {
            player.setItemInHand(InteractionHand.MAIN_HAND, Items.BOWL.getDefaultInstance());
        }
    }
}
