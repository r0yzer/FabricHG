package de.royzer.fabrichg.command

import de.royzer.fabrichg.isFFA
import net.minecraft.commands.CommandSourceStack
import net.silkmc.silk.commands.LiteralCommandBuilder
import net.silkmc.silk.commands.RegistrableCommand
import net.silkmc.silk.commands.command
import net.silkmc.silk.commands.registration.setupRegistrationCallback

enum class FabricHGCommandApplication {
    HG,
    FFA,
    BOTH;

    fun applicable(ffa: Boolean): Boolean {
        if (this == BOTH) return true

        if (ffa && this == FFA) return true
        else if (!ffa && this == HG) return true

        return false
    }
}

data class FabricHGCommand(
    val application: FabricHGCommandApplication,
    val command: RegistrableCommand<CommandSourceStack>
) {
    fun register(): Boolean {
        if (application.applicable(isFFA)) {
            command.setupRegistrationCallback()
            return true
        }

        return false
    }
}

fun hgCommand(
    name: String,
    builder: LiteralCommandBuilder<CommandSourceStack>.() -> Unit
) = FabricHGCommand(FabricHGCommandApplication.HG, command(name, register=false, builder))

fun ffaCommand(
    name: String,
    builder: LiteralCommandBuilder<CommandSourceStack>.() -> Unit
) = FabricHGCommand(FabricHGCommandApplication.FFA, command(name, register=false, builder))

fun sharedCommand(
    name: String,
    builder: LiteralCommandBuilder<CommandSourceStack>.() -> Unit
) = FabricHGCommand(FabricHGCommandApplication.BOTH, command(name, register=false, builder))