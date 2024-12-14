package de.royzer.fabrichg.util.cloudnet

import de.royzer.fabrichg.game.GamePhaseManager
import eu.cloudnetservice.driver.inject.InjectionLayer
import eu.cloudnetservice.driver.provider.CloudServiceProvider
import net.silkmc.silk.core.logging.logger

object CloudNetManager {
    private val serviceProvider: CloudServiceProvider = InjectionLayer.ext().instance(CloudServiceProvider::class.java)

    fun stopCloudNetService() {
        val serviceName = GamePhaseManager.server.serverName
        logger().info("Trying to stop $serviceName...")
        val service = serviceProvider.serviceByName(serviceName)
        if (service == null) {
            logger().warn("CloudNet Service `$serviceName` was not found.")
            return
        }
        service.provider().stop()
    }
}
