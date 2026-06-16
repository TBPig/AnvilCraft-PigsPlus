package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.block.ExperienceInterfaceBlock;
import dev.dubhe.anvilcraft.init.block.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
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

import java.util.List;

public class ExperienceInterfaceBlockEntity extends BlockEntity {
    private static final int SEARCH_RADIUS = 2;
    private static final int EXPERIENCE_TO_LIQUID = 20;
    private static final int SCAN_COOLDOWN = 20;
    public static final int XP_PER_TIME = 100;

    private int xp_target = 30;
    private int cooldown = 0;
    private boolean working = false;

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

    public void tick(Level level) {
        if (!(level instanceof ServerLevel)) return;

        if (working) {
            cooldown = 1;
            working = false;
        }

        if (--cooldown > 0) return;
        cooldown = SCAN_COOLDOWN;

        if (getHandler() == null) return;

        // 扫描周围玩家
        AABB searchBox = new AABB(getBlockPos()).inflate(SEARCH_RADIUS);
        List<Player> players = level.getEntitiesOfClass(Player.class, searchBox);
        for (Player player : players) {
            if (!player.isRemoved() && !player.isSpectator()) {
                int playerLevel = player.experienceLevel;
                if (playerLevel >= xp_target) {
                    absorbPlayerExperience(player, getHandler());
                } else {
                    releaseExperienceToPlayer(player, getHandler());
                }
            }
        }
    }

    private void absorbPlayerExperience(Player player, ResourceHandler<FluidResource> handler) {
        int totalExp;
        if (player.experienceLevel > xp_target) {
            totalExp = XP_PER_TIME;
        } else {
            totalExp = Math.min(XP_PER_TIME, (int) (player.experienceProgress * player.getXpNeededForNextLevel()));
        }

        if (totalExp <= 0) return;
        int liquidExp = totalExp * EXPERIENCE_TO_LIQUID;
        int accepted;
        try (Transaction transaction = Transaction.openRoot()) {
            accepted = handler.insert(FluidResource.of(ModFluids.EXP_FLUID), liquidExp, transaction);
        }
        if (accepted <= 0) return;

        liquidExp = accepted - accepted % EXPERIENCE_TO_LIQUID; // 确保只接受完整的经验单位

        try (Transaction transaction = Transaction.openRoot()) {
            handler.insert(FluidResource.of(ModFluids.EXP_FLUID), liquidExp, transaction);
            transaction.commit();
        }

        player.giveExperiencePoints(-liquidExp / EXPERIENCE_TO_LIQUID);
    }

    private void releaseExperienceToPlayer(Player player, ResourceHandler<FluidResource> handler) {

        int liquidExp = XP_PER_TIME * EXPERIENCE_TO_LIQUID;
        int accepted;
        try (Transaction transaction = Transaction.openRoot()) {
            accepted = handler.extract(FluidResource.of(ModFluids.EXP_FLUID), liquidExp, transaction);
        }
        if (accepted <= 0) return;

        liquidExp = accepted - accepted % EXPERIENCE_TO_LIQUID; // 确保只接受完整的经验单位

        try (Transaction transaction = Transaction.openRoot()) {
            handler.extract(FluidResource.of(ModFluids.EXP_FLUID), liquidExp, transaction);
            transaction.commit();
        }

        player.giveExperiencePoints(liquidExp / EXPERIENCE_TO_LIQUID);
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

