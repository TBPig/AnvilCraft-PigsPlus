---
navigation:
  title: "§5电力附魔台"
  icon: "anvilcraft_pigsplus:electric_enchanting_table"
  parent: anvilcraft_guideme:pigsplus.md
items:
  - anvilcraft_pigsplus:electric_enchanting_table
---

# 电力附魔台

<recipe id="anvilcraft_pigsplus:electric_enchanting_table"/>

> 昂贵且强大的注魔工具，更便捷的复制，转移与提升附魔等级

## 基本数值
- 最大耗电量: 8192kW
- 工作时间: 5s
- 检测范围: 与附魔台相同
- 检测范围内的每个<ref item="minecraft:bookshelf"/> 减免 6% 耗电需求[乘算叠加]

## 工作过程
1. 检测范围内的，<ref item="minecraft:chiseled_bookshelf"/>上的，附魔书的所有附魔
2. 同种附魔取最高等级，随后尝试为物品附魔，默认耗电需求 = 128x+16x^2[x: 使用铁砧附加该附魔，所需的经验等级]
3. 耗电需求为 0 或超过上限时，物品会被强制弹出
4. 类似于<ref item="anvilcraft:transcendence_anvil"/>，附魔不会被冲突和等级限制，且如果物品已拥有同等级附魔，则升一级
