---
navigation:
  title: "§d电力附魔台"
  icon: "anvilcraft_pigsplus:electric_enchanting_table"
  position: 311
  parent: anvilcraft_guideme:pigsplus.md
item_ids:
  - anvilcraft_pigsplus:electric_enchanting_table
---

# 电力附魔台

<ItemImage id="anvilcraft_pigsplus:electric_enchanting_table" scale="3"></ItemImage>

# 合成

<Recipe id="anvilcraft_pigsplus:electric_enchanting_table"></Recipe>

# 功能

## 基本数值
- 最大耗电量: 8192kW
- 工作时间: 5s
- 检测范围: 与附魔台相同
- 检测范围内的每个<ItemLink id="minecraft:bookshelf" /> 6% 耗电需求[乘算叠加]

## 工作过程
1. 检测范围内的，<ItemLink id="minecraft:chiseled_bookshelf" />上的，附魔书的所有附魔
2. 同种附魔取最高等级，随后尝试为物品附魔，耗电需求 = 128x+16x^2[x: 使用铁砧附加该附魔，所需的经验等级]
3. 耗电需求为 0 或超过上限时，物品会被强制弹出
4. 类似于<ItemLink id="anvilcraft:transcendence_anvil" />，如果物品已拥有同等级附魔，则升一级，且不会被冲突和等级限制
