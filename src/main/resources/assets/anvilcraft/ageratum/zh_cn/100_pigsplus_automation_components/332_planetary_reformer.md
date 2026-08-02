---
navigation:
  title: "§5天体改造"
  icon: "anvilcraft_pigsplus:celestial_reformer_component"
items:
  - anvilcraft_pigsplus:celestial_reformer_component
---

# <ref item="anvilcraft_pigsplus:celestial_reformer_component" />

<recipe id="anvilcraft_pigsplus:celestial_reformer_component"/>

# 行星改造器

- 建造需要消耗 **16 个天体改造器部件**
- 只能在岩石行星、巨行星等行星类天体上建造

# 恒星改造器

- 恒星天体可以建造**恒星改造器**
- 建造需要消耗 **64 个天体改造器部件**

# 改造方式

将对应材料放入锻星砧物流接口后，行星改造器会自动选择满足条件的配方：

- 如果正在执行的配方材料仍然存在，则继续执行当前改造
- 如果当前配方材料消失，但出现了其他配方材料且完全满足其他配方，则重置进度并切换改造
- 每次吸收会一次性取走接口内所有对应材料；单次吸收间隔可以通过配置项更改

# 内置配方

<row halign="center">
<recipe id="anvilcraft_pigsplus:celestial_reformer/increase_liquid_coverage"/>
<recipe id="anvilcraft_pigsplus:celestial_reformer/add_biological_resources"/>
</row>

<row halign="center">
<recipe id="anvilcraft_pigsplus:celestial_reformer/add_civilization"/>
<recipe id="anvilcraft_pigsplus:celestial_reformer/void_wasteland"/>
</row>

# 扩展与数据包

<info>

其他模组无需 mixin 即可扩展配方：

1. 继承 `ReformerModification`，在 `ReformerModifications.REGISTRY` 上使用 `DeferredRegister` 注册
2. 继承 `ReformerRequirement`，在 `CelestialReformerRequirements.REGISTRY` 上使用 `DeferredRegister` 注册
3. 在数据包中新增 `anvilcraft_pigsplus:celestial_reformer` 配方，将 `modification` 指向已注册的效果 id

纯数据包可以：

- 添加或覆盖 `celestial_reformer` 配方
- 使用已经注册的 `modification` 和 `requirement` id
- 覆盖已知的 `planet_resource` 配方 id

纯数据包不能：

- 凭空创建新的改造效果或需求类型
- 让现有改造自动读取任意新增资源配方

当前资源读取限制：

- 普通废土读取 `anvilcraft:planet_resource/wasteland`
- 虚空废土读取 `anvilcraft_pigsplus:planet_resource/void_wasteland`
- 文明资源读取第一条匹配的 `OFFERING` 配方

配方可以附带 `langs` 翻译键，JEI 与 Jade 会通过它们显示改造内容。

</info>
