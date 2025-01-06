package de.royzer.fabrichg.commands

import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.util.giveOrDropItem
import de.royzer.fabrichg.util.tracker
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.sendText
import java.util.UUID

val gueldemuerCooldown = hashMapOf<UUID, Int>()

val gueldemuerCommand = command("güldemür") {
    runs {
        if (GamePhaseManager.isIngame) {
            val player = source.player ?: return@runs

            val cooldown = gueldemuerCooldown[player.uuid] ?: 0

            if (cooldown > 0) {
                gueldemuerCooldown[player.uuid] = cooldown - 1
                source.player?.sendText("You are on cooldown") { color = TEXT_GRAY }
                return@runs
            }

            gueldemuerCooldown[player.uuid] = 3
            source.player?.giveOrDropItem(tracker)
            gueldemuerCooldown[source.playerOrException.uuid]
        }
    }
}
