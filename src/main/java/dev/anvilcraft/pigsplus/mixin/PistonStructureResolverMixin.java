package dev.anvilcraft.pigsplus.mixin;

import dev.anvilcraft.pigsplus.block.entity.PecisionMagneticPivotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PistonStructureResolver.class)
abstract class PistonStructureResolverMixin {

    @Shadow
    @Final
    private List<BlockPos> toPush;

    @Shadow
    @Final
    private Level level;

    @Shadow
    @Final
    private Direction pushDirection;

    @Inject(method = "resolve", at = @At("RETURN"))
    private void onPistonResolve(CallbackInfoReturnable<Boolean> cir) {
        if (level.isClientSide()) return;
        if (!cir.getReturnValue()) return;

        List<BlockPos> toPushBlocks = new ArrayList<>(toPush);
        PecisionMagneticPivotBlockEntity.onPistonMoveBlocks(level, toPushBlocks, pushDirection);
    }
}
