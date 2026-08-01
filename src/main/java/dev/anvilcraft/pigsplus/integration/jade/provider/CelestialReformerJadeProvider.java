package dev.anvilcraft.pigsplus.integration.jade.provider;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.api.modification.ReformerModification;
import dev.anvilcraft.pigsplus.api.modification.ReformerModifications;
import dev.anvilcraft.pigsplus.api.requirement.RequirementEntry;
import dev.anvilcraft.pigsplus.api.requirement.ReformerRequirement;
import dev.anvilcraft.pigsplus.block.entity.megastructure.ReformerHandler;
import dev.anvilcraft.pigsplus.block.entity.megastructure.CelestialReformerInputChannel;
import dev.anvilcraft.pigsplus.block.entity.megastructure.CelestialReformerInputRequirement;
import dev.anvilcraft.pigsplus.client.jade.TextureElement;
import dev.anvilcraft.pigsplus.recipe.CelestialReformerRecipe.LaserType;
import dev.anvilcraft.pigsplus.util.CelestialReformerHooks;
import dev.anvilcraft.pigsplus.util.ReformerIcons;
import dev.dubhe.anvilcraft.block.cfa.CelestialForgingAnvilBlock;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.megastructure.IMegastructureHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public enum CelestialReformerJadeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    private static final String DATA_MODIFICATION = "pigsplusModification";
    private static final String DATA_REQUIREMENT_ENTRIES = "pigsplusRequirementEntries";
    private static final String DATA_REQUIREMENTS = "pigsplusRequirements";
    private static final String DATA_REQUIREMENT_PROGRESS = "pigsplusRequirementProgress";

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        try {
            // 服务端把完整需求条目序列化进 NBT
            CelestialForgingAnvilBlockEntity be = findController(accessor);
            if (be == null) return;
            ReformerHandler handler = getReformerHandler(be);
            if (handler == null || !CelestialReformerHooks.isActive(be)) return;
            ResourceLocation modification = handler.getActiveModification(be);
            if (modification == null) return;

            data.putString(DATA_MODIFICATION, modification.toString());
            data.putIntArray(DATA_REQUIREMENT_PROGRESS, handler.getRequirementProgresses());

            // RequirementEntry.CODEC 会保留参数化需求，例如 rotation_speed 的 min/max。
            ListTag requirementEntries = new ListTag();
            for (RequirementEntry entry : handler.getRequirementEntries(be)) {
                Tag encoded = RequirementEntry.CODEC.encodeStart(NbtOps.INSTANCE, entry)
                    .result()
                    .orElseGet(CompoundTag::new);
                requirementEntries.add(encoded);
            }
            data.put(DATA_REQUIREMENT_ENTRIES, requirementEntries);

            // 物品/流体/激光输入用于显示所有收集进度，和服务端需求条目分开传递。
            ListTag requirements = new ListTag();
            for (CelestialReformerInputRequirement requirement : handler.getInputRequirements(be)) {
                CompoundTag entry = new CompoundTag();
                entry.putString("channel", requirement.channel().name());
                entry.putString("resource", requirement.resource().toString());
                entry.putInt("amount", requirement.amount());
                entry.putString(
                    "laserType",
                    requirement.laserType() == null ? "none" : requirement.laserType().getSerializedName()
                );
                requirements.add(entry);
            }
            data.put(DATA_REQUIREMENTS, requirements);
        } catch (RuntimeException | LinkageError ignored) {
        }
    }

    @Override
    public boolean shouldRequestData(BlockAccessor accessor) {
        return true;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        try {
            // 客户端只负责读取服务端已序列化的配方数据，并展示改造、需求与所有输入收集进度。
            CelestialForgingAnvilBlockEntity be = findController(accessor);
            if (be == null) return;
            ReformerHandler handler = getReformerHandler(be);
            if (handler == null || !CelestialReformerHooks.isActive(be)) return;

            CompoundTag serverData = accessor.getServerData();
            if (!serverData.contains(DATA_MODIFICATION)) return;
            ResourceLocation modification = ResourceLocation.tryParse(serverData.getString(DATA_MODIFICATION));
            List<RequirementEntry> requirementEntries = readRequirementEntries(
                serverData.getList(DATA_REQUIREMENT_ENTRIES, Tag.TAG_COMPOUND)
            );
            List<CelestialReformerInputRequirement> requirements = readRequirements(serverData.getList(DATA_REQUIREMENTS, Tag.TAG_COMPOUND));
            int[] requirementProgresses = serverData.getIntArray(DATA_REQUIREMENT_PROGRESS);
            if (modification == null || requirements.isEmpty()) return;

            IElementHelper helper = IElementHelper.get();
            ReformerModification effect = ReformerModifications.REGISTRY.get(modification);
            if (effect != null) {
                tooltip.add(List.of(
                    new TextureElement(
                        effect.getIcon(),
                        ReformerIcons.ICON_SIZE,
                        ReformerIcons.ICON_SIZE
                    ),
                    helper.text(effect.getDescription())
                ));
            }
            List<IElement> requirementIcons = new ArrayList<>();
            for (RequirementEntry entry : requirementEntries) {
                ReformerRequirement requirement = entry.requirement();
                if (requirement != null) {
                    requirementIcons.add(new TextureElement(
                        ReformerIcons.requirementIcon(entry.id()),
                        ReformerIcons.ICON_SIZE,
                        ReformerIcons.ICON_SIZE
                    ));
                }
            }
            for (CelestialReformerInputRequirement input : requirements) {
                if (input.channel() == CelestialReformerInputChannel.LASER_INTERFACE) {
                    requirementIcons.add(new TextureElement(
                        ReformerIcons.laserIcon(),
                        ReformerIcons.ICON_SIZE,
                        ReformerIcons.ICON_SIZE
                    ));
                }
            }
            if (!requirementIcons.isEmpty()) {
                tooltip.add(requirementIcons);
            }

            for (int i = 0; i < requirements.size(); i++) {
                CelestialReformerInputRequirement requirement = requirements.get(i);
                if (requirement.channel() == CelestialReformerInputChannel.LASER_INTERFACE) {
                    tooltip.add(Component.translatable(
                        "tooltip.anvilcraft_pigsplus.celestial_reformer.current.laser",
                        requirement.amount(),
                        laserType(requirement.laserType())
                    ));
                    continue;
                }
                int total = requirement.amount();
                if (total <= 0) continue;
                int current = i < requirementProgresses.length ? requirementProgresses[i] : 0;
                float percentage = (float) Math.min(current, total) / total;
                tooltip.add(helper.progress(
                    percentage,
                    requirementText(requirement, current, total),
                    helper.progressStyle().color(0xFF4C6FDB, 0xFF86A8FF).textColor(0xFFFFFFFF),
                    BoxStyle.getNestedBox(),
                    true
                ));
            }
        } catch (RuntimeException | LinkageError ignored) {
        }
    }

    private static @Nullable CelestialForgingAnvilBlockEntity findController(BlockAccessor accessor) {
        // 多块结构的所有部件都会把主方块定位到锻星砧控制器。
        if (!(accessor.getBlockState().getBlock() instanceof CelestialForgingAnvilBlock block)) return null;

        BlockPos mainPos = block.getMainPartPos(accessor.getPosition(), accessor.getBlockState());
        BlockEntity main = accessor.getLevel().getBlockEntity(mainPos);
        return main instanceof CelestialForgingAnvilBlockEntity controller ? controller : null;
    }

    private static @Nullable ReformerHandler getReformerHandler(CelestialForgingAnvilBlockEntity be) {
        // 只处理当前激活的巨构是行星/恒星改造器的情况。
        IMegastructureHandler active = be.getMegastructureManager().getActiveHandler(be);
        return active instanceof ReformerHandler handler ? handler : null;
    }

    @Override
    public ResourceLocation getUid() {
        return AnvilCraftPigsPlus.of("celestial_reformer");
    }

    private static Component requirementText(
        CelestialReformerInputRequirement requirement,
        int current,
        int total
    ) {
        return switch (requirement.channel()) {
            case LOGISTICS_ITEM -> {
                var item = BuiltInRegistries.ITEM.get(requirement.resource());
                yield Component.translatable(
                    "tooltip.anvilcraft_pigsplus.celestial_reformer.current.item",
                    new ItemStack(item).getHoverName(),
                    current,
                    total
                );
            }
            case FLUID_INTERFACE -> {
                var fluid = BuiltInRegistries.FLUID.get(requirement.resource());
                yield Component.translatable(
                    "tooltip.anvilcraft_pigsplus.celestial_reformer.current.fluid",
                    fluid.getFluidType().getDescription(),
                    current,
                    total
                );
            }
            case LASER_INTERFACE -> Component.translatable(
                "tooltip.anvilcraft_pigsplus.celestial_reformer.current.laser",
                requirement.amount(),
                laserType(requirement.laserType())
            );
        };
    }

    private static Component laserType(@Nullable LaserType type) {
        String key;
        if (type == null || type == LaserType.ANY) {
            key = "gui.anvilcraft_pigsplus.laser.type.any";
        } else if (type == LaserType.GAMMA) {
            key = "gui.anvilcraft_pigsplus.laser.type.gamma";
        } else {
            key = "gui.anvilcraft_pigsplus.laser.type.normal";
        }
        return Component.translatable(key);
    }

    private static List<CelestialReformerInputRequirement> readRequirements(ListTag list) {
        // 这些是物品/流体/激光输入，用于展示所有收集进度条。
        List<CelestialReformerInputRequirement> requirements = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            CelestialReformerInputChannel channel =
                CelestialReformerInputChannel.valueOf(entry.getString("channel"));
            ResourceLocation resource = ResourceLocation.tryParse(entry.getString("resource"));
            requirements.add(new CelestialReformerInputRequirement(
                channel,
                resource,
                entry.getInt("amount"),
                readLaserType(entry)
            ));
        }
        return requirements;
    }

    /**
     * 从服务端 NBT 中恢复完整需求条目。
     *
     * <p>这里的 CODEC 与配方数据包使用同一套逻辑，因此能还原带参数的
     * {@link dev.anvilcraft.pigsplus.api.requirement.RotationSpeedRequirement} 等需求对象。</p>
     */
    private static List<RequirementEntry> readRequirementEntries(ListTag list) {
        List<RequirementEntry> entries = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            RequirementEntry.CODEC.parse(NbtOps.INSTANCE, list.get(i))
                .result()
                .ifPresent(entries::add);
        }
        return entries;
    }

    private static @Nullable LaserType readLaserType(CompoundTag entry) {
        String type = entry.getString("laserType");
        if (type.isEmpty() || "none".equals(type)) return null;
        return LaserType.fromName(type);
    }
}
