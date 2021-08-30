package de.royzer.fabrichg.commands

import de.royzer.fabrichg.game.GamePhaseManager
import net.axay.fabrik.commands.command
import net.axay.fabrik.commands.simpleExecutes
import net.axay.fabrik.core.logging.logInfo


object DamageCommand {
    fun enable() {
        command("damage", true) {
            simpleExecutes {
                GamePhaseManager.server.isPvpEnabled = !GamePhaseManager.server.isPvpEnabled
            }
        }
    }
}

//val damageCommand = command("damage", true) {
//    simpleExecutes {
//            GameManager.server.isPvpEnabled = !GameManager.server.isPvpEnabled
//    }
//}