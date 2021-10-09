package de.royzer.fabrichg.commands

import net.axay.fabrik.commands.command
import net.axay.fabrik.core.text.sendText

val pingCommand = command("ping") {
    runs {
        source.player.sendText(source.player.pingMilliseconds.toString())
    }
}