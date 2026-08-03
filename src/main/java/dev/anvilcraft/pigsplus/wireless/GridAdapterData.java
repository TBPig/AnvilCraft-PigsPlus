package dev.anvilcraft.pigsplus.wireless;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.item.GridAdapterItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

final class GridAdapterData extends SavedData {
    private static final String DATA_NAME = AnvilCraftPigsPlus.MOD_ID + "_grid_adapters";
    private static final String BINDINGS_KEY = "Bindings";
    private static final String POS_KEY = "Pos";
    private static final String SIDE_KEY = "Side";
    private static final String MODE_KEY = "Mode";
    private static final String POWER_KEY = "Power";

    private static final Factory<GridAdapterData> FACTORY = new Factory<>(
        GridAdapterData::new,
        GridAdapterData::load
    );

    private final Map<BlockPos, Entry> entries = new HashMap<>();

    private GridAdapterData() {
    }

    static GridAdapterData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    @Nullable Entry get(BlockPos pos) {
        return this.entries.get(pos);
    }

    void set(BlockPos pos, Entry entry) {
        this.entries.put(pos.immutable(), entry);
        this.setDirty();
    }

    void remove(BlockPos pos) {
        if (this.entries.remove(pos) != null) {
            this.setDirty();
        }
    }

    Map<BlockPos, Entry> snapshot() {
        return Map.copyOf(this.entries);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag bindings = new ListTag();
        for (Map.Entry<BlockPos, Entry> entry : this.entries.entrySet()) {
            CompoundTag bindingTag = entry.getValue().save();
            bindingTag.putLong(POS_KEY, entry.getKey().asLong());
            bindings.add(bindingTag);
        }
        tag.put(BINDINGS_KEY, bindings);
        return tag;
    }

    private static GridAdapterData load(CompoundTag tag, HolderLookup.Provider registries) {
        GridAdapterData data = new GridAdapterData();
        ListTag bindings = tag.getList(BINDINGS_KEY, Tag.TAG_COMPOUND);
        for (int index = 0; index < bindings.size(); index++) {
            CompoundTag bindingTag = bindings.getCompound(index);
            Entry entry = Entry.load(bindingTag);
            if (entry != null) {
                data.entries.put(BlockPos.of(bindingTag.getLong(POS_KEY)), entry);
            }
        }
        return data;
    }

    record Entry(Direction side, int mode, int power) {
        CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putInt(SIDE_KEY, this.side.get3DDataValue());
            tag.putInt(MODE_KEY, this.mode);
            tag.putInt(POWER_KEY, this.power);
            return tag;
        }

        static @Nullable Entry load(CompoundTag tag) {
            int side = tag.getInt(SIDE_KEY);
            int mode = tag.getInt(MODE_KEY);
            if (side < 0 || side >= Direction.values().length) return null;
            if (mode != GridAdapterItem.INPUT_MODE && mode != GridAdapterItem.OUTPUT_MODE) {
                return null;
            }
            int power = tag.contains(POWER_KEY)
                ? tag.getInt(POWER_KEY)
                : GridAdapterItem.DEFAULT_POWER;
            power = Math.max(1, power);
            return new Entry(Direction.from3DDataValue(side), mode, power);
        }
    }
}
