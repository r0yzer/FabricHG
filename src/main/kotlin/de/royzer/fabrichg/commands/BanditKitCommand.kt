package de.royzer.fabrichg.commands

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.cooldown.hasCooldown
import de.royzer.fabrichg.kit.cooldown.sendCooldown
import de.royzer.fabrichg.kit.kits.BANDIT_KITS_KEY
import de.royzer.fabrichg.kit.kits.BANDIT_KIT_KEY
import de.royzer.fabrichg.kit.kits.banditKit
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.sendText

val banditKitCommand = command("banditKit") {
    argument<String>("kit") {_kit ->
        runs {
            val kit = _kit()

            val hgPlayer = source.player?.hgPlayer ?: return@runs

            val hasCooldown = hgPlayer.hasCooldown(banditKit)

            if (hasCooldown) {
                hgPlayer.sendCooldown(banditKit)
                return@runs
            }
            val banditKits = hgPlayer.getPlayerData<List<Kit>>(BANDIT_KITS_KEY) ?: listOf()

            if (!hgPlayer.canUseKit(banditKit)) return@runs

            val kitToGive = banditKits.firstOrNull { it.name == kit } ?: return@runs

            if (hgPlayer.allKits.contains(kitToGive)) {
                source.player?.sendText {
                    text("You already have this kit")
                    color = TEXT_GRAY
                    bold = true
                }
                return@runs
            }

            kitToGive.onEnable?.invoke(hgPlayer, kitToGive, source.playerOrException)
            hgPlayer.giveKitItems(kitToGive)

            hgPlayer.getPlayerData<Kit>(BANDIT_KIT_KEY)?.let { it.onDisable?.invoke(hgPlayer, it) }
            hgPlayer.playerData[BANDIT_KIT_KEY] = kitToGive

            source.player?.sendText {
                text("You are now playing as ") {
                    color = TEXT_GRAY
                }
                text(kitToGive.name) {
                    color = TEXT_BLUE
                    bold = true
                }
            }

            hgPlayer.activateCooldown(banditKit)
        }
    }
}