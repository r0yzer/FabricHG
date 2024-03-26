package de.royzer.fabrichg.bots.skin

import kotlinx.serialization.Serializable

@Serializable
data class SkinRequestData(
    val id: String,
    val name: String,
    val properties: ArrayList<Properties>,
    val profileActions: ArrayList<String>
){
    @Serializable
    data class Properties(
        val name: String,
        val value: String,
        val signature: String
    )
}
