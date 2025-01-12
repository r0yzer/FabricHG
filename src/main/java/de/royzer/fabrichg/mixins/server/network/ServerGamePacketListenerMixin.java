package de.royzer.fabrichg.mixins.server.network;

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
    public void stalaktitChat(ServerboundChatPacket packet, CallbackInfo ci) {
        if (packet.salt() != StalaktitKitKt.STALAKTIT_MESSAGE_SALT) return;

        this.tryHandleChat(packet.message(), () -> {
            PlayerChatMessage playerChatMessage = PlayerChatMessage.unsigned(getPlayer().getUUID(), packet.message());

            CompletableFuture<FilteredText> filteredTextPacket = this.filterTextPacket(playerChatMessage.signedContent());
            Component decoratedMessage = this.server.getChatDecorator().decorate(this.player, playerChatMessage.decoratedContent());
            this.chatMessageChain.append(filteredTextPacket, filteredText -> {
                PlayerChatMessage playerChatMessageWithUnsignedContent = playerChatMessage.withUnsignedContent(decoratedMessage).filter(filteredText.mask());
                this.broadcastChatMessage(playerChatMessageWithUnsignedContent);
            });
        });


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
            ci.cancel();
        }
    }
}
