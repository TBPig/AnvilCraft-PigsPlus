package dev.anvilcraft.pigsplus.api.requirement;

import com.mojang.serialization.MapCodec;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import net.minecraft.ChatFormatting;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class ReformerRequirement {
    /**
     * 校验需求是否成立。
     */
    public abstract boolean test(CelestialForgingAnvilBlockEntity be);

    /**
     * 获取用于展示的描述组件，格式化方式由子类自行决定。
     */
    public abstract Component getDescription();

    /**
     * 获取用于 JEI/Jade/Ageratum 显示的图标。
     */
    public abstract ResourceLocation getIcon();

    /**
     * 返回该需求类型的编解码器。
     *
     * <p>无参数类型返回 {@link MapCodec#unit(Object)}；带参数的类型返回可重建实例的
     * {@code MapCodec}，参数由配方 data.json 中的字段提供。</p>
     */
    public abstract MapCodec<? extends ReformerRequirement> codec();

    /**
     * 生成默认红色样式的翻译文本。
     */
    protected final Component text(String key, Object... args) {
        return Component.translatable(key, args).withStyle(ChatFormatting.RED);
    }

    /**
     * 根据图标文件名生成完整资源路径。
     */
    protected final ResourceLocation icon(String name) {
        return AnvilCraftPigsPlus.of("textures/gui/reformer/" + name + ".png");
    }
}
