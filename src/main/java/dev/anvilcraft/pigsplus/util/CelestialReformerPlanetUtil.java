package dev.anvilcraft.pigsplus.util;

import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialBodyData;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetResourceGenerator;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetaryResourceSet;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetaryResourceSet.WeightedItemStack;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 行星资源生成的共用工具，优先复用铁砧工艺本体的资源生成逻辑。
 */
public final class CelestialReformerPlanetUtil {
    public static final List<ResourceLocation> CIVILIZATION_GEM_BLOCKS = List.of(
        ResourceLocation.withDefaultNamespace("emerald_block"),
        ResourceLocation.parse("anvilcraft:topaz_block"),
        ResourceLocation.parse("anvilcraft:ruby_block"),
        ResourceLocation.parse("anvilcraft:sapphire_block")
    );
    public static final List<ResourceLocation> CIVILIZATION_GEM_AMULETS = List.of(
        ResourceLocation.parse("anvilcraft:emerald_amulet"),
        ResourceLocation.parse("anvilcraft:topaz_amulet"),
        ResourceLocation.parse("anvilcraft:ruby_amulet"),
        ResourceLocation.parse("anvilcraft:sapphire_amulet")
    );

    private CelestialReformerPlanetUtil() {
    }

    /**
     * 更新天体参数，并按照本体规则重新生成资源集。
     */
    public static void regenerate(CelestialForgingAnvilBlockEntity be, CelestialBodyData body) {
        PlanetaryResourceSet resources = generate(be, body);
        be.setCelestialBodyData(body);
        be.setPlanetaryResourceSet(resources);
        be.setChanged();
    }

    /**
     * 清除原有生物资源，并按本体逻辑重新随机生成一份生物资源。
     */
    public static void addBiologicalResources(CelestialForgingAnvilBlockEntity be) {
        CelestialBodyData body = be.getCelestialBodyData();
        if (body == null || be.getLevel() == null) return;
        long randomOffset = be.getLevel().getRandom().nextLong();
        PlanetaryResourceSet generated = null;
        for (int i = 0; i < 64; i++) {
            PlanetaryResourceSet candidate = generate(be, body, randomOffset + i);
            if (!candidate.getBiologicalItems().isEmpty() || !candidate.getBiologicalFluids().isEmpty()) {
                generated = candidate;
                break;
            }
        }
        if (generated == null) {
            generated = generate(be, body, randomOffset);
        }
        PlanetaryResourceSet resources = ensureResources(be);
        listField(resources, "biologicalItems").addAll(generated.getBiologicalItems());
        listField(resources, "biologicalFluids").addAll(generated.getBiologicalFluids());
        be.setPlanetaryResourceSet(resources);
        be.setChanged();
    }

    /**
     * 添加低等文明，生成与本体相同的文明资源，删除废土资源，并停止生物资源产出。
     */
    public static void addCivilization(CelestialForgingAnvilBlockEntity be) {
        PlanetaryResourceSet resources = ensureResources(be);
        listField(resources, "biologicalItems").clear();
        listField(resources, "biologicalFluids").clear();
        listField(resources, "wastelandItems").clear();
        listField(resources, "offerings").clear();
        addOffering(resources, pickCivilizationResource(be, CIVILIZATION_GEM_BLOCKS), 50);
        addOffering(resources, ResourceLocation.withDefaultNamespace("experience_bottle"), 40);
        addOffering(resources, ResourceLocation.parse("anvilcraft:royal_steel_ingot"), 5);
        addOffering(resources, ResourceLocation.withDefaultNamespace("totem_of_undying"), 2);
        addOffering(resources, pickCivilizationResource(be, CIVILIZATION_GEM_AMULETS), 2);
        addOffering(resources, ResourceLocation.withDefaultNamespace("heart_of_the_sea"), 1);
        invokeVoid(resources, "setHasCivilization");
        be.setChanged();
    }

    /**
     * 转为废土世界并清空文明；虚空废土使用虚空物质替换粗铀与钚粒。
     */
    public static void setWasteland(CelestialForgingAnvilBlockEntity be, boolean voidWasteland) {
        PlanetaryResourceSet resources = ensureResources(be);
        listField(resources, "biologicalItems").clear();
        listField(resources, "biologicalFluids").clear();
        listField(resources, "offerings").clear();
        listField(resources, "wastelandItems").clear();
        setField(resources, "hasCivilization", false);
        invokeVoid(resources, "setWasteland");

        invokeAdd(resources, "addWastelandItem", new WeightedItemStack(ResourceLocation.parse("anvilcraft:reinforced_concrete_gray"), 60));
        invokeAdd(resources, "addWastelandItem", new WeightedItemStack(ResourceLocation.parse("anvilcraft:circuit_board"), 30));
        invokeAdd(resources, "addWastelandItem", new WeightedItemStack(ResourceLocation.parse("anvilcraft:processor"), 5));
        if (voidWasteland) {
            invokeAdd(resources, "addWastelandItem", new WeightedItemStack(ResourceLocation.parse("anvilcraft:void_matter"), 5));
        } else {
            invokeAdd(resources, "addWastelandItem", new WeightedItemStack(ResourceLocation.parse("anvilcraft:raw_uranium"), 3));
            invokeAdd(resources, "addWastelandItem", new WeightedItemStack(ResourceLocation.parse("anvilcraft:plutonium_nugget"), 2));
        }
        be.setChanged();
    }

    private static ResourceLocation pickCivilizationResource(
        CelestialForgingAnvilBlockEntity be,
        List<ResourceLocation> candidates
    ) {
        if (be.getLevel() == null) return candidates.getFirst();
        return candidates.get(be.getLevel().getRandom().nextInt(candidates.size()));
    }

    private static void addOffering(PlanetaryResourceSet resources, ResourceLocation item, int weight) {
        invokeAdd(resources, "addOffering", new WeightedItemStack(item, weight));
    }

    private static PlanetaryResourceSet generate(CelestialForgingAnvilBlockEntity be, CelestialBodyData body) {
        return generate(be, body, 0);
    }

    private static PlanetaryResourceSet generate(
        CelestialForgingAnvilBlockEntity be,
        CelestialBodyData body,
        long seedOffset
    ) {
        return PlanetResourceGenerator.generate(
            body,
            be.getAgeAnvilCount(),
            be.getLevel(),
            be.getBodySeed() + seedOffset,
            null
        );
    }

    private static PlanetaryResourceSet ensureResources(CelestialForgingAnvilBlockEntity be) {
        PlanetaryResourceSet resources = be.getPlanetaryResourceSet();
        if (resources == null) {
            resources = new PlanetaryResourceSet();
            be.setPlanetaryResourceSet(resources);
        }
        return resources;
    }

    private static void invokeVoid(PlanetaryResourceSet resources, String methodName) {
        try {
            Method method = PlanetaryResourceSet.class.getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(resources);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to modify planetary resource set", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Object> listField(PlanetaryResourceSet resources, String fieldName) {
        try {
            var field = PlanetaryResourceSet.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (List<Object>) field.get(resources);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to modify planetary resource set", ex);
        }
    }

    private static void setField(PlanetaryResourceSet resources, String fieldName, boolean value) {
        try {
            var field = PlanetaryResourceSet.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.setBoolean(resources, value);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to modify planetary resource set", ex);
        }
    }

    private static void invokeAdd(PlanetaryResourceSet resources, String methodName, Object value) {
        try {
            Method method = PlanetaryResourceSet.class.getDeclaredMethod(methodName, value.getClass());
            method.setAccessible(true);
            method.invoke(resources, value);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to modify planetary resource set", ex);
        }
    }
}
