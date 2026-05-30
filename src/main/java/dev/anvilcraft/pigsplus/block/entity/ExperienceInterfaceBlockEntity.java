package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.block.ExperienceInterfaceBlock;
import dev.dubhe.anvilcraft.init.block.ModFluids;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public class ExperienceInterfaceBlockEntity extends BlockEntity {
    private static final int SEARCH_RADIUS = 2;
    private static final int EXPERIENCE_TO_LIQUID = 20;
    private static final int SCAN_COOLDOWN = 20;
    public static final int XP_PER_TIME = 100;

    private int xp_target = 30;
    private int cooldown = 0;
    private boolean working = false;
    @Getter
    private int time = 0;

    public ExperienceInterfaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        cooldown = input.getIntOr("Cooldown", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Cooldown", cooldown);
    }

    public void clientTick() {
        time++;
    }

    public void tick(Level level) {
        if (level.isClientSide()) return;

        if (working) {
            cooldown = 1;
            working = false;
        }
        // 扫描周围玩家
        if (--cooldown <= 0) {
            cooldown = SCAN_COOLDOWN;
            AABB searchBox = new AABB(getBlockPos()).inflate(SEARCH_RADIUS);
            for (Player player : level.getEntitiesOfClass(Player.class, searchBox)) {
                if (!player.isRemoved() && !player.isSpectator()) {
                    int playerLevel = player.experienceLevel;
                    if (playerLevel >= xp_target) {
                        absorbPlayerExperience(player);
                    } else {
                        releaseExperienceToPlayer(player);
                    }
                }
            }
        }
    }

    private void absorbPlayerExperience(Player player) {
        int totalExp;
        if (player.experienceLevel > xp_target) {
            totalExp = XP_PER_TIME;
        } else {
            totalExp = Math.min(XP_PER_TIME, (int) (player.experienceProgress * player.getXpNeededForNextLevel()));
        }

        if (totalExp <= 0) return;
        int liquidExp = totalExp * EXPERIENCE_TO_LIQUID;
        try (Transaction transaction = Transaction.openRoot()) {
            ResourceHandler<FluidResource> handler = getHandler();
            if (handler == null) return;

            int accepted;
            try (Transaction nested = Transaction.open(transaction)) {
                accepted = handler.insert(FluidResource.of(ModFluids.EXP_FLUID), liquidExp, nested);
            }
            if (accepted <= 0) return;

            liquidExp = accepted;
            if (liquidExp % EXPERIENCE_TO_LIQUID != 0) {
                liquidExp -= accepted % EXPERIENCE_TO_LIQUID; // 确保只接受完整的经验单位
                handler.insert(FluidResource.of(ModFluids.EXP_FLUID), liquidExp, transaction);
            }

            player.giveExperiencePoints(-liquidExp / EXPERIENCE_TO_LIQUID);
            transaction.commit();
        }
    }

    private void releaseExperienceToPlayer(Player player) {
        int liquidExp = XP_PER_TIME * EXPERIENCE_TO_LIQUID;
        try (Transaction transaction = Transaction.openRoot()) {
            ResourceHandler<FluidResource> handler = getHandler();
            if (handler == null) return;

            int accepted;
            try (Transaction nested = Transaction.open(transaction)) {
                accepted = handler.extract(FluidResource.of(ModFluids.EXP_FLUID), liquidExp, nested);
            }
            if (accepted <= 0) return;

            liquidExp = accepted;
            if (liquidExp % EXPERIENCE_TO_LIQUID != 0) {
                liquidExp -= accepted % EXPERIENCE_TO_LIQUID; // 确保只接受完整的经验单位
                handler.extract(FluidResource.of(ModFluids.EXP_FLUID), liquidExp, transaction);
            }

            player.giveExperiencePoints(liquidExp / EXPERIENCE_TO_LIQUID);
            transaction.commit();
        }
    }

    private @Nullable ResourceHandler<FluidResource> getHandler() {
        if (level == null) return null;
        Direction direction = getBlockState().getValue(ExperienceInterfaceBlock.FACING);
        BlockPos targetPos = getBlockPos().relative(direction.getOpposite());
        return level.getCapability(
            Capabilities.Fluid.BLOCK,
            targetPos,
            level.getBlockState(targetPos),
            level.getBlockEntity(targetPos),
            direction
        );
    }
}

