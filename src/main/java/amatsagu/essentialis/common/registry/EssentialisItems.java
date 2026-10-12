package amatsagu.essentialis.common.registry;

import amatsagu.essentialis.Essentialis;
import amatsagu.essentialis.common.item.ChakramItem;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Function;

/**
 * Item registry for Essentialis.
 *
 * <p>Items are built with vanilla {@link Item.Properties} helpers ({@code .sword(...)},
 * {@code .pickaxe(...)}, {@code .durability(...)}, ...) instead of old specialised item subclasses.
 * That keeps every item data-driven and makes updating to future Minecraft versions a matter of
 * adjusting these builders rather than rewriting item hierarchies.</p>
 */
public final class EssentialisItems {
	/** Iron pickaxe durability (250) times nine, because the hammer can break a 3x3 each swing. */
	public static final int HAMMER_DURABILITY = 2250;

	/**
	 * The Echo Chakram. Mechanically an iron sword (same attributes, repairable with echo shards) but
	 * with a much larger durability pool, epic rarity and an extended attack range, exactly like the
	 * original data pack item.
	 */
	public static final Item CHAKRAM = register("chakram", id -> new ChakramItem(new Item.Properties()
			.setId(id)
			// Same melee profile as an iron sword: +3 attack damage, -2.4 attack speed.
			.sword(ToolMaterial.IRON, 3.0F, -2.4F)
			// 1500 durability instead of the iron sword's 250.
			.durability(1500)
			// Repairable with echo shards.
			.repairable(Items.ECHO_SHARD)
			.rarity(Rarity.EPIC)
			// 5 tick throw cooldown, matching the data pack's schedule.
			.useCooldown(0.25F)
			// max_reach: 3 (survival) / 4 (creative), from the data pack.
			.component(DataComponents.ATTACK_RANGE, new AttackRange(0.0F, 3.0F, 0.0F, 4.0F, 0.0F, 1.0F))
	));

	/**
	 * The Hammer. An iron pickaxe in every stat (mining speed, attack damage/speed, repair material,
	 * enchantability), except it also mines shovel blocks at iron speed and breaks a 3x3 area. It has
	 * nine times the durability because a single swing can consume up to nine durability.
	 */
	public static final Item HAMMER = register("hammer", id -> new Item(new Item.Properties()
			.setId(id)
			// Reuse the exact iron pickaxe profile...
			.pickaxe(ToolMaterial.IRON, 1.0F, -2.8F)
			// ...then swap in a 3x3-sized durability pool and a pickaxe+shovel tool.
			.durability(HAMMER_DURABILITY)
			.component(DataComponents.TOOL, createHammerTool())
	));

	private EssentialisItems() {
	}

	/** A tool that behaves like an iron pickaxe and an iron shovel combined. */
	private static Tool createHammerTool() {
		final HolderGetter<Block> lookup = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
		return new Tool(
				List.of(
						Tool.Rule.deniesDrops(lookup.getOrThrow(BlockTags.INCORRECT_FOR_IRON_TOOL)),
						Tool.Rule.minesAndDrops(lookup.getOrThrow(BlockTags.MINEABLE_WITH_PICKAXE), ToolMaterial.IRON.speed()),
						Tool.Rule.minesAndDrops(lookup.getOrThrow(BlockTags.MINEABLE_WITH_SHOVEL), ToolMaterial.IRON.speed())
				),
				1.0F,
				1,
				true
		);
	}

	private static Item register(final String name, final Function<ResourceKey<Item>, Item> factory) {
		final ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Essentialis.id(name));
		return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(key));
	}

	/** Convenience helper for creating a stack of an Essentialis item. */
	public static ItemStack stack(final ItemLike item) {
		return item.asItem().getDefaultInstance();
	}

	public static void init() {
		// Triggers static initialisation.
	}
}
