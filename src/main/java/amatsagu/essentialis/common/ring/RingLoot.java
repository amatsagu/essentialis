package amatsagu.essentialis.common.ring;

import amatsagu.essentialis.common.registry.EssentialisItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;

import java.util.List;
import java.util.Set;

public final class RingLoot {

	private static final List<ResourceKey<LootTable>> ABANDONED_HUMAN_STRUCTURES = List.of(
		BuiltInLootTables.ABANDONED_MINESHAFT,
		BuiltInLootTables.PILLAGER_OUTPOST,
		BuiltInLootTables.WOODLAND_MANSION,
		BuiltInLootTables.STRONGHOLD_CORRIDOR,
		BuiltInLootTables.STRONGHOLD_CROSSING,
		BuiltInLootTables.STRONGHOLD_LIBRARY
	);

	private static final List<ResourceKey<LootTable>> ANY_CHEST = List.of(
		BuiltInLootTables.SIMPLE_DUNGEON,
		BuiltInLootTables.VILLAGE_WEAPONSMITH,
		BuiltInLootTables.VILLAGE_TOOLSMITH,
		BuiltInLootTables.VILLAGE_ARMORER,
		BuiltInLootTables.VILLAGE_CARTOGRAPHER,
		BuiltInLootTables.VILLAGE_MASON,
		BuiltInLootTables.VILLAGE_SHEPHERD,
		BuiltInLootTables.VILLAGE_BUTCHER,
		BuiltInLootTables.VILLAGE_FLETCHER,
		BuiltInLootTables.VILLAGE_FISHER,
		BuiltInLootTables.VILLAGE_TANNERY,
		BuiltInLootTables.VILLAGE_TEMPLE,
		BuiltInLootTables.VILLAGE_DESERT_HOUSE,
		BuiltInLootTables.VILLAGE_PLAINS_HOUSE,
		BuiltInLootTables.VILLAGE_TAIGA_HOUSE,
		BuiltInLootTables.VILLAGE_SNOWY_HOUSE,
		BuiltInLootTables.VILLAGE_SAVANNA_HOUSE,
		BuiltInLootTables.DESERT_PYRAMID,
		BuiltInLootTables.JUNGLE_TEMPLE,
		BuiltInLootTables.ABANDONED_MINESHAFT,
		BuiltInLootTables.STRONGHOLD_CORRIDOR,
		BuiltInLootTables.STRONGHOLD_CROSSING,
		BuiltInLootTables.WOODLAND_MANSION,
		BuiltInLootTables.BURIED_TREASURE
	);

	private static final List<ResourceKey<LootTable>> NETHER_FORTRESS_LOOT = List.of(
		BuiltInLootTables.NETHER_BRIDGE,
		BuiltInLootTables.BASTION_TREASURE,
		BuiltInLootTables.BASTION_OTHER,
		BuiltInLootTables.BASTION_BRIDGE,
		BuiltInLootTables.BASTION_HOGLIN_STABLE
	);

	private static final List<ResourceKey<LootTable>> WATER_CHESTS = List.of(
		BuiltInLootTables.UNDERWATER_RUIN_SMALL,
		BuiltInLootTables.UNDERWATER_RUIN_BIG,
		BuiltInLootTables.SHIPWRECK_SUPPLY,
		BuiltInLootTables.SHIPWRECK_TREASURE,
		BuiltInLootTables.SHIPWRECK_MAP,
		BuiltInLootTables.BURIED_TREASURE
	);

	private static final List<ResourceKey<LootTable>> ALL_ARCHAEOLOGY = List.of(
		BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY,
		BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY,
		BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_COMMON,
		BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE,
		BuiltInLootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY,
		BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY
	);

	private static ResourceKey<LootTable> entity(final String path) {
		return ResourceKey.create(Registries.LOOT_TABLE, Identifier.parse("minecraft:entities/" + path));
	}

	private RingLoot() {
	}

	public static void init() {
		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			if (ABANDONED_HUMAN_STRUCTURES.contains(key)) {
				add(tableBuilder, EssentialisItems.RINGS.get(RingKind.CITRINE), 3);
			}

			if (ANY_CHEST.contains(key)) {
				add(tableBuilder, EssentialisItems.RINGS.get(RingKind.JADE), 2);
				add(tableBuilder, EssentialisItems.RINGS.get(RingKind.TOPAZ), 2);
			}

			if (NETHER_FORTRESS_LOOT.contains(key)) {
				add(tableBuilder, EssentialisItems.RINGS.get(RingKind.RUBY), 2);
			}

			if (WATER_CHESTS.contains(key)) {
				add(tableBuilder, EssentialisItems.RINGS.get(RingKind.SAPPHIRE), 2);
			}

			if (ALL_ARCHAEOLOGY.contains(key)) {
				add(tableBuilder, EssentialisItems.RINGS.get(RingKind.SAPPHIRE), 3);
				add(tableBuilder, EssentialisItems.RINGS.get(RingKind.CITRINE), 3);
				add(tableBuilder, EssentialisItems.RINGS.get(RingKind.RUBY), 3);
			}

			if (key.equals(entity("drowned"))) {
				add(tableBuilder, EssentialisItems.RINGS.get(RingKind.SAPPHIRE), 1);
			}

			if (Set.of(entity("witch"), entity("pillager"), entity("vindicator"), entity("evoker")).contains(key)) {
				add(tableBuilder, EssentialisItems.RINGS.get(RingKind.TANZANITE), 2);
			}
		});
	}

	private static void add(final LootTable.Builder tableBuilder, final Item item, final int chance) {
		tableBuilder.withPool(LootPool.lootPool()
			.add(EmptyLootItem.emptyItem().setWeight(100 - chance))
			.add(LootItem.lootTableItem(item).setWeight(chance)));
	}
}
