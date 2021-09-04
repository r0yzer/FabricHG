package de.royzer.fabrichg.mixins;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VanillaLayeredBiomeSource.class)
public class VanillaLayeredBiomeSourceMixin {
    @Inject(
            method = "<init>",
            at = @At("HEAD")
    )
    public void init(long seed, boolean legacyBiomeInitLayer, boolean largeBiomes, Registry<Biome> biomeRegistry, CallbackInfo ci) {
        VanillaLayeredBiomeSourceAccessor.setBiomes(
                ImmutableList.of(
                        BiomeKeys.MUSHROOM_FIELDS,
                        BiomeKeys.MUSHROOM_FIELD_SHORE,
                        BiomeKeys.TAIGA,
                        BiomeKeys.TAIGA_HILLS,
                        BiomeKeys.TAIGA_MOUNTAINS,
                        BiomeKeys.GIANT_TREE_TAIGA,
                        BiomeKeys.GIANT_SPRUCE_TAIGA,
                        BiomeKeys.PLAINS,
                        BiomeKeys.FOREST,
                        BiomeKeys.DARK_FOREST
                )
        );
    }
}
