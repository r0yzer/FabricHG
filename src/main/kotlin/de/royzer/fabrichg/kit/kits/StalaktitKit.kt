package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.bots.HGBot
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import de.royzer.fabrichg.util.higherBy
import kotlinx.coroutines.cancel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.ChatType
import net.minecraft.network.chat.FilterMask
import net.minecraft.network.chat.LastSeenMessages
import net.minecraft.network.chat.SignedMessageBody
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.PointedDripstoneBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.DripstoneThickness
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literal
import java.time.Instant
import java.util.*
import kotlin.random.Random

fun createPointedDripstone(direction: Direction, dripstoneThickness: DripstoneThickness): BlockState {
    val dripstone = Blocks.POINTED_DRIPSTONE.defaultBlockState()
        .setValue(PointedDripstoneBlock.TIP_DIRECTION, direction)
        .setValue(PointedDripstoneBlock.THICKNESS, dripstoneThickness)
    return dripstone
}

fun createDripstonePosMap(original: BlockPos, overPlayer: Int): Map<BlockPos, BlockState> {
    return mapOf(
        original.higherBy(overPlayer) to createPointedDripstone(Direction.DOWN, DripstoneThickness.TIP_MERGE),
        original.higherBy(overPlayer - 1) to createPointedDripstone(Direction.DOWN, DripstoneThickness.TIP)
    )
}



fun ServerPlayer.sendChatPacket(from: HGPlayer, message: String, type: ResourceKey<ChatType>) { // junge buster bist du auf crack
    val body = SignedMessageBody.Packed(
        message,
        Instant.now(),
        Random.nextLong(0, Long.MAX_VALUE),
        LastSeenMessages.Packed(listOf())
    )
    val chatType = ChatType.bind(type, from.serverPlayer ?: return)

    connection.send(
        ClientboundPlayerChatPacket(
            from.uuid,
            0,
            null,
            body,
            null,
            FilterMask.PASS_THROUGH,
            chatType
        )
    )
}

val stalaktitKit = kit("Stalaktit") {
    kitSelectorItem = Items.POINTED_DRIPSTONE.defaultInstance
    cooldown = 25.0
    description = "Hänge von der Decke wie ein Stalaktit"

    val dripstoneHigherBy by property(15, "dripstone higher by")
    val dripstoneCount by property(35, "dripstone count")
    val dripstoneDelay by property(5, "dripstone spawn delay (ticks)")

    kitItem {
        itemStack = kitSelectorItem.copy()

        onClickAtEntity { hgPlayer, kit, entity, interactionHand ->
            val world = entity.world

            hgPlayer.activateCooldown(kit)

            val messageFromPlayer = "Hänge von der Decke wie ein Stalaktit"
            val messageToPlayer = "du hängst von der Decke wie ein Stalaktit"

            println("click at: $entity")

            val clickedPlayer = when (entity) {
                is ServerPlayer -> entity
                is HGBot -> entity.serverPlayer
                else -> null
            }

            if (clickedPlayer != null) {
                clickedPlayer.sendChatPacket(hgPlayer, messageToPlayer, ChatType.MSG_COMMAND_INCOMING)

                PlayerList.players.forEach { (_, hgPlayer) ->
                    if (hgPlayer.uuid == entity.uuid) return@forEach;
                    hgPlayer.serverPlayer?.sendChatPacket(clickedPlayer.hgPlayer, messageFromPlayer, ChatType.CHAT)
                }
            }

            mcCoroutineTask(howOften = dripstoneCount.toLong(), period = dripstoneDelay.ticks) {
                if (!entity.isAlive) {
                    this.coroutineContext.cancel()
                    return@mcCoroutineTask
                }
                Items.POINTED_DRIPSTONE
                val dripstoneHeight = dripstoneHigherBy
                val overPos = entity.onPos.higherBy(dripstoneHeight + 1)
                val blockMap = createDripstonePosMap(entity.onPos, dripstoneHeight)
                world.setBlockAndUpdate(overPos, Blocks.DRIPSTONE_BLOCK.defaultBlockState())
                blockMap.forEach { (pos, dripstone) ->
                    world.setBlockAndUpdate(pos, dripstone)
                }
                world.setBlockAndUpdate(overPos, Blocks.AIR.defaultBlockState())
            }
        }
    }
}