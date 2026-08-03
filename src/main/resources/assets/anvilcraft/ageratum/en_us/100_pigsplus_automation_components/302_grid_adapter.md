---
navigation:
  title: "§5Grid Adapter"
  icon: "anvilcraft_pigsplus:grid_adapter"
items:
  - anvilcraft_pigsplus:grid_adapter
---

# Grid Adapter

<recipe id="anvilcraft_pigsplus:grid_adapter"/>

Connects AnvilCraft's power grid and ordinary FE machines

# Modes

Hold [Alt] by default to switch modes

## Input: AnvilCraft kW -> FE/t

- Right-click to set the **maximum** input value
- Shift + right-click an FE block to bind or unbind
- If the target block cannot receive FE, the adapter stops consuming

## Output: FE/t -> AnvilCraft kW

- Right-click to set the **constant** output value
- Shift + right-click an FE block to bind or unbind
- Must extract the full required FE in one go to work; no partial extraction

<tip>
The constant output value is meant to ensure that even when generation is insufficient, it can still work normally for short periods from time to time, because the AnvilCraft power grid cannot store energy.
</tip>
