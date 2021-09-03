package de.royzer.fabrichg.mixinskt

import de.royzer.fabrichg.game.PlayerList
import net.axay.fabrik.core.text.sendText
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import kotlin.math.sqrt

object Tracker {
    fun onTrackerUse(playerEntity: PlayerEntity, stack: ItemStack, cir: CallbackInfoReturnable<TypedActionResult<ItemStack>>, world: World, hand: Hand) {
        val player = playerEntity as? ServerPlayerEntity ?: return
        if (stack.name.string == "Tracker") {
            val nearestPlayer = player.nearestPlayerInfo()?.first
            if (nearestPlayer != null) {
                val distance = player.nearestPlayerInfo()?.second?.toInt()
                player.sendText {
                    color = 0x7A7A7A
                    text(nearestPlayer.name.string) { color = 0x00FFFF }
                    text(" ist ")
                    text(distance.toString()) { color = 0x00FFFF }
                    text(" Blöcke entfernt")
                }
                player.networkHandler.sendPacket(PlayerSpawnPositionS2CPacket(BlockPos(nearestPlayer.x, nearestPlayer.y, nearestPlayer.z), nearestPlayer.yaw))
            } else {
                player.sendText("Es konnte kein Spieler gefunden werden") {
                    color = 0xFF3219
                }
            }
        }
    }

    private fun ServerPlayerEntity.nearestPlayerInfo(): Pair<ServerPlayerEntity, Double>? {
        val playerDistances: MutableMap<ServerPlayerEntity, Double> = mutableMapOf()
        for (player in PlayerList.alivePlayers) {
            val otherPlayer = player.serverPlayerEntity ?: continue
            val distance = sqrt(this.squaredDistanceTo(Vec3d(otherPlayer.x, this.y, otherPlayer.z)))
            if (distance > 10) {
                playerDistances[otherPlayer] = distance
            }
        }
        return playerDistances.minByOrNull { it.value }?.toPair()
    }
}