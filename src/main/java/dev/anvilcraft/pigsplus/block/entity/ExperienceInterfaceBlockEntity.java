package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.block.ExperienceInterfaceBlock;
import dev.anvilcraft.pigsplus.inventory.ExperienceInterfaceMenu;
import dev.anvilcraft.pigsplus.util.ExpUtil;
import dev.anvilcraft.pigsplus.util.FluidUtil;
import dev.anvilcraft.pigsplus.util.ParticleUtil;
import dev.dubhe.anvilcraft.init.block.ModFluidTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.CONFIG;

public class ExperienceInterfaceBlockEntity extends BlockEntity implements MenuProvider {
    private static final int SEARCH_RADIUS = 2;
    private static final int SCAN_COOLDOWN = 40;

    private int xp_target = 30;
    private int cooldown = 0;
    private boolean working = false;

    public ExperienceInterfaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.cooldown = tag.getInt("Cooldown");
        this.xp_target = tag.getInt("XpTarget");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Cooldown", this.cooldown);
        tag.putInt("XpTarget", this.xp_target);
    }

    public int getXpTarget() {
        return xp_target;
    }

    public void setXpTarget(int xpTarget) {
        this.xp_target = xpTarget;
        setChanged();
    }

    public void tick(Level level) {
        if (!(level instanceof ServerLevel level1)) return;

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
                    absorbPlayerExperience(player, getHandler(), level1);
                } else {
                    releaseExperienceToPlayer(player, getHandler(), level1);
                }
            }
        }
    }

    private void absorbPlayerExperience(Player player, IFluidHandler handler, ServerLevel level1) {
        int totalExp = ExpUtil.getPlayerXp(player) - ExpUtil.getXpfromAllLevel(xp_target);
        if (totalExp <= 0) return;

        int liquidExp = ExpUtil.getFLuidFromXp(totalExp);
        int accepted = FluidUtil.fill(handler, ModFluidTags.EXPERIENCE, liquidExp, IFluidHandler.FluidAction.SIMULATE);
        if (accepted <= 0) return;

        liquidExp = ExpUtil.XpRound(accepted); // 确保只接受完整的经验单位

        if (liquidExp <= 0) return;

        FluidUtil.fill(handler, ModFluidTags.EXPERIENCE, liquidExp, IFluidHandler.FluidAction.EXECUTE);
        player.giveExperiencePoints(-ExpUtil.getXpFromFluid(liquidExp));

        int particleNum = Math.max(20, liquidExp / CONFIG.electricEnchantingTable.fluidComsumeSpeed);
        for (int i = 0; i < particleNum; i++) {
            ParticleUtil.sendParticle(level1, player.getPosition(0), this.getBlockPos().getCenter());
        }
    }

    private void releaseExperienceToPlayer(Player player, IFluidHandler handler, ServerLevel level1) {
        int totalExp = ExpUtil.getXpfromAllLevel(xp_target) - ExpUtil.getPlayerXp(player);
        if (totalExp <= 0) return;

        int liquidExp = ExpUtil.getFLuidFromXp(totalExp);
        FluidStack accepted = FluidUtil.drain(handler, ModFluidTags.EXPERIENCE, liquidExp, IFluidHandler.FluidAction.SIMULATE);
        if (accepted.isEmpty()) return;

        liquidExp = ExpUtil.XpRound(accepted.getAmount()); // 确保只接受完整的经验单位
        if (liquidExp <= 0) return;

        FluidUtil.drain(handler, ModFluidTags.EXPERIENCE, liquidExp, IFluidHandler.FluidAction.EXECUTE);
        player.giveExperiencePoints(ExpUtil.getXpFromFluid(liquidExp));

        int particleNum = Math.max(20, liquidExp / CONFIG.electricEnchantingTable.fluidComsumeSpeed);
        for (int i = 0; i < particleNum; i++) {
            ParticleUtil.sendParticle(level1, this.getBlockPos().getCenter(), player.getPosition(0));
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.anvilcraft_pigsplus.experience_interface");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        if (player.isSpectator()) return null;
        return new ExperienceInterfaceMenu(i, this::setXpTarget);
    }

    public @Nullable IFluidHandler getHandler() {
        if (level == null) return null;
        Direction direction = getBlockState().getValue(ExperienceInterfaceBlock.FACING);
        BlockPos targetPos = getBlockPos().relative(direction.getOpposite());
        return level.getCapability(
            Capabilities.FluidHandler.BLOCK,
            targetPos,
            level.getBlockState(targetPos),
            level.getBlockEntity(targetPos),
            direction
        );
    }
}

