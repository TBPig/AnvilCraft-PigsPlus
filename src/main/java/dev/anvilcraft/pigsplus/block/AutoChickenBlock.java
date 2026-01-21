package dev.anvilcraft.pigsplus.block;

import dev.dubhe.anvilcraft.api.hammer.IHammerRemovable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class AutoChickenBlock extends Block implements IHammerRemovable {
    public AutoChickenBlock(Properties properties) {
        super(properties);
    }

    public void spawnEgg(ServerLevel level, BlockPos pos) {
        BlockState breakerBlockState = level.getBlockState(pos);
        if (breakerBlockState.isAir()) return;
        // 在方块下方生成一个鸡蛋掉落物
        ItemStack itemStack = new ItemStack(Items.EGG);
        Vec3 itemPos = pos.below().getCenter();
        level.addFreshEntity(
            new ItemEntity(
                level,
                itemPos.x,
                itemPos.y,
                itemPos.z,
                itemStack,
                level.random.nextDouble() * 0.2 - 0.1,
                -0.1,
                level.random.nextDouble() * 0.2 - 0.1
            )
        );
    }
}
