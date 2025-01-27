package de.royzer.fabrichg.util.luckperms

import de.royzer.fabrichg.util.luckperms.LuckPermsUtils.permissionData
import de.royzer.fabrichg.util.luckperms.LuckPermsUtils.permissionPrefix
import net.fabricmc.loader.api.FabricLoader
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User
import net.minecraft.server.level.ServerPlayer

object LuckPermsUtils {
    val permissionPrefix = "hg"
    val LuckPerms by lazy { LuckPermsProvider.get() }

    val ServerPlayer.lpUser: User
        get() = LuckPerms.getPlayerAdapter(ServerPlayer::class.java).getUser(this)

    val ServerPlayer.permissionData
        get() = LuckPerms.getPlayerAdapter(ServerPlayer::class.java).getPermissionData(this)
}

fun ServerPlayer.hasPermission(permission: String): Boolean {
    if (FabricLoader.getInstance().isDevelopmentEnvironment) return true
    return permissionData.checkPermission("$permissionPrefix.$permission").asBoolean()
}
