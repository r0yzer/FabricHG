package de.royzer.fabrichg.mixins.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAcessor {
    @Invoker("addPassenger")
    void addEntityPassenger(Entity entity);

    @Invoker("unsetRemoved")
    void removeRemoval();

    @Invoker("collide")
    Vec3 collisionVector(Vec3 vec);

    @Invoker("setSharedFlag")
    void setBrainBusting(int flag, boolean set);

}
