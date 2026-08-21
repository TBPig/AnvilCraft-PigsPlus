---
navigation:
  title: "§5电力附魔台"
  icon: "anvilcraft_pigsplus:electric_enchanting_table"
items:
  - anvilcraft_pigsplus:electric_enchanting_table
---

# 电力附魔台

<recipe id="anvilcraft_pigsplus:electric_enchanting_table"/>

> 昂贵而强大的注魔工具，可便捷地复制、转移与提升附魔等级

# 检测范围

<ref item="anvilcraft_pigsplus:electric_enchanting_table"/>的检测范围和<ref item="minecraft:enchanting_table"/>一致，但不会被中间的方块影响

<structure id="../../structures/electric_enchanting_table.nbt" />

## 工作过程

1. 消耗 **256kW** 能量（可在配置中调整）
2. 工作时根据附魔等级消耗**液态经验**，从检测范围内的<ref item="anvilcraft_pigsplus:experience_interface"/>（而非储罐）中抽取，单个接口提取速率：100mB/t
3. 在检测范围内<ref item="minecraft:chiseled_bookshelf"/>中放置附魔书
4. 无GUI，持物品右击放入，<ref item="anvilcraft_pigsplus:electric_enchanting_table"/>吸收足量液态经验后，将附魔书的附魔复制一份赋予物品
5. 检测范围内的每个<ref item="minecraft:bookshelf"/>提供的附魔能力（也可以不是书架），为<ref item="anvilcraft_pigsplus:electric_enchanting_table"/>减免经验需求
6. 附魔机制类似于<ref item="anvilcraft:transcendence_anvil"/>，无视附魔冲突与等级上限，且若物品已拥有同等级附魔，则提升一级

<info>
经验需求的减免计算公式默认为： 

```
y = 1 / (1+0.1x)
y: 减免后需求
x: 附魔能力
```

</info>

