---
navigation:
  title: "§6Enchanted Generator"
  icon: "anvilcraft_pigsplus:enchanted_generator"
  parent: anvilcraft_guideme:pigsplus.md
items:
  - anvilcraft_pigsplus:enchanted_generator
---

# Enchanted Generator

<item id="anvilcraft_pigsplus:enchanted_generator"/>

# Crafting

<recipe id="anvilcraft_pigsplus:enchanted_generator"/>

# Function

- Working area: 3x3x3 centered on itself
- Can detect enchanted books on <ref item="minecraft:chiseled_bookshelf"/> within the working area

## Normal Working Mode

- Each enchantment level provides 2kW of energy
- Maximum output: 1024kW
- Does not consume enchanted books

## Overclocked Working Mode

When, in *Normal Working Mode*, the energy provided by enchantments exceeds the normal maximum output by 100kW, i.e. reaches 1124kW, it enters *Overclocked Working Mode*

- Each enchantment level provides 16kW of energy (8 times normal mode)
- Maximum output: 32768kW
- Consumes enchanted books; the consumption interval is inversely proportional to the power output, and when the power output reaches its limit, it is 10s

