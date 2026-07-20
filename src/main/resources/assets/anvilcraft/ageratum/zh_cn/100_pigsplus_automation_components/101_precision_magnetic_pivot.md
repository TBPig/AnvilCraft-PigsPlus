---
navigation:
  title: "§2精密磁枢"
  icon: "anvilcraft_pigsplus:precision_magnetic_pivot"
items:
  - anvilcraft_pigsplus:precision_magnetic_pivot
  - anvilcraft_pigsplus:universal_redstone_component
---

# <ref item="anvilcraft_pigsplus:precision_magnetic_pivot"/>

> 对《factorio》电磁工厂的拙劣模仿，但模仿的是电磁工厂

<recipe id="anvilcraft_pigsplus:precision_magnetic_pivot"/>

# 工作

- 采用特殊方式启动：使用<ref item="anvilcraft:magnet_block"/>（磁铁方块）绕<ref item="anvilcraft_pigsplus:precision_magnetic_pivot"/>的四周进行摩擦
- 按顺时针或逆时针顺序，**水平摩擦**四个面后，进入持续 2s 的工作状态
- 如果摩擦顺序不对，会重新结算

# 电荷

- 摩擦到第1、2、3、4个面时，分别产生2、4、8、16个电荷
- 如果完成一个摩擦周期，还会额外产生64个电荷
- 可用<ref item="anvilcraft:charge_collector"/>收集这些电荷

# 高效配方

激活的<ref item="anvilcraft_pigsplus:precision_magnetic_pivot"/>可进行*精密电磁加工*

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

可以在<ref item="minecraft:stonecutter"/>中制作成各种红石元件