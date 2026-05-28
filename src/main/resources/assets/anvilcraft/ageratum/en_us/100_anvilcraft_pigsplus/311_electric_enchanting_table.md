---
navigation:
  title: "§5Electric Enchanting Table"
  icon: "anvilcraft_pigsplus:electric_enchanting_table"
  parent: anvilcraft_guideme:pigsplus.md
items:
  - anvilcraft_pigsplus:electric_enchanting_table
---

# Electric Enchanting Table

<recipe id="anvilcraft_pigsplus:electric_enchanting_table"/>

> An expensive and powerful enchanting tool for easier duplication, transfer, and upgrading of enchantments

## Basic Stats
- Maximum power consumption: 8192kW
- Working time: 5s
- Detection range: same as the enchanting table
- Each <ref item="minecraft:bookshelf"/> within the detection range reduces power demand by 6% [multiplicative stacking]

## Work Process
1. Detect all enchantments on enchanted books on <ref item="minecraft:chiseled_bookshelf"/> within the detection range
2. For enchantments of the same type, take the highest level, then try to enchant the item. Default power demand = 128x+16x^2 [x: the experience levels required to apply that enchantment with an anvil]
3. If the power demand is 0 or exceeds the upper limit, the item will be forcibly ejected
4. Similar to <ref item="anvilcraft:transcendence_anvil"/>, enchantments are not affected by conflicts or level limits, and if the item already has an enchantment of the same level, it is upgraded by one level

