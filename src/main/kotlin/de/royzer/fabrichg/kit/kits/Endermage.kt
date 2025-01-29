package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.cooldown.checkUsesForCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import net.minecraft.core.Vec3i
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.AABB
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literalText
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds


private const val ENDERMAGE_JOB_KEY = "endermageJob"

val endermageKit = kit("Endermage") {
    kitSelectorItem = Items.END_PORTAL_FRAME.defaultInstance

    description = "Teleport other players"

    cooldown = 15.0

    maxUses = 5

    val verticalIgnoreDistance by property(2, "Vertical ignore distance")
    val horizontalSearchDistance by property(1.5, "Horizontal search distance")

    kitItem {
        itemStack = kitSelectorItem

        onPlace { hgPlayer, kit, itemStack, _blockPos, level ->
            val blockPos = _blockPos.subtract(Vec3i(0, 1, 0))
            val serverPlayer = hgPlayer.serverPlayer ?: return@onPlace
            if (hgPlayer.getPlayerData<Job>(ENDERMAGE_JOB_KEY) != null) {
                serverPlayer.sendSystemMessage(literalText("Du suchst bereits") { color = TEXT_GRAY })
                return@onPlace
            }
            val before = level.getBlockState(blockPos)
            level.setBlock(blockPos, Blocks.END_PORTAL_FRAME.defaultBlockState(), 3)


            val task = mcCoroutineTask(howOften = 5 * 20) {
                val nearbyPlayers = serverPlayer.level()
                    .getEntitiesOfClass(ServerPlayer::class.java, AABB(blockPos).inflate(horizontalSearchDistance, 356.0, horizontalSearchDistance)).filter {
                        !it.hgPlayer.isNeo
                    }.filter {
                        !it.hgPlayer.hasKit(this@kit.kit)
                    }.filter {
                        abs(it.y - serverPlayer.y) > verticalIgnoreDistance
                    }
                if (nearbyPlayers.isNotEmpty()) {
                    val centerPos = blockPos.center
                    nearbyPlayers.forEach {
                        it.teleportTo(
                            level as ServerLevel, centerPos.x,
                            centerPos.y + 1, centerPos.z, 180f, 0f
                        )
                        it.invulnerableTime = 4 * 20
                    }
                    serverPlayer.teleportTo(
                        level as ServerLevel, centerPos.x,
                        centerPos.y + 1, centerPos.z, 180f, 0f
                    )
                    serverPlayer.invulnerableTime = 4 * 20
                    serverPlayer.sendSystemMessage(literalText {
                        text("Du hast ")
                        text("${nearbyPlayers.size}") { color = TEXT_BLUE }
                        text(" Spieler teleportiert")
                        color = TEXT_GRAY
                    })
                    level.setBlock(blockPos, before, 3)
                    hgPlayer.getPlayerData<Job>(ENDERMAGE_JOB_KEY)?.cancel("grad so n all you can eat buffet gegessen")
                    hgPlayer.playerData.remove(ENDERMAGE_JOB_KEY)
                }
            }

            mcCoroutineTask(delay = 5.seconds) {
                if (hgPlayer.getPlayerData<Job>(ENDERMAGE_JOB_KEY) != null) {
                    level.setBlock(blockPos, before, 3)
                    hgPlayer.getPlayerData<Job>(ENDERMAGE_JOB_KEY)?.cancel("die masse härten lassen")
                    hgPlayer.playerData.remove(ENDERMAGE_JOB_KEY)
                }
            }

            hgPlayer.playerData[ENDERMAGE_JOB_KEY] = task

            hgPlayer.checkUsesForCooldown(kit, maxUses!!)
        }
    }

    onDisable { hgPlayer, kit ->
        hgPlayer.getPlayerData<Job>(ENDERMAGE_JOB_KEY)?.cancel("300 pferde sa4 am steuer")
        hgPlayer.playerData.remove(ENDERMAGE_JOB_KEY)
    }
}