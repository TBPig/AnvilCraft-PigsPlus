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
<recipe id="anvilcraft_pigsplus:celestial_reformer/slow_rotation"/>
<recipe id="anvilcraft_pigsplus:celestial_reformer/fast_rotation"/>
</row>

<row halign="center">
<recipe id="anvilcraft_pigsplus:celestial_reformer/star_strengthen_magnetic_field"/>
<recipe id="anvilcraft_pigsplus:celestial_reformer/star_weaken_magnetic_field"/>
</row>

<info>

其他附属模组可以注册新的行星改造效果：

1. 继承 `ReformerModification`
2. 在 `ReformerModifications.REGISTRY` 上使用 `DeferredRegister` 注册
3. 在数据包中新增 `anvilcraft_pigsplus:celestial_reformer` 配方，将 `modification` 指向注册的效果 id
4. 配方可以附带多个 `langs` 翻译键，JEI 与 Jade 会通过翻译键显示改造内容

配方还可以通过`requirements`添加需求条目，例如限制行星/恒星改造器、天体环境、转速、磁场或巨构状态

需求条目同样通过注册表扩展

</info>
