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

# Detection Range

The detection range of <ref item="anvilcraft_pigsplus:electric_enchanting_table"/> is the same as <ref item="minecraft:enchanting_table"/>, but is not obstructed by blocks placed between them

<structure id="../../structures/electric_enchanting_table.nbt" />

## Work Process

1. Consumes **256kW** of power (adjustable in config)
2. While working, consumes **liquid experience** based on the enchantment level, drawn from <ref item="anvilcraft_pigsplus:experience_interface"/> (**not tanks**) within the detection range, at a rate of 100mB/t per interface
3. Place enchanted books on <ref item="minecraft:chiseled_bookshelf"/> within the detection range
4. Place the item inside. Once <ref item="anvilcraft_pigsplus:electric_enchanting_table"/> absorbs enough liquid experience, it copies the enchantment from the book onto the item
5. Each <ref item="minecraft:bookshelf"/> within the detection range reduces the experience demand of <ref item="anvilcraft_pigsplus:electric_enchanting_table"/> by 5% [multiplicative stacking]
6. The enchanting mechanic is similar to <ref item="anvilcraft:transcendence_anvil"/> — enchantments ignore conflict restrictions and level limits, and if the item already has the same level of enchantment, it is upgraded by one level

