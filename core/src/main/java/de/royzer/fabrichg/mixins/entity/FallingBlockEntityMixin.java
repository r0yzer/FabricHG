package de.royzer.fabrichg.mixins.entity;

import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin {
    @Shadow private BlockState blockState;
    @Shadow private float fallDamagePerDistance;

    @Inject(
            method = "setHurtsEntities",
            at = @At("TAIL")
    )
    public void setHurtsEntities(float fallDamagePerDistance, int fallDamageMax, CallbackInfo ci) {
        if (blockState.getBlock() == Blocks.POINTED_DRIPSTONE) {
            this.fallDamagePerDistance = fallDamagePerDistance / 6;
        }
    }
}
