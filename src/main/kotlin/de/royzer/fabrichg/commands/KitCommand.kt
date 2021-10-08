package de.royzer.fabrichg.commands

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.gui.kitSelectorGUI
import de.royzer.fabrichg.kit.kits
import de.royzer.fabrichg.kit.kits.backupKit
import de.royzer.fabrichg.kit.kits.noneKit
import net.axay.fabrik.commands.*
import net.axay.fabrik.core.text.sendText
import net.axay.fabrik.igui.*

val kitCommand = command("kit") {
    runs {
        if (GamePhaseManager.isNotStarted || source.player.hgPlayer.canUseKit(backupKit)|| source.player.hasPermissionLevel(PermissionLevel.OWNER.level)) {
            if (GamePhaseManager.currentPhaseType == PhaseType.INVINCIBILITY) {
                if (source.player.hgPlayer.hasKit(backupKit) || source.player.hgPlayer.hasKit(noneKit))
                    this.source.player.openGui(
                        kitSelectorGUI(this.source.player), 1
                    )
            } else this.source.player.openGui(
                kitSelectorGUI(this.source.player), 1
            )
        }
    }
    argument<String>("kit") { kitArg ->
        suggestList { kits.map { it.name } }
        runs {
            if (GamePhaseManager.isNotStarted || source.player.hgPlayer.canUseKit(backupKit) || source.player.hasPermissionLevel(PermissionLevel.OWNER.level)) {
                if (GamePhaseManager.currentPhaseType == PhaseType.INVINCIBILITY)
                    if (!(source.player.hgPlayer.hasKit(backupKit) || source.player.hgPlayer.hasKit(noneKit))) return@runs
                val kitName = kitArg()
                val kit = kits.firstOrNull { it.name.equals(kitName, true) }
                if (kit != null) {
                    if (GamePhaseManager.isIngame) kit.onEnable?.invoke(source.player.hgPlayer, kit)
                    if (GamePhaseManager.currentPhaseType == PhaseType.INVINCIBILITY)
                        kit.kitItems.forEach { source.player.inventory.insertStack(it.itemStack.copy()) }
                    source.player.hgPlayer.kits[0] = kit
                }
                else
                    source.player.sendText("Es konnte kein Kit mit dem Namen gefunden werden") { color = 0xFF0000 }
            }
        }
    }
    literal("info") runs { source.player.sendText {
            source.player.hgPlayer.kits.forEach {
                text(it.name) {
                    strikethrough = source.player.hgPlayer.kitsDisabled
                }
            }
            color = 0x00FF00
        }
    }
}