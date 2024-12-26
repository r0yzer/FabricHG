package de.royzer.fabrichg.commands

import de.royzer.fabrichg.util.isOP
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.world.entity.player.Inventory
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literal
import net.silkmc.silk.igui.*
import net.silkmc.silk.igui.elements.GuiPlaceholder

fun Inventory.toGuiPage(): GuiPage {
    return GuiPage(
        "1",
        1,
        items.mapIndexed { index, itemStack -> (index) to GuiPlaceholder(itemStack.copy().guiIcon) }.toMap(),
        null,
        null
    )
}

val invCommand = command("inv") {
    argument("player", EntityArgument.player()) { player ->
        runs {
            val sourcePlayer = source.player ?: return@runs
            val serverPlayer = if (sourcePlayer.isOP()) player().findPlayers(source).first() else sourcePlayer

            val gui = igui(GuiType.NINE_BY_FOUR, "${serverPlayer.name.string}s inv".literal, 1) {
                page(1) { }
            }
            sourcePlayer.openGui(gui)

            var task: Job? = null
            task = mcCoroutineTask(howOften = Long.MAX_VALUE, period = 10.ticks) {
                if (gui.views[sourcePlayer] == null) {
                    task!!.cancel("HODEN_HAUER — gestern um 23:37 Uhrhab mich gerade eingeschissen")
                    return@mcCoroutineTask
                }
                gui.loadPage(serverPlayer.inventory.toGuiPage())
            }
        }

    }
}