package dev.anvilcraft.pigsplus.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.anvilcraft.lib.v2.util.predicate.BlockStatePredicate;
import dev.anvilcraft.lib.v2.util.predicate.ChanceItemStack;
import dev.anvilcraft.lib.v2.util.predicate.ItemIngredientPredicate;
import dev.anvilcraft.pigsplus.block.PecisionMagneticPivotBlock;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonRecipeTypes;
import dev.dubhe.anvilcraft.recipe.anvil.util.WrapUtils;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.AbstractProcessRecipe;
import dev.dubhe.anvilcraft.recipe.component.HasCauldronSimple;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

/**
 * 精密电磁加工配方
 *
 * <p>该配方用于在铁砧下落时产生精密电磁加工效果，需要在铁砧下方放置激活的精密磁枢作为触发条件</p>
 */
@SuppressWarnings("unused")
@Getter
public class PrecisionElectromagneticProcessingRecipe extends AbstractProcessRecipe<PrecisionElectromagneticProcessingRecipe> {
    /**
     * 构造一个精密电磁加工配方
     *
     * @param itemIngredients 物品原料列表
     * @param results         结果物品列表
     * @param hasCauldron     炼药锅条件
     */
    public PrecisionElectromagneticProcessingRecipe(
        List<ItemIngredientPredicate> itemIngredients,
        List<ChanceItemStack> results,
        HasCauldronSimple hasCauldron
    ) {
        super(
            new Property()
                .setItemInputOffset(new Vec3(0.0, -0.375, 0.0))
                .setItemInputRange(new Vec3(0.75, 0.75, 0.75))
                .setInputItems(itemIngredients)
                .setItemOutputOffset(new Vec3(0.0, -0.75, 0.0))
                .setResultItems(results)
                .setCauldronOffset(new Vec3i(0, -1, 0))
                .setHasCauldron(hasCauldron)
                .setBlockInputOffset(new Vec3i(0, -2, 0))
                .setInputBlocks(
                    BlockStatePredicate.builder()
                        .of(AddonBlocks.PRECISION_MAGNETIC_PIVOT.get())
                        .with(PecisionMagneticPivotBlock.LIT, true)
                        .build()
                )
        );
    }

    @Override
    public RecipeSerializer<PrecisionElectromagneticProcessingRecipe> getSerializer() {
        return AddonRecipeTypes.PRECISION_ELECTROMAGNETIC_PROCESSING_SERIALIZER.get();
    }

    @Override
    public RecipeType<PrecisionElectromagneticProcessingRecipe> getType() {
        return AddonRecipeTypes.PRECISION_ELECTROMAGNETIC_PROCESSING_TYPE.get();
    }

    /**
     * 创建一个构建器实例
     *
     * @return 构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 是否消耗流体
     *
     * @return 如果消耗流体返回true，否则返回false
     */
    public boolean isConsumeFluid() {
        HasCauldronSimple hasCauldron = this.getHasCauldron();
        return hasCauldron.hasFluid() && hasCauldron.consume() > 0;
    }

    /**
     * 是否产生流体
     *
     * @return 如果产生流体返回true，否则返回false
     */
    public boolean isProduceFluid() {
        HasCauldronSimple hasCauldron = this.getHasCauldron();
        return !hasCauldron.transforms().isEmpty();
    }

    /**
     * 精密电磁加工配方序列化器
     */
    public static class Serializer implements RecipeSerializer<PrecisionElectromagneticProcessingRecipe> {
        /**
         * 编解码器
         */
        private static final MapCodec<PrecisionElectromagneticProcessingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemIngredientPredicate.CODEC.listOf()
                .optionalFieldOf("ingredients", List.of())
                .forGetter(PrecisionElectromagneticProcessingRecipe::getInputItems),
            ChanceItemStack.CODEC.listOf()
                .optionalFieldOf("results", List.of())
                .forGetter(PrecisionElectromagneticProcessingRecipe::getResultItems),
            HasCauldronSimple.CODEC
                .forGetter(PrecisionElectromagneticProcessingRecipe::getHasCauldron)
        ).apply(instance, PrecisionElectromagneticProcessingRecipe::new));

        /**
         * 流编解码器
         */
        private static final StreamCodec<RegistryFriendlyByteBuf, PrecisionElectromagneticProcessingRecipe> STREAM_CODEC = StreamCodec.composite(
            ItemIngredientPredicate.STREAM_CODEC.apply(ByteBufCodecs.list()),
            PrecisionElectromagneticProcessingRecipe::getInputItems,
            ChanceItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
            PrecisionElectromagneticProcessingRecipe::getResultItems,
            HasCauldronSimple.STREAM_CODEC,
            PrecisionElectromagneticProcessingRecipe::getHasCauldron,
            PrecisionElectromagneticProcessingRecipe::new
        );

        @Override
        public MapCodec<PrecisionElectromagneticProcessingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PrecisionElectromagneticProcessingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    /**
     * 精密电磁加工配方构建器
     */
    public static class Builder extends SimpleAbstractBuilder<PrecisionElectromagneticProcessingRecipe, Builder> {
        /**
         * 炼药锅条件构建器
         */
        HasCauldronSimple.Builder hasCauldron = HasCauldronSimple.empty();

        public Builder fluid(Fluid fluid) {
            this.hasCauldron.fluid(fluid);
            return this;
        }

        public Builder fluid(Holder<Fluid> fluid) {
            this.hasCauldron.fluid(fluid);
            return this;
        }

        /**
         * 设置炼药锅方块
         *
         * @param cauldron 炼药锅方块
         * @return 构建器实例
         */
        public Builder fluid(Block cauldron) {
            return this.fluid(BuiltInRegistries.FLUID.get(WrapUtils.cauldron2Fluid(cauldron)));
        }

        /**
         * 设置转换后的流体
         *
         * @param transform 转换后的流体ID
         * @return 构建器实例
         */
        public Builder transform(Fluid transform, int produce) {
            this.hasCauldron.transform(transform, produce);
            return this;
        }

        public Builder transform(Holder<Fluid> transform, int produce) {
            this.hasCauldron.transform(transform, produce);
            return this;
        }

        public Builder transform(Block cauldron, int produce) {
            return this.transform(BuiltInRegistries.FLUID.get(WrapUtils.cauldron2Fluid(cauldron)), produce);
        }

        public Builder transform(FluidStack transform) {
            this.hasCauldron.transform(transform);
            return this;
        }

        /**
         * 设置消耗量
         *
         * @param consume 消耗量
         * @return 构建器实例
         */
        public Builder consume(int consume) {
            this.hasCauldron.consume(consume);
            return this;
        }


        /**
         * 设置需要点燃锅
         *
         * @return 构建器实例
         */
        public Builder ignite() {
            this.hasCauldron.ignite();
            return this;
        }

        @Override
        protected PrecisionElectromagneticProcessingRecipe of(
            List<ItemIngredientPredicate> itemIngredients,
            List<ChanceItemStack> results
        ) {
            return new PrecisionElectromagneticProcessingRecipe(
                itemIngredients,
                results,
                this.hasCauldron.build()
            );
        }

        @Override
        public void validate(ResourceLocation id) {
            HasCauldronSimple hasCauldronSimple = this.hasCauldron.build();
            if (itemIngredients.isEmpty() && !hasCauldronSimple.hasFluid()) {
                throw new IllegalArgumentException("Recipe input must not be empty, RecipeId: " + id);
            }
        }

        @Override
        public String getType() {
            return "precision_electromagnetic_processing";
        }

        @Override
        protected Builder getThis() {
            return this;
        }
    }
}
