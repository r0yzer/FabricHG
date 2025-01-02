package de.royzer.fabrichg.networking

import de.royzer.fabrichg.util.cloudnet.CloudNetManager
import net.minecraft.resources.ResourceLocation
import net.silkmc.silk.core.Silk
import net.silkmc.silk.network.packet.s2cPacket


object PluginMessaging {
    private val HG_STARTING_PAKET = s2cPacket<String>(ResourceLocation.fromNamespaceAndPath("fabrichg", "starting"))

    fun sendHgStartingMessage() {
        val player = Silk.players.randomOrNull() ?: return
        val message = "${CloudNetManager.getServiceName()}:30"
        HG_STARTING_PAKET.send(message, player)
    }
}
