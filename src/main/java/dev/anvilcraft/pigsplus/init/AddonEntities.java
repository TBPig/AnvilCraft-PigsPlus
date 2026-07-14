package dev.anvilcraft.pigsplus.init;

import dev.anvilcraft.pigsplus.client.renderer.entity.StalkerRenderer;
import dev.anvilcraft.pigsplus.entity.StalkerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.Supplier;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.REGISTRATE;

public class AddonEntities {
    public static final Supplier<EntityType<StalkerEntity>> STALKER = REGISTRATE
        .entity("stalker", StalkerEntity::new, MobCategory.MONSTER)
        .loot((tables, entityType) -> tables.add(
            entityType, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0f))
                    .add(LootItem.lootTableItem(Items.ECHO_SHARD)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(22.0f)))))
                .withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0f))
                    .add(LootItem.lootTableItem(AddonItems.ECHO_GEODE)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2f)))))
        ))
        .properties(builder -> builder.sized(1f, 2f))
        .renderer(() -> StalkerRenderer::new)
        .register();

    public static void register() {
    }
}
