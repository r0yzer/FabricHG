package de.royzer.fabrichg.commands

import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.feast.Feast
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literalText

val feastCommand = command("feast") {
    alias("feaßt")
    literal("start") {
        requiresPermissionLevel(4)

        runs {
            if (!Feast.started)
                Feast.start()
        }

        argument<Int>("time") { timeLeft ->
            runs {
                if (!Feast.started) {
                    Feast.timeLeft = timeLeft()
                    Feast.start()
                }
            }
        }
    }

    runs {
        if (!Feast.started) {
            source.player?.sendSystemMessage(literalText("Feast hat noch nicht gestartet") {
                color = TEXT_GRAY
            })

            return@runs
        }

        source.player?.connection?.send(ClientboundSetDefaultSpawnPositionPacket(Feast.feastCenter, 0.0F))
    }
}