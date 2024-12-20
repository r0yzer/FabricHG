package de.royzer.fabrichg.mixins.entity;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAcessor {
    @Invoker("addPassenger")
    void addEntityPassenger(Entity entity);

    @Invoker("unsetRemoved")
    void removeRemoval();
}
