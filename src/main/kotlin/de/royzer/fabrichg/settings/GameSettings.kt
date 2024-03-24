package de.royzer.fabrichg.settings


object GameSettings {
    var minifeastEnabled = true
    var mushroomCowNerf = true

    override fun toString(): String {
        return "Minifeast enabled: $minifeastEnabled\n" +
                "Mushroom cow nerf enabled: $mushroomCowNerf\n"
    }
}