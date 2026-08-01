package dev.anvilcraft.pigsplus.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.anvilcraft.pigsplus.api.modification.ReformerModification;
import dev.anvilcraft.pigsplus.api.requirement.CelestialReformerRequirements;
import dev.anvilcraft.pigsplus.api.requirement.RequirementEntry;
import dev.anvilcraft.pigsplus.api.requirement.ReformerRequirement;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonRecipeTypes;
import dev.dubhe.anvilcraft.recipe.anvil.builder.AbstractRecipeBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.ArrayList;

public record CelestialReformerRecipe(
    ResourceLocation modification,
    List<RequirementEntry> requirements,
    List<ItemInput> items,
    List<FluidInput> fluids,
    List<LaserInput> lasers
) implements Recipe<CelestialReformerRecipe.Input> {

    @Override
    public boolean matches(Input input, Level level) {
        return true;
    }

    @Override
    public ItemStack assemble(Input input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AddonRecipeTypes.CELESTIAL_REFORMER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AddonRecipeTypes.CELESTIAL_REFORMER_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public record Input() implements RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return ItemStack.EMPTY;
        }

        @Override
        public int size() {
            return 0;
        }
    }

    public record ItemInput(ResourceLocation item, int count) {
        public static final MapCodec<ItemInput> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("item").forGetter(ItemInput::item),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("count", 1).forGetter(ItemInput::count)
        ).apply(instance, ItemInput::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ItemInput> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, ItemInput::item,
            ByteBufCodecs.INT, ItemInput::count,
            ItemInput::new
        );
    }

    public record FluidInput(ResourceLocation fluid, int amount) {
        public static final MapCodec<FluidInput> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("fluid").forGetter(FluidInput::fluid),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("amount", 1000).forGetter(FluidInput::amount)
        ).apply(instance, FluidInput::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FluidInput> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, FluidInput::fluid,
            ByteBufCodecs.INT, FluidInput::amount,
            FluidInput::new
        );
    }

    public enum LaserType implements StringRepresentable {
        NORMAL("normal"),
        GAMMA("gamma"),
        ANY("any");

        public static final Codec<LaserType> CODEC = Codec.STRING.xmap(
            LaserType::fromName,
            LaserType::getSerializedName
        );

        private final String name;

        LaserType(String name) {
            this.name = name;
        }

        public static LaserType fromName(String name) {
            return switch (name) {
                case "normal" -> NORMAL;
                case "gamma" -> GAMMA;
                case "any" -> ANY;
                default -> throw new IllegalArgumentException("Unknown laser type: " + name);
            };
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public record LaserInput(int level, LaserType type) {
        public static final MapCodec<LaserInput> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("level").forGetter(LaserInput::level),
            LaserType.CODEC.fieldOf("type").forGetter(LaserInput::type)
        ).apply(instance, LaserInput::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, LaserInput> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, LaserInput::level,
            ByteBufCodecs.STRING_UTF8.map(LaserType::fromName, LaserType::getSerializedName),
            LaserInput::type,
            LaserInput::new
        );
    }

    public static final MapCodec<CelestialReformerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("modification").forGetter(CelestialReformerRecipe::modification),
        RequirementEntry.CODEC.listOf().optionalFieldOf("requirements", List.of())
            .forGetter(CelestialReformerRecipe::requirements),
        ItemInput.CODEC.codec().listOf().optionalFieldOf("items", List.of()).forGetter(CelestialReformerRecipe::items),
        FluidInput.CODEC.codec().listOf().optionalFieldOf("fluids", List.of()).forGetter(CelestialReformerRecipe::fluids),
        LaserInput.CODEC.codec().listOf().optionalFieldOf("lasers", List.of()).forGetter(CelestialReformerRecipe::lasers)
    ).apply(instance, CelestialReformerRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CelestialReformerRecipe> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC, CelestialReformerRecipe::modification,
        RequirementEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), CelestialReformerRecipe::requirements,
        ItemInput.STREAM_CODEC.apply(ByteBufCodecs.list()), CelestialReformerRecipe::items,
        FluidInput.STREAM_CODEC.apply(ByteBufCodecs.list()), CelestialReformerRecipe::fluids,
        LaserInput.STREAM_CODEC.apply(ByteBufCodecs.list()), CelestialReformerRecipe::lasers,
        CelestialReformerRecipe::new
    );

    public static class Serializer implements RecipeSerializer<CelestialReformerRecipe> {
        @Override
        public MapCodec<CelestialReformerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CelestialReformerRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends AbstractRecipeBuilder<CelestialReformerRecipe> {
        private ResourceLocation modification;
        private final List<RequirementEntry> requirements = new ArrayList<>();
        private final List<ItemInput> items = new ArrayList<>();
        private final List<FluidInput> fluids = new ArrayList<>();
        private final List<LaserInput> lasers = new ArrayList<>();

        public Builder modification(ResourceLocation modification) {
            this.modification = modification;
            return this;
        }

        public Builder modification(String modification) {
            this.modification = ResourceLocation.parse(modification);
            return this;
        }

        public Builder modification(DeferredHolder<ReformerModification, ?> modification) {
            return modification(modification.getId());
        }

        public Builder requirement(ResourceLocation requirement) {
            requirements.add(new RequirementEntry(
                requirement,
                CelestialReformerRequirements.REGISTRY.get(requirement)
            ));
            return this;
        }

        public Builder requirement(String requirement) {
            return requirement(ResourceLocation.parse(requirement));
        }

        public Builder requirement(DeferredHolder<ReformerRequirement, ?> requirement) {
            return requirement(requirement.getId());
        }

        public Builder requirement(DeferredHolder<ReformerRequirement, ?> holder, ReformerRequirement requirement) {
            requirements.add(new RequirementEntry(holder.getId(), requirement));
            return this;
        }

        public Builder item(ItemLike item, int count) {
            items.add(new ItemInput(BuiltInRegistries.ITEM.getKey(item.asItem()), count));
            return this;
        }

        public Builder item(ResourceLocation item, int count) {
            items.add(new ItemInput(item, count));
            return this;
        }

        public Builder fluid(ResourceLocation fluid, int amount) {
            fluids.add(new FluidInput(fluid, amount));
            return this;
        }

        public Builder laser(int level) {
            lasers.add(new LaserInput(level, LaserType.ANY));
            return this;
        }

        public Builder laser(int level, boolean gamma) {
            lasers.add(new LaserInput(level, gamma ? LaserType.GAMMA : LaserType.NORMAL));
            return this;
        }

        public Builder laser(int level, LaserType type) {
            lasers.add(new LaserInput(level, type));
            return this;
        }

        @Override
        public CelestialReformerRecipe buildRecipe() {
            return new CelestialReformerRecipe(modification, requirements, items, fluids, lasers);
        }

        @Override
        public void validate(ResourceLocation id) {
            if (modification == null) {
                throw new IllegalArgumentException("Modification must not be null, RecipeId: " + id);
            }
            if (items.isEmpty() && fluids.isEmpty() && lasers.isEmpty()) {
                throw new IllegalArgumentException("Recipe inputs must not be empty, RecipeId: " + id);
            }
        }

        @Override
        public String getType() {
            return "celestial_reformer";
        }

        @Override
        public Item getResult() {
            if (!items.isEmpty()) {
                return BuiltInRegistries.ITEM.get(items.getFirst().item());
            }
            return Items.AIR;
        }

        @Override
        public void save(RecipeOutput output, String id) {
            save(output, AnvilCraftPigsPlus.of("celestial_reformer/" + id));
        }
    }
}
