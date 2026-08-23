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
2. While working, consumes **liquid experience** based on the enchantment level, drawn from <ref item="anvilcraft_pigsplus:experience_interface"/> (**not tanks**) within the detection range, at a default rate of 1000mB/t per interface
3. Place enchanted books in <ref item="minecraft:chiseled_bookshelf"/> within the detection range
4. With no GUI, right-click while holding an item to insert it. Once <ref item="anvilcraft_pigsplus:electric_enchanting_table"/> absorbs enough liquid experience, it copies the enchantment from the book onto the item
5. Each block that provides enchantment power within the detection range (not necessarily a <ref item="minecraft:bookshelf"/>) reduces the experience demand of <ref item="anvilcraft_pigsplus:electric_enchanting_table"/>
6. The enchanting mechanic is similar to <ref item="anvilcraft:transcendence_anvil"/> — enchantments ignore conflict restrictions and level limits, and if the item already has the same level of enchantment, it is upgraded by one level

<info>
The default experience demand reduction formula is:

y = 1 / (1+0.1x)

y: demand after reduction

x: enchantment power

</info>
