package de.royzer.fabrichg.mixinskt

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.game.PlayerList
import net.silkmc.silk.core.text.sendText
import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import kotlin.math.sqrt

object Tracker {
    fun onTrackerUse(playerEntity: Player, stack: ItemStack, cir: CallbackInfoReturnable<InteractionResultHolder<ItemStack>>, world: Level, hand: InteractionHand) {
        val player = playerEntity as? ServerPlayer ?: return
        if (stack.displayName.string == "[Tracker]") {
            val nearestPlayer = player.nearestPlayerInfo()?.first
            if (nearestPlayer != null) {
                val distance = player.nearestPlayerInfo()?.second?.toInt()
                player.sendText {
                    color = TEXT_GRAY
                    text(nearestPlayer.name.string) { color = TEXT_BLUE }
                    text(" ist ")
                    text(distance.toString()) { color = TEXT_BLUE }
                    text(" Blöcke entfernt")
                }
                player.connection.send(ClientboundSetDefaultSpawnPositionPacket(BlockPos(nearestPlayer.x.toInt(),
                    nearestPlayer.y.toInt(),
                    nearestPlayer.z.toInt()
                ),
                    0.0F
                ))
            } else {
                player.sendText("Es konnte kein Spieler gefunden werden") {
                    color = 0xFF4B4B
                }
            }
        }
    }

    private fun ServerPlayer.nearestPlayerInfo(): Pair<ServerPlayer, Double>? {
        val playerDistances: MutableMap<ServerPlayer, Double> = mutableMapOf()
        for (player in PlayerList.alivePlayers) {
            val otherPlayer = player.serverPlayer ?: continue
            val distance = sqrt(this.distanceToSqr(Vec3(otherPlayer.x, this.y, otherPlayer.z)))
            if (distance > 10) {
                playerDistances[otherPlayer] = distance
            }
        }
        return playerDistances.minByOrNull { it.value }?.toPair()
    }
}