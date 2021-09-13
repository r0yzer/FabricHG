package de.royzer.fabrichg.kit

import de.royzer.fabrichg.kit.kits.AnchorKit
import de.royzer.fabrichg.kit.kits.MagmaKit
import de.royzer.fabrichg.kit.kits.NoneKit


abstract class Kit {
    abstract val name: String
}

val kits = listOf(
    MagmaKit,
    NoneKit,
    AnchorKit
)