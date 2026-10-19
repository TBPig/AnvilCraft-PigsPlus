---
navigation:
  title: "§2无线传输器"
  icon: "anvilcraft_pigsplus:wireless_transmitter"
items:
  - anvilcraft_pigsplus:wireless_transmitter
---

# 无线传输器

<recipe id="anvilcraft_pigsplus:wireless_transmitter"/>

放置在物品/流体容器上，且目标也是对应容器时，会尝试将物品/流体发送过去

## 使用过程

1. 手持未绑定的<ref item="anvilcraft_pigsplus:wireless_transmitter"/>右键要被**输入**的容器
2. 将此<ref item="anvilcraft_pigsplus:wireless_transmitter"/>贴在要**输出**的容器边上

## 属性参数

- 只能绑定 64 格以内的方块
- 工作消耗 128kW
- 每次最多传输 1 组物品或 8000mB 流体，传输间隔和<ref item="anvilcraft:chute"/>一致

## 额外特性

- 绑定后 Shift + 右键可清除绑定
- 右键已放置的<ref item="anvilcraft_pigsplus:wireless_transmitter"/>可复制其**输入**目标
- 从底面输入的物品/流体会立即尝试送往绑定的目标方块，等同于向目标方块输入
- 传输物品或流体时会沿连线显示粒子，每格约 1 个，最多 16 个，并随机分布在整条连线上
- 粒子发射间隔为 0.4 秒（8 tick），避免被动传输频繁触发时产生过多粒子；实际显示数量还受原版“粒子效果”设置影响
