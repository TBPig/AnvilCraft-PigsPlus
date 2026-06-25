---
navigation:
  title: "§5电力附魔台"
  icon: "anvilcraft_pigsplus:electric_enchanting_table"
items:
  - anvilcraft_pigsplus:electric_enchanting_table
---

# 电力附魔台

<recipe id="anvilcraft_pigsplus:electric_enchanting_table"/>

> 昂贵且强大的注魔工具，更便捷的复制，转移与提升附魔等级

## 基本数值(可在config调整)
- 耗电量: 512kW
- 液态经验吸收效率:  100mB/t
- 检测范围: 与附魔台相同
- 检测范围内的每个<ref item="minecraft:bookshelf"/> 减免 4% 经验需求[乘算叠加]

## 工作过程
1. 检测范围内的，<ref item="minecraft:chiseled_bookshelf"/>上的，附魔书的所有附魔
2. 同种附魔取最高等级，随后尝试为物品附魔，并计算需要消耗的液态经验
3. 从检测范围内的<ref item="anvilcraft_pigsplus:experience_interface"/>所依附的容器中吸收液态经验，达到需求时完成工作
4. 将附魔书的附魔复制一份到被附魔的物品
5. 类似于<ref item="anvilcraft:transcendence_anvil"/>，附魔不会被冲突和等级限制，且如果物品已拥有同等级附魔，则升一级
