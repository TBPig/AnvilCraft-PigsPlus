---
navigation:
  title: "§2Budding Echo Shard"
  icon: "anvilcraft_pigsplus:budding_echo_shard"
items:
  - anvilcraft_pigsplus:budding_echo_shard
  - anvilcraft_pigsplus:echo_cluster
  - anvilcraft_pigsplus:echo_geode
---

# Budding Echo Shard

<row halign="center">
<item id="anvilcraft_pigsplus:budding_echo_shard"/>
<item id="anvilcraft_pigsplus:echo_cluster"/>
<item id="anvilcraft_pigsplus:echo_geode"/>
<item id="minecraft:echo_shard"/>
</row>

> Sculk matter shows extremely strong transmutability, but echo shards do not seem to exhibit this property. Perhaps we should try letting them interact with a material with a similar crystal structure.
> When budding amethyst comes into contact with an echo shard, it produces a substance known as a "budding echo shard." It has a powerful transmutative effect on surrounding matter.

# Crafting

<recipe id="anvilcraft:item_inject/budding_echo_shard"/>

# Function

## Producing Echo Shards

1. Place <ref item="minecraft:amethyst_cluster"/> above <ref item="anvilcraft_pigsplus:budding_echo_shard"/>
2. When the <ref item="anvilcraft_pigsplus:budding_echo_shard"/> receives a random tick, it transforms the <ref item="minecraft:amethyst_cluster"/> above it into an <ref item="anvilcraft_pigsplus:echo_cluster"/>
3. When <ref item="anvilcraft_pigsplus:echo_cluster"/> is mined, it always drops 1 <ref item="minecraft:echo_shard"/>

## Producing Sculk Blocks

- When it receives a random tick, it attempts to transform sculk-replaceable blocks within 4 blocks into <ref item="minecraft:sculk"/> and may generate sculk-related blocks on top of them

> Can be used to make XP farms

# <ref item="anvilcraft_pigsplus:echo_geode"/>

Currently has no practical use and only drops from <ref item="anvilcraft_pigsplus:budding_echo_shard"/>

<recipe id="anvilcraft:time_warp/budding_echo_shard"/>
