package dev.anvilcraft.pigsplus.block;

import dev.anvilcraft.pigsplus.inventory.ChainSmithingMenu;
import dev.dubhe.anvilcraft.api.hammer.IHammerRemovable;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SmithingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ChainSmithingTableBlock extends SmithingTableBlock implements IHammerRemovable {
    private static final Component CONTAINER_TITLE = Component.translatable("block.anvilcraft_pigsplus.chain_smithing_table");

    public ChainSmithingTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return new SimpleMenuProvider(
            (i, inventory, player) -> new ChainSmithingMenu(
                i,
                inventory,
                ContainerLevelAccess.create(level, pos)
            ),
            CONTAINER_TITLE
        );
    }
}