package de.royzer.fabrichg.util.cloudnet

import eu.cloudnetservice.driver.inject.InjectionLayer
import eu.cloudnetservice.driver.provider.CloudServiceProvider
import eu.cloudnetservice.wrapper.configuration.WrapperConfiguration

object CloudNetManager {
    private val serviceProvider: CloudServiceProvider = InjectionLayer.ext().instance(CloudServiceProvider::class.java)
    private val wrapperConfig: WrapperConfiguration = InjectionLayer.ext().instance(WrapperConfiguration::class.java)

    fun stopCloudNetService() {
        wrapperConfig.serviceInfoSnapshot().provider().stop()
    }
}
