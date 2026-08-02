package dev.anvilcraft.pigsplus.util;

import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialBodyData;
import dev.dubhe.anvilcraft.block.entity.celestial.LiquidCoverage;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetResourceInput;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetResourceGenerator;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetResourceRecipe;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetResourceRecipe.WeightedEntry;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetaryResourceSet;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetaryResourceSet.WeightedFluidStack;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetaryResourceSet.WeightedItemStack;
import dev.dubhe.anvilcraft.block.entity.celestial.RockyPlanetData;
import dev.dubhe.anvilcraft.init.recipe.ModRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 天体改造的公共资源操作工具，只放置会被多个改造复用的方法。
 */
public final class CelestialReformerPlanetUtil {
    private CelestialReformerPlanetUtil() {
    }

    /**
     * 更新天体参数，并按照本体规则重新生成资源集。
     */
    public static void regenerate(CelestialForgingAnvilBlockEntity be, CelestialBodyData body) {
        regenerate(be, body, getOceanFluid(be));
    }

    /**
     * 更新天体参数并重新生成资源集，同时指定海洋液体。
     */
    public static void regenerate(
        CelestialForgingAnvilBlockEntity be,
        CelestialBodyData body,
        ResourceLocation oceanFluid
    ) {
        PlanetaryResourceSet resources = generate(be, body);
        if (body instanceof RockyPlanetData rp
            && rp.liquidCoverage() != LiquidCoverage.NONE
            && oceanFluid != null) {
            setOceanFluid(resources, oceanFluid);
        }
        be.setCelestialBodyData(body);
        be.setPlanetaryResourceSet(resources);
    }

    /**
     * 获取当前行星海洋使用的流体 id；无液体时返回 null。
     */
    public static @Nullable ResourceLocation getOceanFluid(CelestialForgingAnvilBlockEntity be) {
        if (!(be.getCelestialBodyData() instanceof RockyPlanetData rp)
            || rp.liquidCoverage() == LiquidCoverage.NONE) {
            return null;
        }
        PlanetaryResourceSet resources = be.getPlanetaryResourceSet();
        if (resources == null) return null;
        List<WeightedFluidStack> fluids = resources.getFluids();
        return fluids.isEmpty() ? null : fluids.getFirst().fluidId();
    }

    /**
     * 将行星表面流体替换为指定液体。
     */
    public static void setOceanFluid(PlanetaryResourceSet resources, ResourceLocation fluid) {
        listField(resources, "fluids").clear();
        invokeAdd(resources, "addFluid", new WeightedFluidStack(fluid, 100));
    }

    /**
     * 按本体规则生成一份行星资源集。
     */
    public static PlanetaryResourceSet generate(
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

    /**
     * 清空废土相关资源并设置废土状态。
     */
    public static PlanetaryResourceSet resetWasteland(CelestialForgingAnvilBlockEntity be) {
        PlanetaryResourceSet resources = ensureResources(be);
        listField(resources, "biologicalItems").clear();
        listField(resources, "biologicalFluids").clear();
        listField(resources, "offerings").clear();
        listField(resources, "wastelandItems").clear();
        setField(resources, "hasCivilization", false);
        invokeVoid(resources, "setWasteland");
        return resources;
    }

    public static void addWastelandItem(PlanetaryResourceSet resources, ResourceLocation item, int weight) {
        invokeAdd(resources, "addWastelandItem", new WeightedItemStack(item, weight));
    }

    /**
     * 获取指定类别下第一条匹配的本体行星资源配方条目。
     */
    public static List<WeightedEntry> getPlanetResourceEntries(
        CelestialForgingAnvilBlockEntity be,
        PlanetResourceRecipe.Category category
    ) {
        PlanetResourceRecipe recipe = findPlanetResourceRecipe(be, category);
        if (recipe == null) return List.of();
        return switch (category) {
            case WASTELAND -> {
                PlanetResourceRecipe.WastelandData data = recipe.wastelandData();
                yield data == null ? List.of() : data.entries();
            }
            case OFFERING -> {
                PlanetResourceRecipe.OfferingData data = recipe.offeringData();
                yield data == null ? List.of() : data.entries();
            }
            default -> List.of();
        };
    }

    /**
     * 获取指定 id 的废土资源配方条目。
     */
    public static List<WeightedEntry> getWastelandEntries(
        CelestialForgingAnvilBlockEntity be,
        ResourceLocation recipeId
    ) {
        PlanetResourceRecipe recipe = findPlanetResourceRecipe(be, recipeId);
        if (recipe == null || recipe.category() != PlanetResourceRecipe.Category.WASTELAND) return List.of();
        PlanetResourceRecipe.WastelandData data = recipe.wastelandData();
        return data == null ? List.of() : data.entries();
    }

    public static @Nullable PlanetResourceRecipe findPlanetResourceRecipe(
        CelestialForgingAnvilBlockEntity be,
        ResourceLocation recipeId
    ) {
        if (be.getLevel() == null) return null;
        RecipeHolder<?> holder = be.getLevel().getRecipeManager().byKey(recipeId).orElse(null);
        return holder != null && holder.value() instanceof PlanetResourceRecipe recipe ? recipe : null;
    }

    public static @Nullable PlanetResourceRecipe findPlanetResourceRecipe(
        CelestialForgingAnvilBlockEntity be,
        PlanetResourceRecipe.Category category
    ) {
        if (be.getLevel() == null) return null;
        CelestialBodyData body = be.getCelestialBodyData();
        if (body == null) return null;
        PlanetResourceInput input = new PlanetResourceInput(body, be.getAgeAnvilCount());
        for (RecipeHolder<PlanetResourceRecipe> holder :
            be.getLevel().getRecipeManager().getAllRecipesFor(ModRecipeTypes.PLANET_RESOURCE_TYPE.get())) {
            PlanetResourceRecipe recipe = holder.value();
            if (recipe.category() == category && recipe.matches(input, be.getLevel())) {
                return recipe;
            }
        }
        return null;
    }

    public static PlanetaryResourceSet ensureResources(CelestialForgingAnvilBlockEntity be) {
        PlanetaryResourceSet resources = be.getPlanetaryResourceSet();
        if (resources == null) {
            resources = new PlanetaryResourceSet();
            be.setPlanetaryResourceSet(resources);
        }
        return resources;
    }

    @SuppressWarnings("unchecked")
    public static List<Object> listField(PlanetaryResourceSet resources, String fieldName) {
        try {
            var field = PlanetaryResourceSet.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (List<Object>) field.get(resources);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to modify planetary resource set", ex);
        }
    }

    public static void setField(PlanetaryResourceSet resources, String fieldName, boolean value) {
        try {
            var field = PlanetaryResourceSet.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.setBoolean(resources, value);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to modify planetary resource set", ex);
        }
    }

    public static void invokeVoid(PlanetaryResourceSet resources, String methodName) {
        try {
            Method method = PlanetaryResourceSet.class.getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(resources);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to modify planetary resource set", ex);
        }
    }

    public static void invokeAdd(PlanetaryResourceSet resources, String methodName, Object value) {
        try {
            Method method = PlanetaryResourceSet.class.getDeclaredMethod(methodName, value.getClass());
            method.setAccessible(true);
            method.invoke(resources, value);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to modify planetary resource set", ex);
        }
    }

    private static PlanetaryResourceSet generate(CelestialForgingAnvilBlockEntity be, CelestialBodyData body) {
        return generate(be, body, 0);
    }
}
