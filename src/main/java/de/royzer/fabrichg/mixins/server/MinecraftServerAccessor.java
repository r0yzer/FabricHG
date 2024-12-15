package de.royzer.fabrichg.mixins.server;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessor {
    @Accessor("levels")
    public Map<ResourceKey<Level>, ServerLevel> getLevelsMap();
}
