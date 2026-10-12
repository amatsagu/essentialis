<div align="center">
  <h1>
    <img src="src/main/resources/assets/essentialis/icon.png" alt="Essentialis Logo" width="48" height="48" align="middle" />
    <sub>Essentialis</sub>
  </h1>
  <h3>A growing collection of special tools, weapons and trinkets.</h3>
</div>

## About this mod
**Essentialis** is a Fabric mod that adds unique, hand-crafted items to Minecraft. Each item is
implemented natively in Java (rather than as a data pack) so it can hook into vanilla mechanics
cleanly and play nicely with other mods such as JEI.

The mod is written with forward-porting in mind: items and entities are built from small, focused
classes plus vanilla registry helpers, and all of the content (enchantments, recipes, models,
loot) is data-driven. Updating to a new Minecraft version mostly means revisiting the registries
and the handful of vanilla hooks each item uses.

The current items are the **Echo Chakram** (a faithful native port of the popular
[*Chakram*](https://modrinth.com/datapack/chakram) data pack by maybejake & evergreenavery), the
**Hammer** (a combined iron pickaxe/shovel that mines in a 3x3) and the **Wayback Stone** (a
death-saving charm with a teleport home).

## The Echo Chakram
A mid-range throwing weapon crafted from echo shards and a breeze rod. **Right click** to throw the
chakram: it flies out, spins, bounces off terrain and returns to your hand. In survival the item is
physically thrown and comes back on its own; in creative you keep your copy and can throw freely.

- Same melee profile as an iron sword, but with **1500 durability** and **epic** rarity.
- Repairable with **echo shards**.
- Ramps off walls to build speed and reach (up to 5 wall bounces are rewarded by Ricochet).
- Can be thrown from either hand and returns to the hand it was thrown from.
- Special interactions: cuts down crops with Harvesting, makes Endermen blink away, and triggers
  target blocks on impact.

### Enchantments
All enchantments are real, registered enchantments (not custom data), so they show up correctly in
tooltips, the enchanting table, anvils and recipe viewers such as JEI.

| Enchantment | Max level | Effect |
| --- | --- | --- |
| **Bouncing** | 3 | After hitting an enemy, the chakram ricochets toward another nearby, visible enemy instead of returning. Mutually exclusive with Phasing. |
| **Harvesting** | 1 | Destroys crops, flowers and other foliage the chakram flies through. |
| **Lightweight** | 3 | Increases the chakram's maximum flight range by 7.5 blocks per level. |
| **Phasing** | 5 | Lets the chakram pass through enemies (up to one per level) instead of returning on impact. Mutually exclusive with Bouncing. |
| **Ricochet** | 5 | Adds bonus damage for every wall the chakram has bounced off. Mutually exclusive with Sharpness, Smite and Bane of Arthropods. |

The enchantments can be found on enchanted books in **ancient city** chests (or applied at an
enchanting table, since the chakram is a sword-class weapon).

### Descriptions and stats
Enchantment descriptions are stored as the standard localization keys
`enchantment.essentialis.<name>.desc`. They are not injected into tooltips by the mod itself; instead
they are written to the language file so that description-aware tools (such as JEI Enchantment Info
or the Enchantment Descriptions mod) can display them, and so any resource pack can translate them.
Each description includes how the enchantment scales with its level.

The chakram itself shows a small stats block under its name, in vanilla's blue-value style:

```
Chakram
When Thrown:
 5 Thrown Damage
 15 Block Range
 10 Max Bounces
 3 Max Ricochets
```

These values are recomputed from the enchantments on the stack, so they change the moment the
chakram is enchanted (Lightweight raises the range line, Sharpness/Ricochet raise the damage line,
Smite/Bane add conditional bonus lines, and so on).

A single throw is capped at **10 wall bounces**: once the limit is reached the chakram simply comes
home. This bounds Ricochet's damage scaling and prevents pathological corner cases where a chakram
could keep bouncing forever.

The stats code is **client-only**; the server never touches it.

### Crafting
```
p e p
e b e
p e p
```
where `e` is an **echo shard**, `p` is an **ender pearl** and `b` is a **breeze rod**.

The recipe is unlocked in the recipe book as soon as you obtain your first echo shard. This is done
in code by hooking vanilla's slot-change dispatch (`AbstractContainerMenu.triggerSlotListeners`),
which only runs when an inventory slot actually changes — no per-tick polling and no advancement
required.

## The Hammer
A heavy mining tool that behaves like an **iron pickaxe and iron shovel combined**.

- Iron pickaxe stats: same mining speed, attack damage/speed and repair material (iron).
- Mines a **3x3 area** in the plane you are looking at, against the block you break.
- Breaks both pickaxe blocks and shovel blocks (dirt, sand, gravel, ...) at iron speed.
- **2250 durability** (nine times an iron pickaxe), because a swing can consume one durability per
  block broken — up to nine.
- Supports the same enchantments as an iron pickaxe/shovel (Efficiency, Fortune, Silk Touch,
  Unbreaking, Mending, ...).
- The client draws the full 3x3 outline while you hold it, so you can see what a swing will hit.

### How the 3x3 works
Vanilla breaks the targeted block normally; the mod then breaks the surrounding blocks through the
player's own game-mode break logic, so every extra block uses the real vanilla path — correct drops
(Fortune/Silk Touch, iron-tier checks), one durability each, stats, and the block-break events other
mods (land protection, etc.) listen to. A re-entrancy guard prevents the extra breaks from triggering
further 3x3 breaks. The client outline is generated from the same plane rule, so it always matches.

### Crafting
```
  ii
 si
b
```
where `i` is an **iron block**, `s` is a **stick** and `b` is a **blaze rod**.

## The Wayback Stone
A charm that cheats death. When you would otherwise die, the stone instead leaves you on **one
heart** and teleports you to your spawn point (bed or respawn anchor, following vanilla respawn
rules including anchor charge use), grants **30 seconds of fire resistance** (with visible particles),
then goes on a **15 minute cooldown** shared by all Wayback Stones.

- Works from a trinket **ring slot** when [Trinkets Updated](https://modrinth.com/mod/trinkets-updated) is
  installed, or from either hand without it.
- Does nothing while on cooldown, in creative/spectator, or without a valid bed or charged anchor
  spawn — in those cases death proceeds normally.
- The stone is not consumed; the cooldown is the cost.
- The effect is written in the item tooltip in gray text through vanilla's tooltip mechanism, so
  JEI and other tooltip readers display it, plus an `item.essentialis.wayback_stone.desc` language
  key for description mods.

### Crafting
```
IGI
GHG
IGI
```
where `I` is a **gold ingot**, `G` is **glowstone** and `H` is a **heart of the sea** — the same
recipe and texture as the original
[WaybackStone](https://modrinth.com/mod/wayback-stone) mod (mechanic redesigned).

## Installation
1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft **26.3**.
2. Drop [Fabric API](https://modrinth.com/mod/fabric-api) and the Essentialis jar into your `mods`
   folder.
3. Launch the game.

## Building from source
Requires JDK 25. Gradle itself must also run on JDK 25 — point `JAVA_HOME` at a 25 install if your
default JDK is newer:

```bash
JAVA_HOME=/path/to/jdk-25 ./gradlew build
```

The built jar is written to `build/libs/`.

### Project layout
- `src/main/java/amatsagu/essentialis/Essentialis.java` – mod entry point.
- `common/registry/` – item, entity and creative tab registration.
- `common/item/` – item behaviour (`ChakramItem`).
- `common/entity/` – the chakram projectile (`ChakramEntity`), a native port of the original
  data pack's flight, bounce, return and enchantment logic.
- `common/enchantment/` – enchantment keys and lookup helpers.
- `common/loot/` – ancient city book injection.
- `common/recipe/` – the chakram recipe key and the recipe-book unlock helper.
- `common/HammerMining`, `common/HammerUtil` – the hammer's server-side 3x3 mining and shared plane rules.
- `common/WaybackStoneEffect` – the death-save handler (cancels lethal damage, teleports home, applies cooldown).
- `compat/trinkets/` – isolated Trinkets API bridge, only loaded when the mod is present.
- `mixin/` – a single targeted mixin that unlocks the recipe when an echo shard enters the inventory.
- `client/` – client entry point, the chakram renderer (reuses vanilla's item display renderer), the
  chakram stat tooltip and the hammer's 3x3 block outline.
- `src/main/resources/data/essentialis/enchantment/` – the data-driven enchantment definitions.

### Performance notes
The chakram is written to be cheap on the server:
- Enemy detection does a single world query per tick over the volume the chakram will sweep, then
  tests candidates per sub-step against that cached list, instead of querying the world 30 times a
  tick.
- Flight direction is cached and only recomputed when the chakram turns.
- Scratch objects (candidate list, mutable block position) are reused, so the hot loop performs no
  per-step allocations.

The hammer only does work on an actual block break (event-driven), and the 3x3 outline is only
computed on the client while the hammer is held.

## Credits
- Original *Chakram* data pack concept and assets by **maybejake** and **evergreenavery**
  (MIT licensed). The chakram texture/icon come from that project.
- Native Fabric implementation by **Amatsagu**.

> [!Important]
> Parts of this mod were created with the help of artificial intelligence.

## License
All Rights Reserved.
