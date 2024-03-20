package de.royzer.fabrichg.commands

import de.royzer.fabrichg.feast.Feast
import net.silkmc.silk.commands.command

val feastCommand = command("feast") {
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
}