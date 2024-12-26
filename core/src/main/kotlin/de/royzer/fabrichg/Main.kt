package de.royzer.fabrichg

import de.royzer.fabrichg.kit.achievements.AchievementManager
import de.royzer.fabrichg.kit.kits
import de.royzer.fabrichg.stats.Stats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.core.BlockPos
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.world.level.GameRules

//val String.hgId get() = Identifier("fabrichg", this)

lateinit var server: DedicatedServer

val fabrichgScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

const val TEXT_BLUE = 0x00FFFF
const val TEXT_GRAY = 0x7A7A7A

fun initServer() {
    kits

    ServerLifecycleEvents.SERVER_STARTING.register {
        server = it as DedicatedServer
        Stats.init()
        AchievementManager.init()
    }

    ServerLifecycleEvents.SERVER_STARTED.register {
        it.overworld().dayTime = 0L
        it.gameRules.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, it)
        it.gameRules.getRule(GameRules.RULE_DAYLIGHT).set(false, it)
        it.gameRules.getRule(GameRules.RULE_DO_TRADER_SPAWNING).set(false, it)
        it.gameRules.getRule(GameRules.RULE_DO_PATROL_SPAWNING).set(false, it)
        it.gameRules.getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS).set(false, it)
        it.overworld().setDefaultSpawnPos(BlockPos.ZERO, 0f)
    }


}
