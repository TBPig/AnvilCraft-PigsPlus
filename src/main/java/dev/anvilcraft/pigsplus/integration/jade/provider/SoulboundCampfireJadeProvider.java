package dev.anvilcraft.pigsplus.integration.jade.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.block.entity.SoulboundCampfireBlockEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ItemView;
import snownee.jade.api.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum SoulboundCampfireJadeProvider
    implements IServerExtensionProvider<ItemStack>, IClientExtensionProvider<ItemStack, ItemView> {
    INSTANCE;

    private static final MapCodec<Integer> COOKING_TIME_CODEC = Codec.INT.fieldOf("anvilcraft_pigsplus:cooking");

    @Override
    public ResourceLocation getUid() {
        return AnvilCraftPigsPlus.of("soulbound_campfire");
    }

    @Override
    public List<ClientViewGroup<ItemView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<ItemStack>> groups) {
        return ClientViewGroup.map(groups, stack -> {
            CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            if (customData.isEmpty()) {
                return null;
            }
            Optional<Integer> remaining = customData.read(COOKING_TIME_CODEC).result();
            if (remaining.isEmpty()) {
                return null;
            }
            String text = IThemeHelper.get().seconds(remaining.get(), accessor.tickRate()).getString();
            return new ItemView(stack).amountText(text);
        }, null);
    }

    @Override
    public @Nullable List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
        if (!(accessor.getTarget() instanceof SoulboundCampfireBlockEntity campfire)) {
            return null;
        }
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < campfire.getItems().size(); i++) {
            ItemStack stack = campfire.getItems().get(i);
            if (stack.isEmpty()) {
                continue;
            }
            ItemStack displayed = stack.copy();
            CustomData customData = displayed
                .getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .update(NbtOps.INSTANCE, COOKING_TIME_CODEC, campfire.getRemainingCookTicks(i))
                .getOrThrow();
            displayed.set(DataComponents.CUSTOM_DATA, customData);
            stacks.add(displayed);
        }
        return List.of(new ViewGroup<>(stacks));
    }
}
