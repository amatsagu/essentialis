<div align="center">
  <h1>
    <img src="src/main/resources/assets/essentialis/icon.png" alt="Essentialis Logo" width="48" height="48" align="middle" />
    <sub>Essentialis</sub>
  </h1>
  <h3>A growing collection of special weapons, tools and trinkets.</h3>
</div>

## About this mod
**Essentialis** is a Fabric mod that adds unique items to Minecraft. Some of them are completely new, some were created by other people (check credits below) but we rewritten them to be native java mod (+ optimized).

The current items are the **Echo Chakram** (a faithful native port of the popular
[*Chakram*](https://modrinth.com/datapack/chakram) data pack by maybejake & evergreenavery), the
**Hammer** (a combined iron pickaxe/shovel that mines in a 3x3), the **Wayback Stone** (a
death-saving charm with a teleport home) and set of 12 rings (with unique effects).

## <img src="src/main/resources/assets/essentialis/textures/item/chakram.png" width="32" align="top" /> The Echo Chakram
A mid-range throwing weapon crafted from echo shards and a breeze rod. **Right click** to throw the
chakram: it flies out, spins, bounces off terrain and returns to the player's hand.

- Repairable with **echo shards**.
- Ramps off walls to build reach (up to 10 bounces per throw, each rewarded by Ricochet).
- Can be thrown from either hand and returns to the hand it was thrown from.
- Special interactions: cuts down crops with Harvesting, makes Endermen blink away, and triggers
  target blocks on impact.
- Comes with few new enchantments:

| Enchantment | Max level | Effect |
| --- | --- | --- |
| **Bouncing** | 3 | After hitting an enemy, the chakram ricochets toward another nearby, visible enemy instead of returning. Mutually exclusive with Phasing. |
| **Harvesting** | 1 | Destroys crops, flowers and other foliage the chakram flies through. |
| **Lightweight** | 3 | Increases the chakram's maximum flight range by 7.5 blocks per level. |
| **Phasing** | 5 | Lets the chakram pass through enemies (up to one per level) instead of returning on impact. Mutually exclusive with Bouncing. |
| **Ricochet** | 5 | Adds bonus damage for every wall the chakram has bounced off. Mutually exclusive with Sharpness, Smite and Bane of Arthropods. |
| **Scavenger** | 1 | Picks up items the chakram flies past and brings them back to the player. |

The enchantments can be found on enchanted books in **ancient city** chests (or applied at an
enchanting table, as chakram is a sword-class weapon).

## <img src="src/main/resources/assets/essentialis/textures/item/hammer.png" width="32" align="top" /> The Hammer
A heavy mining tool that works as a combination of **iron pickaxe** and **iron shovel** - breaking a
**3x3 area** with every swing.

- Same mining speed and stats as an iron pickaxe.
- Repairable with **iron blocks**.
- Supports the same enchantments as iron pickaxes and shovels (Efficiency, Fortune, Silk Touch, ...).
- Supports UI highlight for blocks before breaking them.

## <img src="src/main/resources/assets/essentialis/textures/item/wayback_stone.png" width="32" align="top" /> The Wayback Stone
A charm that cheats death. When player is about to die, it leaves them on **one heart** and
teleports to respawn point.

- Works from a trinket **ring slot** or from either hand.
- Has 15 minute use cooldown. Stone itself is never consumed.
- Also grants 30 seconds of fire resistance.

## Rings
Trinkets with no durability. Work from a trinket **ring slot** or
from either hand. Effects from multiple rings stack. Netherite versions are upgraded in a
**smithing table** (ring + netherite ingot + upgrade template).

| Icon | Ring |
|---|---|
| <img src="src/main/resources/assets/essentialis/textures/item/citrine_ring.png" width="32" /> | **Citrine Ring** - highlights enemies within **16 blocks** (red outline). Netherite: **32 blocks** and ignites undead within 16 blocks. Cannot be stacked. Rare loot (3%): mineshafts, outposts, mansions, suspicious sand/gravel. |
| <img src="src/main/resources/assets/essentialis/textures/item/jade_ring.png" width="32" /> | **Jade Ring** - **+10%** movement speed and jump height. Netherite: doubled, also works while riding. Rare loot (2%): chests and barrels everywhere. |
| <img src="src/main/resources/assets/essentialis/textures/item/ruby_ring.png" width="32" /> | **Ruby Ring** - heals **10%** of dealt damage. Netherite: **25%**. Cannot be stacked. Rare loot (2%): nether fortresses and bastions. Rare loot (3%): suspicious sand/gravel. |
| <img src="src/main/resources/assets/essentialis/textures/item/sapphire_ring.png" width="32" /> | **Sapphire Ring** - **25%** shorter burn time, **+25%** speed in water and lava. Netherite: +50% speed, +50% breath and **50%** shorter burn time. Rare loot (2%): ocean ruins, shipwrecks. Rare loot (3%): suspicious sand/gravel. Rare loot (1%): drowned kills. |
| <img src="src/main/resources/assets/essentialis/textures/item/tanzanite_ring.png" width="32" /> | **Tanzanite Ring** - doubles the duration of **any** effect on the player. Netherite: positive effects only. Cannot be stacked. Rare loot (2%): witch and illager kills. |
| <img src="src/main/resources/assets/essentialis/textures/item/topaz_ring.png" width="32" /> | **Topaz Ring** - **+1** Luck. Netherite: +2 Luck and bonus chance for rare finds (chest loot, double mob drops, extra catches, gold nuggets from quartz, diamonds from lapis and emerald ores). Rare loot (2%): chests and barrels everywhere. |

## Mod compatibility
This mod was created for **Essentia 3** modpack - it's a close to vanilla modpack so this mod will work but it's not intended to be used elsewhere - that's why I don't provide config file.

It requires [Trinkets Updated](https://modrinth.com/mod/trinkets-updated) mod and it is recommended to combine with [JEI](https://modrinth.com/mod/jei) to see recipes and extra data. Additionally, there's experimental, optional support for [Penchant](https://github.com/ThePotatoArchivist/Penchant) mod.

## Credits
- Original *Chakram* data pack concept and assets by **maybejake** and **evergreenavery**
  (MIT licensed). The chakram texture/icon come from that project.
- Wayback stone icon comes from original *Wayback Stone* data pack / mod concept and assets by **DarkhaxDev** (GNU Lesser General Public licensed).
- All ring icons comes from *Jewelry (RPG Series)* mod by **ZsoltMolnarrr**.
- Native Fabric implementation by **Amatsagu**.

> [!Note]
> Some of the datapack -> java code transpilation was handled with help of artificial intelligence. This mod can be viewed publicly so clients running Essentia 3 Modpack can properly resolve files & check for updates. This mod can be launched standalone but it was prepared with this modpack in mind.

## License
All Rights Reserved with exception of elements taken directly from credited works.
