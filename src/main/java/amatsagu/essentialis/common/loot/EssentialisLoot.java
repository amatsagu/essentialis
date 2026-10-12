package amatsagu.essentialis.common.loot;

import amatsagu.essentialis.common.enchantment.EssentialisEnchantments;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.List;

/**
 * Makes the chakram enchantments discoverable as enchanted books.
 *
 * <p>The original data pack raycast from the player to an ancient city chest and injected one book
 * per successful roll (13% for most enchantments, 6% for Harvesting). We reproduce those odds with
 * one weighted loot pool per enchantment, which is both simpler and data-driven.</p>
 */
public final class EssentialisLoot {
	private record EnchantEntry(ResourceKey<Enchantment> enchantment, int maxLevel, int chance) {
	}

	private static final List<EnchantEntry> ENTRIES = List.of(
			new EnchantEntry(EssentialisEnchantments.BOUNCING, 3, 13),
			new EnchantEntry(EssentialisEnchantments.HARVESTING, 1, 6),
			new EnchantEntry(EssentialisEnchantments.LIGHTWEIGHT, 3, 13),
			new EnchantEntry(EssentialisEnchantments.PHASING, 5, 13),
			new EnchantEntry(EssentialisEnchantments.RICOCHET, 5, 13)
	);

	private EssentialisLoot() {
	}

	public static void init() {
		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			// ResourceKeys must be compared by equality, not identity.
			if (!BuiltInLootTables.ANCIENT_CITY.equals(key)) {
				return;
			}

			final var lookup = registries.lookup(Registries.ENCHANTMENT);
			if (lookup.isEmpty()) {
				return;
			}

			for (final EnchantEntry entry : ENTRIES) {
				final var holder = lookup.get().get(entry.enchantment());
				if (holder.isEmpty()) {
					continue;
				}

				final LootPool.Builder pool = LootPool.lootPool()
						.add(EmptyLootItem.emptyItem().setWeight(100 - entry.chance()))
						.add(LootItem.lootTableItem(Items.BOOK)
								.setWeight(entry.chance())
								.apply(new SetEnchantmentsFunction.Builder()
										.withEnchantment(holder.get(), ContextIntProviders.between(1, entry.maxLevel()))));

				tableBuilder.withPool(pool);
			}
		});
	}
}
