package de.royzer.fabrichg.mixins.block;

//import net.minecraft.block.BlockState;
//import net.minecraft.block.Blocks;
//import net.minecraft.block.MushroomPlantBlock;
//import net.minecraft.block.PlantBlock;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.WorldView;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Mixin(MushroomPlantBlock.class)
//public class MushroomPlantBlockMixin extends PlantBlock {
//    protected MushroomPlantBlockMixin(Settings settings) {
//        super(settings);
//    }
//    @Inject(
//            method = "canPlaceAt",
//            at = @At("RETURN"),
//            cancellable = true
//    )
//    public void canPlace(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
//        BlockPos blockPos = pos.down();
//        BlockState blockState = world.getBlockState(blockPos);
//        if (blockState.getBlock().equals(Blocks.STONE)) cir.setReturnValue(false);
//        cir.setReturnValue(this.canPlantOnTop(blockState, world, blockPos));
//    }
//}
