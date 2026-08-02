package dev.anvilcraft.pigsplus.api.modification;

import dev.anvilcraft.pigsplus.init.AddonDataAttachments;
import dev.anvilcraft.pigsplus.util.CelestialReformerPlanetUtil;
import dev.anvilcraft.pigsplus.util.OceanEnchantmentData;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.LiquidCoverage;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetaryResourceSet;
import dev.dubhe.anvilcraft.block.entity.celestial.RockyPlanetData;
import dev.dubhe.anvilcraft.init.block.ModFluids;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 将水海洋转换为携带随机附魔的液态魔咒海。
 */
public class AddLiquidEnchantmentOceanModification extends ReformerModification {
    @Override
    public Component getDescription() {
        return this.text("modification.anvilcraft_pigsplus.add_liquid_enchantment_ocean");
    }

    @Override
    public ResourceLocation getIcon() {
        return this.icon("sea");
    }

    @Override
    public void apply(CelestialForgingAnvilBlockEntity be) {
        if (!(be.getCelestialBodyData() instanceof RockyPlanetData rp)
            || rp.liquidCoverage() == LiquidCoverage.NONE) {
            return;
        }
        ResourceKey<Enchantment> enchantment = pickRandomEnchantment(be);
        if (enchantment == null) return;

        PlanetaryResourceSet resources = CelestialReformerPlanetUtil.ensureResources(be);
        CelestialReformerPlanetUtil.setOceanFluid(resources, ModFluids.LIQUID_ENCHANTMENT.getId());
        be.setPlanetaryResourceSet(resources);
        be.setData(AddonDataAttachments.OCEAN_ENCHANTMENT, new OceanEnchantmentData(enchantment));
    }

    private static @Nullable ResourceKey<Enchantment> pickRandomEnchantment(CelestialForgingAnvilBlockEntity be) {
        if (be.getLevel() == null) return null;
        List<Holder.Reference<Enchantment>> enchantments = be.getLevel()
            .registryAccess()
            .lookupOrThrow(Registries.ENCHANTMENT)
            .listElements()
            .toList();
        if (enchantments.isEmpty()) return null;
        return enchantments.get(be.getLevel().getRandom().nextInt(enchantments.size())).key();
    }
}
