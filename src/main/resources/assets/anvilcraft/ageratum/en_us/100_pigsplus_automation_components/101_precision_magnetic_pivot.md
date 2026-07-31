---
navigation:
  title: "§2Precision Magnetic Pivot"
  icon: "anvilcraft_pigsplus:precision_magnetic_pivot"
items:
  - anvilcraft_pigsplus:precision_magnetic_pivot
  - anvilcraft_pigsplus:universal_redstone_component
---

# <ref item="anvilcraft_pigsplus:precision_magnetic_pivot"/>

> A poor imitation of Factorio's electromagnetic plant — but it does imitate the electromagnetic plant

<recipe id="anvilcraft_pigsplus:precision_magnetic_pivot"/>

# Operation

- Uses a special activation method: rub <ref item="anvilcraft:magnet_block"/> (Magnet Blocks) around the four sides of <ref item="anvilcraft_pigsplus:precision_magnetic_pivot"/>
- Rub the four sides **horizontally** in clockwise or counterclockwise order to enter a 2-second working state
- If the rubbing order is incorrect, the sequence resets
- A Precision Magnetic Pivot periodically checks for magnetic field interference. If another working pivot is within the centered 5×5×5 area, it ignores rubbing, generates no charge, and cannot enter its working state

# Charge

- Completing a full rubbing cycle generates an additional 64 charges
- Charges can be collected using <ref item="anvilcraft:charge_collector"/>

# Recipes

An activated <ref item="anvilcraft_pigsplus:precision_magnetic_pivot"/> can perform *Precision Electromagnetic Processing*

<row halign="center">
<recipe id="anvilcraft_pigsplus:precision_electromagnetic_processing/circuit_board"/>
<recipe id="anvilcraft_pigsplus:precision_electromagnetic_processing/processor"/>
</row>

<row halign="center">
<recipe id="anvilcraft_pigsplus:precision_electromagnetic_processing/karakuri_component_2"/>
<recipe id="anvilcraft:precision_electromagnetic_processing/magnetoelectric_core"/>
</row>

<row halign="center">
<recipe id="anvilcraft:precision_electromagnetic_processing/magnet"/>
<recipe id="anvilcraft:precision_electromagnetic_processing/precision_magnetic_pivot"/>
</row>

# <ref item="anvilcraft_pigsplus:universal_redstone_component"/>

<recipe id="anvilcraft_pigsplus:precision_electromagnetic_processing/universal_redstone_component"/>

Can be crafted into various redstone components in a <ref item="minecraft:stonecutter"/>
