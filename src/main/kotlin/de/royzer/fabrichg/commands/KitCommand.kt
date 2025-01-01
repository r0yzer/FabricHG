package de.royzer.fabrichg.commands

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.gui.kitSelectorGUI
import de.royzer.fabrichg.kit.kits
import de.royzer.fabrichg.kit.kits.backupKit
import de.royzer.fabrichg.kit.kits.noneKit
import de.royzer.fabrichg.settings.ConfigManager
import net.silkmc.silk.commands.*
import net.silkmc.silk.core.text.sendText
import net.silkmc.silk.igui.*

val kitCommand = command("kit") {
    runs {
        val index = 0

        val player = source.playerOrException
        if (GamePhaseManager.isNotInPvpPhase || player.hgPlayer.kits[index] == backupKit) {
            if (GamePhaseManager.currentPhaseType == PhaseType.INVINCIBILITY) {
                if (player.hgPlayer.kits[index] == backupKit || player.hgPlayer.kits[index] == noneKit)
                    player.openGui(
                        kitSelectorGUI(player, index + 1), 1
                    )
            } else player.openGui(
                kitSelectorGUI(player, index + 1), 1
            )
        }
        if (player.hasPermissions(PermissionLevel.OWNER.level)) {
            player.openGui(
                kitSelectorGUI(player, index + 1), 1
            )
        }
    }
    argument<Int>("index") { _index ->
        argument<String>("kit") { kitArg ->
            suggestList { kits.map { it.name } }
            runs {
                val index = _index() - 1

                if (index >= ConfigManager.gameSettings.kitAmount) {
                    return@runs
                }
                val player = source.playerOrException
                if (GamePhaseManager.isNotInPvpPhase
                    || (player.hgPlayer.canUseKit(backupKit) && player.hgPlayer.kits[index] == backupKit)
                    || player.hasPermissions(PermissionLevel.OWNER.level)
                ) {
                    if (GamePhaseManager.currentPhaseType == PhaseType.INVINCIBILITY) {
                        if (!(player.hgPlayer.hasKit(backupKit) || player.hgPlayer.hasKit(noneKit))) return@runs
                        // mit none kann man auch in invincibility
                        // backup auch ingame
                    }
                    val kitName = kitArg()
                    val kit = kits.firstOrNull { it.name.equals(kitName, true) }
                    if (kit != null) {
                        player.hgPlayer.setKit(kit, index)
                    } else {
                        player.sendText("Es konnte kein Kit mit dem Namen gefunden werden") { color = 0xFF0000 }
                    }
                }
            }
        }
        runs {
            val index = _index() - 1

            if (index >= ConfigManager.gameSettings.kitAmount) {
                return@runs
            }

            val player = source.playerOrException
            if (GamePhaseManager.isNotInPvpPhase
                || (player.hgPlayer.canUseKit(backupKit) && player.hgPlayer.kits[index] == backupKit)
                || player.hasPermissions(PermissionLevel.OWNER.level)
            ) {
                if (GamePhaseManager.currentPhaseType == PhaseType.INVINCIBILITY) {
                    if (player.hgPlayer.kits[index] == backupKit || player.hgPlayer.kits[index] == noneKit) {
                        player.openGui(
                            kitSelectorGUI(player, index + 1), 1
                        )
                    }
                } else player.openGui(
                    kitSelectorGUI(player, index + 1), 1
                )
            }
        }
    }
    argument<String>("kit") { kitArg ->
        val index = 0
        suggestList { kits.map { it.name } }
        runs {
            val player = source.playerOrException
            if (GamePhaseManager.isNotInPvpPhase || player.hgPlayer.canUseKit(backupKit) || player.hasPermissions(
                    PermissionLevel.OWNER.level
                )
            ) {
                if (GamePhaseManager.currentPhaseType == PhaseType.INVINCIBILITY)
                    if (!(player.hgPlayer.hasKit(backupKit) || player.hgPlayer.hasKit(noneKit))) return@runs
                val kitName = kitArg()
                val kit = kits.firstOrNull { it.name.equals(kitName, true) }
                if (kit != null) {
                    player.hgPlayer.setKit(kit, index)
                } else {
                    player.sendText("Es konnte kein Kit mit dem Namen gefunden werden") { color = 0xFF0000 }
                }
            }
        }
    }
    literal("info") runs {
        val player = source.playerOrException
        player.sendText {
            player.hgPlayer.kits.forEach {
                text(it.name) {
                    strikethrough = player.hgPlayer.kitsDisabled
                }
            }
            color = 0x00FF00
        }
    }
}

val kitinfoCommand = command("kitinfo") {
    argument<String>("kit") { kitArg ->
        suggestList { kits.map { it.name } }
        runs {
            val kitName = kitArg()
            val kit = kits.firstOrNull { it.name.equals(kitName, true) }
            source.player?.sendText {
                text(kit?.description ?: "Es konnte kein Kit mit dem Namen gefunden werden")
                color = TEXT_BLUE
            }
        }
    }
}