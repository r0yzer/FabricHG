package de.royzer.fabrichg.data.hgplayer

import de.royzer.fabrichg.game.combatlog.maxCombatlogTime
import de.royzer.fabrichg.hgId
import kotlinx.serialization.Serializable
import net.axay.fabrik.persistence.compoundKey
import net.axay.fabrik.persistence.persistentCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

//@Serializable
//data class HGPlayerData(
//    var kills: Int = 0,
//    var status: HGPlayerStatus = HGPlayerStatus.ALIVE,
//    val kits: String = "nunja",
//    var combatlogTime: Int = maxCombatlogTime,
//)
//
//private val hgPlayerDataKey = compoundKey<HGPlayerData>("hgplayerdatakey".hgId)
//
//val ServerPlayerEntity.hgPlayerData: HGPlayerData
//    get() = persistentCompound[hgPlayerDataKey] ?: HGPlayerData().also { persistentCompound[hgPlayerDataKey] = it }