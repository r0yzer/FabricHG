package de.royzer.fabrichg.commands

import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.gui.gameOverviewGUI
import de.royzer.fabrichg.util.openSpectatorClickableGUI
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.Silk
import net.silkmc.silk.igui.openGui

val infoCommand = command("info") {
    runs {
        val player = source.player ?: return@runs
        player.openSpectatorClickableGUI(gameOverviewGUI(player), 1)
    }
}