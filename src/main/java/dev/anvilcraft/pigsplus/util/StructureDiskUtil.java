package dev.anvilcraft.pigsplus.util;

import dev.dubhe.anvilcraft.init.item.ModComponents;
import dev.dubhe.anvilcraft.item.property.component.StructureDiskData;
import net.minecraft.SharedConstants;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class StructureDiskUtil {

    public static boolean saveData(Level level, Direction direction, ItemStack held, CompoundTag tag) {
        try {
            MinecraftServer server = level.getServer();
            if (server == null) return false;
            Path baseDir = server.getWorldPath(LevelResource.ROOT)
                .toAbsolutePath()
                .normalize()
                .resolve("anvilcraft")
                .resolve("structures");
            UUID uuid = UUID.randomUUID();
            String name = "memory_block_comparator";
            String fileName = "%s_%s.nbt".formatted(name, uuid);
            Path structureFile = baseDir.resolve(fileName);
            Files.createDirectories(baseDir);
            NbtIo.writeCompressed(tag, structureFile);

            StructureDiskData diskData = new StructureDiskData(fileName, name, uuid, direction, 1, 1, 1, false);
            held.set(ModComponents.STRUCTURE_DISK_DATA, diskData);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static CompoundTag getCompoundTag(BlockState remembered) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("DataVersion", SharedConstants.getCurrentVersion().getDataVersion().getVersion());
        tag.putString("author", "Memory Block Comparator");

        ListTag sizeTag = new ListTag();
        sizeTag.add(IntTag.valueOf(1));
        sizeTag.add(IntTag.valueOf(1));
        sizeTag.add(IntTag.valueOf(1));
        tag.put("size", sizeTag);

        ListTag paletteTag = new ListTag();
        paletteTag.add(NbtUtils.writeBlockState(remembered));
        tag.put("palette", paletteTag);

        ListTag blocksTag = new ListTag();
        CompoundTag blockTag = new CompoundTag();
        ListTag posTag = new ListTag();
        posTag.add(IntTag.valueOf(0));
        posTag.add(IntTag.valueOf(0));
        posTag.add(IntTag.valueOf(0));
        blockTag.put("pos", posTag);
        blockTag.putInt("state", 0);
        blocksTag.add(blockTag);
        tag.put("blocks", blocksTag);
        tag.put("entities", new ListTag());
        return tag;
    }
}
