package dev.anvilcraft.pigsplus.block.entity.megastructure;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.api.modification.ReformerModification;
import dev.anvilcraft.pigsplus.api.modification.ReformerModifications;
import dev.anvilcraft.pigsplus.api.requirement.RequirementEntry;
import dev.anvilcraft.pigsplus.init.AddonRecipeTypes;
import dev.anvilcraft.pigsplus.network.CelestialReformerSyncPacket;
import dev.anvilcraft.pigsplus.recipe.CelestialReformerRecipe;
import dev.anvilcraft.pigsplus.recipe.CelestialReformerRecipe.LaserType;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilFluidInterfaceBlockEntity;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilLaserInterfaceBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialRefactorOption;
import dev.dubhe.anvilcraft.block.entity.megastructure.BaseMegastructureHandler;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 行星/恒星改造器的公共处理器。
 *
 * <p>负责从物流/流体/激光接口按配方吸收材料并推进进度，需求全部满足后调用
 * {@link ReformerModification} 修改天体属性；同时把运行状态同步给客户端，
 * 供 Jade 等显示模块读取。</p>
 *
 * <p>子类只需提供各自的 {@link #name()} 与巨构名称常量。</p>
 */
public abstract class ReformerHandler extends BaseMegastructureHandler {
    private @Nullable ResourceLocation activeRecipeId;
    private @Nullable RecipeHolder<CelestialReformerRecipe> currentRecipe;
    @Getter
    private int inputIndex;
    @Getter
    private int progress;
    private int cooldown;

    /**
     * 巨构建造完成时调用，清空所有改造状态并同步客户端。
     */
    @Override
    public void onBuild(CelestialForgingAnvilBlockEntity be) {
        this.removeRecipe(be);
    }

    /**
     * 巨构拆除/解除绑定时调用，清空所有改造信息与进度并同步客户端。
     */
    @Override
    public void onClear(CelestialForgingAnvilBlockEntity be) {
        this.removeRecipe(be);
    }

    /**
     * 服务端每 tick 驱动改造流程。
     *
     * <p>冷却结束后选择当前配方，一次吸收尽可能连续推进多个需求；全部需求完成后执行
     * 改造，并将进度变化同步给客户端。若配方含激光要求，激光仅作为本轮物品/流体吸取的
     * 前提条件，不再单独推进进度。</p>
     */
    @Override
    public void serverTick(CelestialForgingAnvilBlockEntity be) {
        if (be.getLevel() == null || be.getLevel().isClientSide()) return;

        CelestialRefactorOption option = be.getActiveMegastructureOption();
        if (option == null || !this.name().equals(option.megastructure())) return;

        // 工作冷却
        if (this.cooldown > 0) {
            this.cooldown--;
            return;
        }
        this.cooldown = AnvilCraftPigsPlus.CONFIG.reformerAbsorptionCooldown;

        // 检查正在执行的配方
        if (this.currentRecipe == null) {
            List<RecipeHolder<CelestialReformerRecipe>> recipes = new ArrayList<>(
                be.getLevel().getRecipeManager().getAllRecipesFor(AddonRecipeTypes.CELESTIAL_REFORMER_TYPE.get())
            );
            RecipeHolder<CelestialReformerRecipe> newRecipe = findRecipeById(recipes, this.activeRecipeId);
            if (newRecipe != null) this.resetRecipe(be, newRecipe);
        }
        if (this.currentRecipe == null) {
            RecipeHolder<CelestialReformerRecipe> newRecipe = this.getServerRecipe(be);
            if (newRecipe == null) return;

            this.resetRecipe(be, newRecipe);
        }

        CelestialReformerRecipe recipe = this.currentRecipe.value();
        List<CelestialReformerInputRequirement> requirements = this.toRequirements(recipe);
        if (requirements.isEmpty()) {
            this.applyModification(be, recipe.modification());
            return;
        }

        boolean hasValidLaser = this.hasAnyValidLaser(be, recipe);
        boolean hasRequirement = false;
        // 有激光要求时，激光必须先于物品/流体吸取生效
        if (this.getLaserRequirement(recipe) == null || hasValidLaser) {// 吸收材料
            while (this.inputIndex < requirements.size()) {
                CelestialReformerInputRequirement requirement = requirements.get(this.inputIndex);
                if (requirement.channel() == CelestialReformerInputChannel.LASER_INTERFACE) {
                    this.inputIndex++;
                    continue;
                }
                int gained = this.tickRequirement(be, requirement);
                if (gained <= 0) break;
                this.progress += gained;
                hasRequirement = true;
                if (this.progress >= this.getRequirementTarget(requirement)) {
                    this.inputIndex++;
                    this.progress = 0;
                    continue;
                }
                break;
            }
        }

        // 没有任何材料就尝试刷新配方
        if (!hasRequirement) {
            RecipeHolder<CelestialReformerRecipe> newRecipe = this.getServerRecipe(be);
            if (newRecipe == null) return;

            this.resetRecipe(be, newRecipe);
        }

        if (this.inputIndex >= requirements.size()) {
            this.applyModification(be, recipe.modification());
        } else if (this.progress > 0) {
            this.syncState(be);
        }

    }

    private void resetRecipe(CelestialForgingAnvilBlockEntity be, RecipeHolder<CelestialReformerRecipe> holder) {
        this.currentRecipe = holder;
        this.activeRecipeId = holder.id();
        this.resetProgress();
        this.syncState(be);
    }

    private void removeRecipe(CelestialForgingAnvilBlockEntity be) {
        this.activeRecipeId = null;
        this.currentRecipe = null;
        this.cooldown = AnvilCraftPigsPlus.CONFIG.reformerAbsorptionCooldown;
        this.resetProgress();
        this.syncState(be);
    }

    /**
     * 重置输入进度，使下一次从第一个需求开始。
     */
    private void resetProgress() {
        this.inputIndex = 0;
        this.progress = 0;
    }

    /**
     * 执行改造：调用注册表中的 {@link ReformerModification} 修改天体属性，随后清空进度、
     * 更新完成状态并同步客户端。
     */
    private void applyModification(CelestialForgingAnvilBlockEntity be, ResourceLocation modification) {
        ReformerModification modificationEffect =
            ReformerModifications.REGISTRY.get(modification);
        if (modificationEffect != null) {
            modificationEffect.apply(be);
        } else {
            AnvilCraftPigsPlus.LOGGER.warn("Unsupported planetary reformer modification: {}", modification);
        }
        this.removeRecipe(be);
        this.syncCelestialBody(be);
    }

    /**
     * 将服务端已修改的天体数据立即推送给附近玩家，避免 GUI 打开后仍显示旧状态。
     */
    private void syncCelestialBody(CelestialForgingAnvilBlockEntity be) {
        be.setChanged();
        if (be.getLevel() instanceof ServerLevel serverLevel) {
            Packet<ClientGamePacketListener> packet = be.getUpdatePacket();
            for (ServerPlayer player : serverLevel.getChunkSource().chunkMap.getPlayers(
                serverLevel.getChunkAt(be.getBlockPos()).getPos(),
                false
            )) {
                player.connection.send(packet);
            }
        }
    }

    /**
     * 将当前 handler 状态通过自定义网络包同步给追踪该区块的玩家。
     *
     * <p>只发送给服务端；客户端收到 {@link CelestialReformerSyncPacket} 后直接写入
     * 客户端 handler，保证多块结构上任意部件都能读到最新进度。</p>
     */
    private void syncState(CelestialForgingAnvilBlockEntity be) {
        be.setChanged();
        if (be.getLevel() instanceof ServerLevel serverLevel) {
            CompoundTag tag = new CompoundTag();
            this.writeUpdateTag(tag, serverLevel.registryAccess());
            PacketDistributor.sendToPlayersTrackingChunk(
                serverLevel,
                serverLevel.getChunkAt(be.getBlockPos()).getPos(),
                new CelestialReformerSyncPacket(be.getBlockPos(), tag)
            );
        }
    }

    /**
     * 获取当前应执行的配方。
     *
     * <p>客户端只按已同步的
     * {@link #activeRecipeId} 做纯读取，不参与配方选择。</p>
     *
     * @return 当前配方，无可用配方时返回 {@code null}
     */
    private @Nullable RecipeHolder<CelestialReformerRecipe> getClientRecipe(CelestialForgingAnvilBlockEntity be) {
        // 客户端纯读取
        if (this.currentRecipe != null) return this.currentRecipe;

        if (be.getLevel() == null) {
            return null;
        }

        List<RecipeHolder<CelestialReformerRecipe>> recipes = new ArrayList<>(
            be.getLevel().getRecipeManager().getAllRecipesFor(AddonRecipeTypes.CELESTIAL_REFORMER_TYPE.get())
        );
        return findRecipeById(recipes, this.activeRecipeId);
    }

    /**
     * 获取当前应执行的配方。
     *
     * <p>筛选满足条件且输入全部满足的配方</p>
     *
     * @return 当前配方，无可用配方时返回 {@code null}
     */
    private @Nullable RecipeHolder<CelestialReformerRecipe> getServerRecipe(CelestialForgingAnvilBlockEntity be) {
        if (be.getLevel() == null) return null;

        List<RecipeHolder<CelestialReformerRecipe>> recipes =
            new ArrayList<>(be.getLevel().getRecipeManager().getAllRecipesFor(AddonRecipeTypes.CELESTIAL_REFORMER_TYPE.get()));
        recipes.removeIf(holder -> !matchesRequirements(be, holder.value()));
        if (recipes.isEmpty()) return null;

        for (RecipeHolder<CelestialReformerRecipe> holder : recipes) {
            if (this.hasAllRequiredInputs(be, holder.value())) {
                return holder;
            }
        }
        return null;
    }

    /**
     * 校验配方携带的所有需求是否在当前天体上成立。
     *
     * @return 任一需求不成立或需求未注册时返回 {@code false}
     */
    public static boolean matchesRequirements(CelestialForgingAnvilBlockEntity be, CelestialReformerRecipe recipe) {
        for (RequirementEntry entry : recipe.requirements()) {
            if (!entry.requirement().test(be)) return false;
        }
        return true;
    }

    /**
     * 按配方 id 在候选配方中查找匹配项。
     *
     * @param recipeId 配方 id，可为 {@code null}
     * @return 匹配的配方，未找到时返回 {@code null}
     */
    public static @Nullable RecipeHolder<CelestialReformerRecipe> findRecipeById(
        List<RecipeHolder<CelestialReformerRecipe>> recipes,
        @Nullable ResourceLocation recipeId
    ) {
        if (recipeId == null) return null;
        return recipes.stream()
            .filter(holder -> recipeId.equals(holder.id()))
            .findFirst()
            .orElse(null);
    }

    /**
     * 把配方中的物品/流体/激光输入展开为统一的输入需求列表。
     */
    private List<CelestialReformerInputRequirement> toRequirements(CelestialReformerRecipe recipe) {
        List<CelestialReformerInputRequirement> requirements = new ArrayList<>();
        for (CelestialReformerRecipe.ItemInput input : recipe.items()) {
            requirements.add(CelestialReformerInputRequirement.logisticsItem(input.item(), input.count()));
        }
        for (CelestialReformerRecipe.FluidInput input : recipe.fluids()) {
            requirements.add(CelestialReformerInputRequirement.fluid(input.fluid(), input.amount()));
        }
        for (CelestialReformerRecipe.LaserInput input : recipe.lasers()) {
            requirements.add(CelestialReformerInputRequirement.laser(input.level(), input.type()));
        }
        return requirements;
    }

    /**
     * 获取单个需求的完成目标。
     */
    private int getRequirementTarget(CelestialReformerInputRequirement requirement) {
        return requirement.amount();
    }

    /**
     * 检查配方所有输入是否同时在场（只判断类型存在，不要求数量足够）。
     */
    private boolean hasAllRequiredInputs(CelestialForgingAnvilBlockEntity be, CelestialReformerRecipe recipe) {
        List<CelestialReformerInputRequirement> requirements = this.toRequirements(recipe);
        if (requirements.isEmpty()) return false;
        for (CelestialReformerInputRequirement requirement : requirements) {
            if (!this.hasRequirementInput(be, requirement)) return false;
        }
        return true;
    }

    /**
     * 检查单个输入通道中是否存在指定资源。
     */
    private boolean hasRequirementInput(
        CelestialForgingAnvilBlockEntity be,
        CelestialReformerInputRequirement requirement
    ) {
        return switch (requirement.channel()) {
            case LOGISTICS_ITEM -> this.hasItemInInterfaces(be, requirement.resource());
            case FLUID_INTERFACE -> this.hasFluidInInterfaces(be, requirement.resource());
            case LASER_INTERFACE -> this.hasValidLaser(be, requirement);
        };
    }

    /**
     * 检查物流接口中是否存在指定物品。
     */
    private boolean hasItemInInterfaces(CelestialForgingAnvilBlockEntity be, ResourceLocation itemId) {
        var item = BuiltInRegistries.ITEM.get(itemId);
        if (item == Items.AIR) return true;
        List<IItemHandler> interfaces = this.findLogisticsInterfaces(be);
        if (interfaces == null) return false;
        for (IItemHandler handler : interfaces) {
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                if (handler.getStackInSlot(slot).is(item)) return true;
            }
        }
        return false;
    }

    /**
     * 检查流体接口中是否存在指定流体。
     */
    private boolean hasFluidInInterfaces(CelestialForgingAnvilBlockEntity be, ResourceLocation fluidId) {
        Fluid fluid = BuiltInRegistries.FLUID.get(fluidId);
        if (fluid.isSame(Fluids.EMPTY)) return true;
        for (CelestialForgingAnvilFluidInterfaceBlockEntity fluidInterface : this.findFluidInterfaces(be)) {
            IFluidHandler handler = fluidInterface.getInternalFluidHandler();
            for (int tank = 0; tank < handler.getTanks(); tank++) {
                FluidStack stack = handler.getFluidInTank(tank);
                if (!stack.isEmpty() && stack.getFluid().isSame(fluid)) return true;
            }
        }
        return false;
    }

    /**
     * 检查是否存在等级与伽马类型均满足要求的激光接口。
     */
    private boolean hasValidLaser(
        CelestialForgingAnvilBlockEntity be,
        CelestialReformerInputRequirement requirement
    ) {
        for (CelestialForgingAnvilLaserInterfaceBlockEntity laser : this.findLaserInterfaces(be)) {
            LaserType type = requirement.laserType();
            if (laser.getReceivedLaserLevel() >= requirement.amount()
                && (type == LaserType.ANY || laser.isReceivedGamma() == (type == LaserType.GAMMA))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查配方中是否存在任意一个已满足的激光要求。
     */
    private boolean hasAnyValidLaser(CelestialForgingAnvilBlockEntity be, CelestialReformerRecipe recipe) {
        for (CelestialReformerInputRequirement requirement : this.toRequirements(recipe)) {
            if (requirement.channel() == CelestialReformerInputChannel.LASER_INTERFACE
                && this.hasValidLaser(be, requirement)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取配方中的第一个激光要求；无激光要求时返回 {@code null}。
     */
    private @Nullable CelestialReformerInputRequirement getLaserRequirement(CelestialReformerRecipe recipe) {
        for (CelestialReformerInputRequirement requirement : this.toRequirements(recipe)) {
            if (requirement.channel() == CelestialReformerInputChannel.LASER_INTERFACE) {
                return requirement;
            }
        }
        return null;
    }

    /**
     * 将当前激光需求的等级/伽马要求同步到所有激光接口。
     *
     * <p>配方含激光要求时保持该激光要求，激光作为整轮物品/流体吸取的前提条件。</p>
     */
    public void syncLaserRequirements(CelestialForgingAnvilBlockEntity be) {
        RecipeHolder<CelestialReformerRecipe> clientRecipe = this.getClientRecipe(be);
        CelestialReformerInputRequirement target = clientRecipe == null
            ? null
            : this.getLaserRequirement(clientRecipe.value());
        for (CelestialForgingAnvilLaserInterfaceBlockEntity laser : this.findLaserInterfaces(be)) {
            if (target == null) {
                laser.setLaserRequirement(0, false);
            } else {
                laser.setLaserRequirement(target.amount(), target.laserType() == LaserType.GAMMA);
            }
        }
    }

    /**
     * 推进单个需求：按需求通道消耗物品/流体，或校验激光是否满足要求。
     *
     * @return 本次实际推进的数量；无法推进时返回 0
     */
    private int tickRequirement(
        CelestialForgingAnvilBlockEntity be,
        CelestialReformerInputRequirement requirement
    ) {
        int remaining = this.getRequirementTarget(requirement) - this.progress;
        return switch (requirement.channel()) {
            case LOGISTICS_ITEM -> this.consumeItems(be, requirement.resource(), remaining);
            case FLUID_INTERFACE -> this.consumeFluid(be, requirement.resource(), remaining);
            case LASER_INTERFACE -> 0;
        };
    }

    /**
     * 从所有物流接口中消耗指定物品，累计不超过 {@code limit}。
     *
     * @return 实际消耗数量
     */
    private int consumeItems(CelestialForgingAnvilBlockEntity be, ResourceLocation itemId, int limit) {
        if (limit <= 0) return 0;
        var item = BuiltInRegistries.ITEM.get(itemId);
        if (item == Items.AIR) return 0;
        int consumed = 0;
        for (IItemHandler handler : this.findLogisticsInterfaces(be)) {
            for (int slot = 0; slot < handler.getSlots() && consumed < limit; slot++) {
                ItemStack stack = handler.getStackInSlot(slot);
                if (stack.isEmpty() || !stack.is(item)) continue;
                int amount = Math.min(limit - consumed, stack.getCount());
                consumed += handler.extractItem(slot, amount, false).getCount();
            }
            if (consumed >= limit) break;
        }
        return consumed;
    }

    /**
     * 从所有流体接口中消耗指定流体，累计不超过 {@code limit}。
     *
     * @return 实际消耗数量（mB）
     */
    private int consumeFluid(CelestialForgingAnvilBlockEntity be, ResourceLocation fluidId, int limit) {
        if (limit <= 0) return 0;
        Fluid fluid = BuiltInRegistries.FLUID.get(fluidId);
        if (fluid.isSame(Fluids.EMPTY)) return 0;
        int consumed = 0;
        for (CelestialForgingAnvilFluidInterfaceBlockEntity fluidInterface : this.findFluidInterfaces(be)) {
            IFluidHandler handler = fluidInterface.getInternalFluidHandler();
            FluidStack drained = handler.drain(
                new FluidStack(fluid, limit - consumed),
                IFluidHandler.FluidAction.EXECUTE
            );
            consumed += drained.getAmount();
            if (consumed >= limit) break;
        }
        return consumed;
    }

    /**
     * 获取当前配方展开后的输入需求列表，供显示模块读取。
     */
    public List<CelestialReformerInputRequirement> getInputRequirements(CelestialForgingAnvilBlockEntity be) {
        RecipeHolder<CelestialReformerRecipe> clientRecipe = this.getClientRecipe(be);
        return clientRecipe == null ? List.of() : this.toRequirements(clientRecipe.value());
    }

    /**
     * 获取当前配方的改造 id，供显示模块读取。
     */
    public @Nullable ResourceLocation getActiveModification(CelestialForgingAnvilBlockEntity be) {
        RecipeHolder<CelestialReformerRecipe> clientRecipe = this.getClientRecipe(be);
        return clientRecipe == null ? null : clientRecipe.value().modification();
    }

    /**
     * 获取当前配方的需求 id 列表，供显示模块读取。
     */
    public List<ResourceLocation> getRequirementIds(CelestialForgingAnvilBlockEntity be) {
        RecipeHolder<CelestialReformerRecipe> clientRecipe = this.getClientRecipe(be);
        return clientRecipe == null ? List.of() : clientRecipe.value().requirements().stream().map(RequirementEntry::id).toList();
    }

    /**
     * 获取当前配方的完整需求条目，供显示模块序列化后发送给客户端。
     */
    public List<RequirementEntry> getRequirementEntries(CelestialForgingAnvilBlockEntity be) {
        RecipeHolder<CelestialReformerRecipe> clientRecipe = this.getClientRecipe(be);
        return clientRecipe == null ? List.of() : clientRecipe.value().requirements();
    }

    /**
     * 持久化当前改造状态。
     */
    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putString(
            "pigsplusCelestialReformerRecipe",
            this.activeRecipeId == null ? "" : this.activeRecipeId.toString()
        );
        tag.putInt("pigsplusCelestialReformerInputIndex", this.inputIndex);
        tag.putInt("pigsplusCelestialReformerProgress", this.progress);
    }

    /**
     * 从存档数据恢复当前改造状态。
     */
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        String recipeId = tag.getString("pigsplusCelestialReformerRecipe");
        if (recipeId.isEmpty()) {
            recipeId = tag.getString("pigsplusPlanetaryReformerRecipe");
        }
        this.activeRecipeId = recipeId.isEmpty() ? null : ResourceLocation.tryParse(recipeId);
        this.inputIndex = tag.contains("pigsplusCelestialReformerInputIndex")
            ? tag.getInt("pigsplusCelestialReformerInputIndex")
            : tag.getInt("pigsplusPlanetaryReformerInputIndex");
        this.progress = tag.contains("pigsplusCelestialReformerProgress")
            ? tag.getInt("pigsplusCelestialReformerProgress")
            : tag.getInt("pigsplusPlanetaryReformerProgress");
    }

    /**
     * 写入客户端同步标签，当前与存档序列化保持一致。
     */
    @Override
    public void writeUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        this.saveAdditional(tag, registries);
    }

    /**
     * 读取客户端同步标签。
     */
    @Override
    public void readUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        this.loadAdditional(tag, registries);
    }
}
