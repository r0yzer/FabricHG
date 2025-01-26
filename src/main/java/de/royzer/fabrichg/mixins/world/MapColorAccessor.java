package de.royzer.fabrichg.mixins.world;

import net.minecraft.world.level.material.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MapColor.class)
public interface MapColorAccessor {

    @Accessor("MATERIAL_COLORS")
    static MapColor[] getMaterialColors() {
        return null;
    }
}
