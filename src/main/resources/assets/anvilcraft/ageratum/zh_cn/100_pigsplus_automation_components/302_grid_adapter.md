---
navigation:
  title: "§5电网接入器"
  icon: "anvilcraft_pigsplus:grid_adapter"
items:
  - anvilcraft_pigsplus:grid_adapter
---

# 电网接入器

<recipe id="anvilcraft_pigsplus:grid_adapter"/>

连接砧艺电网和普通 FE 机器

# 模式

默认按住[Alt]键切换模式

## 输入：铁砧kW -> FE/t

- 右键设置**最大**输入值
- Shift + 右键 FE 方块进行绑定或取消绑定
- 如果目标方块无法接收 FE，接入器停止消耗

## 输出：FE/t -> 铁砧kW

- 右键设置**恒定**输出值
- Shift + 右键 FE 方块进行绑定或取消绑定
- 必须一次取满所需 FE 才工作，不会进行部分提取

<tip>
恒定输出值是为了确保发电量不够的情况下，每隔一段时间仍能正常工作一小段时间，毕竟铁砧工艺电网无法存储能量
</tip>
