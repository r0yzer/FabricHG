package de.royzer.fabrichg.stats

import kotlinx.serialization.Serializable
import org.dizitart.no2.repository.annotations.Id

@Serializable
data class Stats(@Id val uuid: String, val kills: Int = 0, val deaths: Int = 0, val wins: Int = 0)