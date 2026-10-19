---
navigation:
  title: "§2Wireless Transmitter"
  icon: "anvilcraft_pigsplus:wireless_transmitter"
items:
  - anvilcraft_pigsplus:wireless_transmitter
---

# Wireless Transmitter

<recipe id="anvilcraft_pigsplus:wireless_transmitter"/>

When placed on an item or fluid container and bound to a matching container, it attempts to send items or fluids over

## Usage

1. While holding an unbound <ref item="anvilcraft_pigsplus:wireless_transmitter"/>, right-click the container to use as the **input** target
2. Place this <ref item="anvilcraft_pigsplus:wireless_transmitter"/> against the side of the container to use as the **output**

## Properties

- Can only bind a block within 64 blocks
- Consumes 128kW while working
- Transfers at most 1 stack of items or 8000mB of fluid per operation; the transfer interval matches the <ref item="anvilcraft:chute"/>

## Additional Features

- After binding, Shift + right-click clears the binding
- Right-clicking a placed <ref item="anvilcraft_pigsplus:wireless_transmitter"/> copies its **input** target
- items or fluids inserted from the **base**  side are immediately routed to the bound target block, equivalent to inserting into the target
- Item and fluid transfers display particles along the transmission line, about 1 per block and at most 16, randomly distributed across the whole line
- Particles are emitted at most once every 0.4 seconds (8 ticks), so frequent passive transfers do not flood the line; the number actually shown also follows the vanilla Particle Effects setting
