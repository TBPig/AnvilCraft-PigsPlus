package dev.anvilcraft.pigsplus.fluid;

import dev.anvilcraft.pigsplus.init.AddonBlockTags;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.CONFIG;

public abstract class VoidAcidFluid extends BaseFlowingFluid {
    protected VoidAcidFluid(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isRandomlyTicking() {
        return true;
    }

    @Override
    public void randomTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
        super.randomTick(level, pos, state, random);

        if (level.isClientSide()) return;
        if (!CONFIG.voidAcidDestroy) return;

        for (Direction direction : Direction.values()) {
            BlockPos targetPos = pos.relative(direction);
            BlockState targetState = level.getBlockState(targetPos);

            if (targetState.is(AddonBlockTags.VOID_ACID_IMMUNE)) continue;
            float hardness = targetState.getDestroySpeed(level, targetPos);
            if (hardness <= 0.0f) continue;

            float probability = 1f / (hardness + 2.0f);
            if (random.nextFloat() < probability) {
                level.destroyBlock(targetPos, false);
                if (random.nextFloat() < 0.1f) {
                    level.setBlock(targetPos, ModBlocks.VOID_STONE.getDefaultState(), Block.UPDATE_ALL);
                } else if (random.nextFloat() < 0.01f) {
                    level.setBlock(targetPos, ModBlocks.VOID_MATTER_BLOCK.getDefaultState(), Block.UPDATE_ALL);
                }
            }
        }
    }

    public static class Flowing extends VoidAcidFluid {
        public Flowing(Properties properties) {
            super(properties);
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends VoidAcidFluid {
        public Source(Properties properties) {
            super(properties);
        }

        public int getAmount(FluidState state) {
            return 8;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }
}
