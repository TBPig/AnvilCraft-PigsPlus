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
     * 把本体按当前天体生成出的生物资源合并进现有资源集。
     */
    public static void addBiologicalResources(CelestialForgingAnvilBlockEntity be) {
        CelestialBodyData body = be.getCelestialBodyData();
        if (body == null) return;
        PlanetaryResourceSet generated = null;
        for (int i = 0; i < 64; i++) {
            PlanetaryResourceSet candidate = generate(be, body, i);
            if (!candidate.getBiologicalItems().isEmpty() || !candidate.getBiologicalFluids().isEmpty()) {
                generated = candidate;
                break;
            }
        }
        if (generated == null) {
            generated = generate(be, body, 0);
        }
        PlanetaryResourceSet resources = be.getPlanetaryResourceSet();
        if (resources == null) {
            resources = generated;
        } else {
            listField(resources, "biologicalItems").addAll(generated.getBiologicalItems());
            listField(resources, "biologicalFluids").addAll(generated.getBiologicalFluids());
        }
        be.setPlanetaryResourceSet(resources);
        be.setChanged();
    }

    /**
     * 添加低等文明，并清空生物资源。
     */
    public static void addCivilization(CelestialForgingAnvilBlockEntity be) {
        PlanetaryResourceSet resources = ensureResources(be);
        listField(resources, "biologicalItems").clear();
        listField(resources, "biologicalFluids").clear();
        invokeVoid(resources, "setHasCivilization");
        be.setChanged();
    }

    /**
     * 转为废土世界；虚空废土使用虚空物质替换粗铀与钚粒。
     */
    public static void setWasteland(CelestialForgingAnvilBlockEntity be, boolean voidWasteland) {
        PlanetaryResourceSet resources = ensureResources(be);
        listField(resources, "biologicalItems").clear();
        listField(resources, "biologicalFluids").clear();
        listField(resources, "offerings").clear();
        listField(resources, "wastelandItems").clear();
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
