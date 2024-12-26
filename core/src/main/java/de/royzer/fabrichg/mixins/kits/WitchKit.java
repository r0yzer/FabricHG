package de.royzer.fabrichg.mixins.kits;

import de.royzer.fabrichg.data.hgplayer.HGPlayer;
import de.royzer.fabrichg.data.hgplayer.HGPlayerKt;
import de.royzer.fabrichg.kit.kits.WitchKitKt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

//@Mixin(ThrownPotion.class)
//public abstract class WitchKit extends Entity {
//
//
////    public WitchKit(EntityType<?> entityType, Level level) {
////        super(entityType, level);
////    }
////
////    @ModifyVariable(method = "applySplash", at = @At("STORE"), ordinal = 1)
////    public List<LivingEntity> modifyList(List<LivingEntity> value) {
////        if (this.getTags().contains(WitchKitKt.getWitchPotionTag())) {
////            return value.stream()
////                    .filter(entity -> {
////                        HGPlayer hgPlayer = HGPlayerKt.getHgPlayer(entity);
////                        return hgPlayer != null && !hgPlayer.hasKit(WitchKitKt.getWitchKit());
////                    })
////                    .toList();
////        }
////        return value;
////    }
//
//}
