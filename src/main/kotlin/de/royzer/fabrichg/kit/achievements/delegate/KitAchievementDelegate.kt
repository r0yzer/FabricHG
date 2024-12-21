package de.royzer.fabrichg.kit.achievements.delegate

import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.KitBuilder
import de.royzer.fabrichg.kit.achievements.KitAchievement
import de.royzer.fabrichg.kit.achievements.KitAchievementLevel
import kotlin.reflect.KProperty

class KitAchievementDelegate(
    val kit: Kit,
    val name: String,
    val levels: List<KitAchievementLevel>
) {
    private val achievement = object : KitAchievement() {
        override val id: Int = this@KitAchievementDelegate.kit.name.hashCode() + this@KitAchievementDelegate.name.hashCode()
        override val name: String = this@KitAchievementDelegate.name
        override val levels: List<KitAchievementLevel> = this@KitAchievementDelegate.levels
        override val kit: Kit = this@KitAchievementDelegate.kit
    }

    init {
        kit.achievements.add(achievement)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): KitAchievement {
        return achievement
    }
}

class AchievementBuilder(val kit: Kit, val name: String) {
    private val levels = mutableListOf<KitAchievementLevel>()

    fun level(max: Int) = levels.add(KitAchievementLevel(levels.size + 1, max))

    fun build(): KitAchievementDelegate {
        return KitAchievementDelegate(kit, name, levels)
    }
}

fun KitBuilder.achievement(name: String, builder: AchievementBuilder.() -> Unit): KitAchievementDelegate {
    return AchievementBuilder(kit, name).apply(builder).build()
}