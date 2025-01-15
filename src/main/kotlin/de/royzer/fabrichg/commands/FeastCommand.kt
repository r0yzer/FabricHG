package de.royzer.fabrichg.commands

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.feast.Feast
import kotlinx.coroutines.cancel
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText

val feastCommand = command("feast") {
    alias("feaßt")
    literal("start") {
        requiresPermissionLevel(4)

        runs {
            if (!Feast.spawned)
                Feast.spawn()
        }

        argument<Int>("time") { timeLeft ->
            runs {
                if (!Feast.spawned) {
                    Feast.timeLeft = timeLeft()
                    Feast.spawn()
                }
            }
        }
    }

    literal("skip") {
        requiresPermissionLevel(4)

        runs {
            if (!Feast.spawned) {
                source.player?.sendText("das feaßt hat noch nicht gestartet. Helm") { color = TEXT_GRAY }
                return@runs
            }
            if (Feast.started) {
                source.player?.sendText("das feaßt ist gespawned. Helm") { color = TEXT_GRAY }
                return@runs
            }

            Feast.feastJob?.cancel(":3")
            Feast.feastJob = null
            Feast.start()
        }
    }

    runs {
        if (!Feast.spawned) {
            source.player?.sendSystemMessage(literalText("The feast has not spawned yet") {
                color = TEXT_GRAY
            })

            return@runs
        }

        source.player?.connection?.send(ClientboundSetDefaultSpawnPositionPacket(Feast.feastCenter, 0.0F))
        source.player?.sendText {
            text("Your tracker is now pointing at ") { color = TEXT_GRAY }
            text("Feast") {
                color = TEXT_BLUE
                bold = true
            }
        }
    }
}