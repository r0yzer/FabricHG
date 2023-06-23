package de.royzer.fabrichg.commands

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literalText
import net.minecraft.Util

val listCommand = command("list") {
    literal("skip") runs {
        GamePhaseManager.currentPhase.startNextPhase()
    }
}