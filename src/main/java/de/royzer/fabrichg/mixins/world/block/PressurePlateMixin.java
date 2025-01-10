package de.royzer.fabrichg.mixins.world.block;

import de.royzer.fabrichg.kit.kits.DemomanKitKt;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.server.level.ServerPlayer;
import de.royzer.fabrichg.kit.kits.PressurePlateStorage;
import de.royzer.fabrichg.data.hgplayer.HGPlayer;
import de.royzer.fabrichg.data.hgplayer.HGPlayerKt;

@Mixin(BasePressurePlateBlock.class)
public class PressurePlateMixin {
    @Inject(method = "checkPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/core/BlockPos;)V"))
    public void stepOnPressurePlate(Entity entity, Level level, BlockPos pos, BlockState state, int currentSignal, CallbackInfo ci) {
        if (currentSignal != 0) return;


        ServerPlayer player = PressurePlateStorage.INSTANCE.getPressurePlateOwners().get(pos);
        if (player == null || player.getUUID() == entity.getUUID()) {
            return;
        };


        HGPlayer hgPlayer = HGPlayerKt.getHgPlayer(player);
        if (hgPlayer.canUseKit(DemomanKitKt.getDemomanKit())) {
            BlockPos belowPos = pos.below();

            if (level.getBlockState(belowPos).getBlock() == Blocks.GRAVEL) {
                level.explode(player, entity.damageSources().playerAttack(player),
                        null, entity.position(), 7f, false,
                        Level.ExplosionInteraction.TNT
                );
            }
        }
    }
}