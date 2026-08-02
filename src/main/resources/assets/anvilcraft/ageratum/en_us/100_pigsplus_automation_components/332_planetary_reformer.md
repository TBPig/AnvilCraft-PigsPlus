---
navigation:
  title: "§5Celestial Reforming"
  icon: "anvilcraft_pigsplus:celestial_reformer_component"
items:
  - anvilcraft_pigsplus:celestial_reformer_component
---

# <ref item="anvilcraft_pigsplus:celestial_reformer_component" />

<recipe id="anvilcraft_pigsplus:celestial_reformer_component"/>

# Planetary Reformer

- Costs **16** Celestial Reformer Components to build
- Can only be built on planetary bodies such as rocky planets and giant planets

# Star Reformer

- Star bodies can have a **Star Reformer** built on them
- Costs **64** Celestial Reformer Components to build

# How Reforming Works

After placing the required materials into the celestial forging anvil logistics interface, the reformer automatically selects a matching recipe:

- If the active recipe's materials are still present, it keeps running
- If the active recipe's materials disappear and another recipe is fully satisfied, it resets progress and switches
- Each absorption takes all matching materials from the interfaces at once; the interval can be changed in config

# Built-in Recipes

<row halign="center">
<recipe id="anvilcraft_pigsplus:celestial_reformer/increase_liquid_coverage"/>
<recipe id="anvilcraft_pigsplus:celestial_reformer/add_biological_resources"/>
</row>

<row halign="center">
<recipe id="anvilcraft_pigsplus:celestial_reformer/add_civilization"/>
<recipe id="anvilcraft_pigsplus:celestial_reformer/void_wasteland"/>
</row>

# Extension and Datapacks

<info>

Other mods can extend recipes without mixins:

1. Extend `ReformerModification` and register it with `DeferredRegister` on `ReformerModifications.REGISTRY`
2. Extend `ReformerRequirement` and register it with `DeferredRegister` on `CelestialReformerRequirements.REGISTRY`
3. Add an `anvilcraft_pigsplus:celestial_reformer` recipe in a datapack, with `modification` pointing to the registered effect id

Pure datapacks can:

- Add or override `celestial_reformer` recipes
- Use already registered `modification` and `requirement` ids
- Override known `planet_resource` recipe ids

Pure datapacks cannot:

- Create new reformer effects or requirement types from scratch
- Make an existing reformer automatically read arbitrary new resource recipes

Current resource recipe limits:

- Normal wasteland reads `anvilcraft:planet_resource/wasteland`
- Void wasteland reads `anvilcraft_pigsplus:planet_resource/void_wasteland`
- Civilization resources read the first matching `OFFERING` recipe

Recipes can include `langs` translation keys, which JEI and Jade use to display the effect.

</info>
