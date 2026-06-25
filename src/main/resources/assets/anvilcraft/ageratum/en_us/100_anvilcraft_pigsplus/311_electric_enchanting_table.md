---
navigation:
  title: "§5Electric Enchanting Table"
  icon: "anvilcraft_pigsplus:electric_enchanting_table"
items:
  - anvilcraft_pigsplus:electric_enchanting_table
---

# Electric Enchanting Table

<recipe id="anvilcraft_pigsplus:electric_enchanting_table"/>

> An expensive and powerful enchanting tool for easier duplication, transfer, and upgrading of enchantments

## Basic Stats (adjustable via config)
- Power consumption: 512kW
- Liquid experience absorption rate: 100mB/t
- Detection range: same as the enchanting table
- Each <ref item="minecraft:bookshelf"/> within the detection range reduces experience demand by 4% [multiplicative stacking]

## Work Process
1. Detect all enchantments on enchanted books on <ref item="minecraft:chiseled_bookshelf"/> within the detection range
2. For enchantments of the same type, take the highest level, then try to enchant the item and calculate the liquid experience required
3. Absorb liquid experience from containers attached to <ref item="anvilcraft_pigsplus:experience_interface"/> within the detection range; work completes when enough is absorbed
4. Copy a duplicate of the enchantment from the enchanted book to the item
5. Similar to <ref item="anvilcraft:transcendence_anvil"/>, enchantments are not affected by conflicts or level limits, and if the item already has an enchantment of the same level, it is upgraded by one level

