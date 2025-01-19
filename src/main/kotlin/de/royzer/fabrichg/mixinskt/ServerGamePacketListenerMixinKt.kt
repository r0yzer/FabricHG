package de.royzer.fabrichg.mixinskt

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.kit.events.kititem.isKitItem
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.UUID

object ServerGamePacketListenerMixinKt {
    val itemsInMouse = hashMapOf<UUID, ItemStack>()

    fun onClickSlot(packet: ServerboundContainerClickPacket, player: ServerPlayer, ci: CallbackInfo) {
        if (GamePhaseManager.currentPhaseType == PhaseType.LOBBY) {
            if (player.containerMenu is ChestMenu) return
            ci.cancel()
            player.containerMenu.sendAllDataToRemote()
        } else {
            if (packet.containerId == 0) return handleInventoryClick(packet, player, ci)

            if (packet.carriedItem.isKitItem) {
                ci.cancel()
                player.containerMenu.sendAllDataToRemote()
                return
            }

            packet.changedSlots.forEach { (_, item) ->
                if (item.isKitItem) {
                    ci.cancel()
                    player.containerMenu.sendAllDataToRemote()
                }
            }
        }
    }

    fun onCloseContainer(packet: ServerboundContainerClosePacket, player: ServerPlayer, ci: CallbackInfo) {
        itemsInMouse.remove(player.uuid)
    }

    private fun handleInventoryClick(packet: ServerboundContainerClickPacket, player: ServerPlayer, ci: CallbackInfo) {
        val item = packet.carriedItem

        if (item.item == Items.AIR) itemsInMouse.remove(player.uuid)
        else itemsInMouse[player.uuid] = item
    }
}

/**
 * welches item der spieler in der hand hat geht nur im inventar wenn er eine kiste offen hat gehts nicht
 * wenn man das braucht implementier hs
 */
val ServerPlayer.itemInMouse: ItemStack?
    get() = ServerGamePacketListenerMixinKt.itemsInMouse[uuid]