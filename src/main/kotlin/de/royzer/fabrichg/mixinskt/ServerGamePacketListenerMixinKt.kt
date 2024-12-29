package de.royzer.fabrichg.mixinskt

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.phase.PhaseType
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ChestMenu
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object ServerGamePacketListenerMixinKt {
    fun onClickSlot(packet: ServerboundContainerClickPacket, player: ServerPlayer, ci: CallbackInfo) {
        if (GamePhaseManager.currentPhaseType != PhaseType.LOBBY) return

        if (player.containerMenu is ChestMenu) return
        ci.cancel()
        player.containerMenu.sendAllDataToRemote()
    }
}