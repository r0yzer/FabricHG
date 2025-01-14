package de.royzer.fabrichg.mixins.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {
    @Shadow @Final protected Holder<ArmorMaterial> material;

    @Shadow @Final protected ArmorItem.Type type;

    @Inject(method = "getDefense", at = @At("RETURN"), cancellable = true)
    public void armorNerf(CallbackInfoReturnable<Integer> cir) {
        double nerfedDefense = Math.sqrt(this.material.value().getDefense(this.type));

        cir.setReturnValue((int) Math.ceil(nerfedDefense));
    }
}
