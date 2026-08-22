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
        this.xp_target = Math.max(0, tag.getInt("XpTarget"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Cooldown", this.cooldown);
        tag.putInt("XpTarget", this.xp_target);
    }

    public int getXpTarget() {
        return this.xp_target;
    }

    public void setXpTarget(int xpTarget) {
        this.xp_target = Math.max(0, xpTarget);
        this.setChanged();
    }

    public void tick(Level level) {
        if (!(level instanceof ServerLevel level1)) return;

        if (this.working) {
            this.cooldown = 1;
            this.working = false;
        }

        if (--this.cooldown > 0) return;
        this.cooldown = SCAN_COOLDOWN;

        IFluidHandler handler = this.getHandler();
        if (handler == null) return;

        // 扫描周围玩家
        AABB searchBox = new AABB(this.getBlockPos()).inflate(SEARCH_RADIUS);
        List<Player> players = level.getEntitiesOfClass(Player.class, searchBox);
        for (Player player : players) {
            if (!player.isRemoved() && !player.isSpectator()) {
                int playerLevel = player.experienceLevel;
                if (playerLevel >= this.xp_target) {
                    this.absorbPlayerExperience(player, handler, level1);
                } else {
                    this.releaseExperienceToPlayer(player, handler, level1);
                }
            }
        }
    }

    private void absorbPlayerExperience(Player player, IFluidHandler handler, ServerLevel level1) {
        int totalExp = ExpUtil.getPlayerXp(player) - ExpUtil.getXpfromAllLevel(this.xp_target);
        if (totalExp <= 0) return;

        int transferExp = Math.min(totalExp, this.getMaxTransfer());
        int liquidExp = ExpUtil.getFLuidFromXp(transferExp);
        int accepted = FluidUtil.fill(handler, ModFluidTags.EXPERIENCE, liquidExp, IFluidHandler.FluidAction.SIMULATE);
        if (accepted <= 0) return;

        liquidExp = ExpUtil.XpRound(accepted); // 确保只接受完整的经验单位

        if (liquidExp <= 0) return;

        FluidUtil.fill(handler, ModFluidTags.EXPERIENCE, liquidExp, IFluidHandler.FluidAction.EXECUTE);
        player.giveExperiencePoints(-ExpUtil.getXpFromFluid(liquidExp));

        int particleNum = Math.min(10, liquidExp / CONFIG.electricEnchantingTable.fluidComsumeSpeed);
        for (int i = 0; i < particleNum; i++) {
            ParticleUtil.sendParticle(level1, player.getPosition(0), this.getBlockPos().getCenter());
        }
    }

    private void releaseExperienceToPlayer(Player player, IFluidHandler handler, ServerLevel level1) {
        int totalExp = ExpUtil.getXpfromAllLevel(this.xp_target) - ExpUtil.getPlayerXp(player);
        if (totalExp <= 0) return;

        int transferExp = Math.min(totalExp, this.getMaxTransfer());
        int liquidExp = ExpUtil.getFLuidFromXp(transferExp);
        FluidStack accepted = FluidUtil.drain(handler, ModFluidTags.EXPERIENCE, liquidExp, IFluidHandler.FluidAction.SIMULATE);
        if (accepted.isEmpty()) return;

        liquidExp = ExpUtil.XpRound(accepted.getAmount()); // 确保只接受完整的经验单位
        if (liquidExp <= 0) return;

        FluidUtil.drain(handler, ModFluidTags.EXPERIENCE, liquidExp, IFluidHandler.FluidAction.EXECUTE);
        player.giveExperiencePoints(ExpUtil.getXpFromFluid(liquidExp));

        int particleNum = Math.min(10, liquidExp / CONFIG.electricEnchantingTable.fluidComsumeSpeed);
        for (int i = 0; i < particleNum; i++) {
            ParticleUtil.sendParticle(level1, this.getBlockPos().getCenter(), player.getPosition(0));
        }
    }

    private int getMaxTransfer() {
        return Math.max(
            1,
            Math.min(CONFIG.experienceInterfaceMaxTransfer, Integer.MAX_VALUE / ExpUtil.EXPERIENCE_TO_LIQUID)
        );
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
        if (this.level == null) return null;
        Direction direction = this.getBlockState().getValue(ExperienceInterfaceBlock.FACING);
        BlockPos targetPos = this.getBlockPos().relative(direction.getOpposite());
        return this.level.getCapability(
            Capabilities.FluidHandler.BLOCK,
            targetPos,
            this.level.getBlockState(targetPos),
            this.level.getBlockEntity(targetPos),
            direction
        );
    }
}

