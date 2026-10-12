package amatsagu.essentialis.common.registry;

import amatsagu.essentialis.Essentialis;
import amatsagu.essentialis.common.item.ChakramItem;
import amatsagu.essentialis.common.item.WaybackStoneItem;
import amatsagu.essentialis.common.ring.RingItem;
import amatsagu.essentialis.common.ring.RingKind;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.Block;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class EssentialisItems {

	public static final int HAMMER_DURABILITY = 2250;

	public static final Item CHAKRAM = register("chakram", id -> new ChakramItem(
		new Item.Properties()
			.setId(id)
			.sword(ToolMaterial.IRON, 3.0F, -2.4F)
			.durability(1500)
			.repairable(Items.ECHO_SHARD)
			.rarity(Rarity.RARE)
			.useCooldown(0.25F)
			.component(DataComponents.ATTACK_RANGE, new AttackRange(0.0F, 3.0F, 0.0F, 4.0F, 0.0F, 1.0F))
	));

	public static final Item HAMMER = register("hammer", id -> new Item(
		new Item.Properties()
			.setId(id)
			.pickaxe(ToolMaterial.IRON, 1.0F, -2.8F)
			.durability(HAMMER_DURABILITY)
			.repairable(Items.IRON_BLOCK)
			.rarity(Rarity.UNCOMMON)
			.component(DataComponents.TOOL, createHammerTool())
	));

	public static final TagKey<Item> REPAIRS_WAYBACK_STONE = TagKey.create(
		Registries.ITEM,
		Essentialis.id("repairs_wayback_stone")
	);

	public static final Item WAYBACK_STONE = register("wayback_stone", id -> new WaybackStoneItem(
		new Item.Properties()
			.setId(id)
			.stacksTo(1)
			.rarity(Rarity.UNCOMMON)
			.repairable(REPAIRS_WAYBACK_STONE)
	));

	public static final Map<RingKind, Item> RINGS = registerRings();

	private EssentialisItems() {
	}

	private static Map<RingKind, Item> registerRings() {
		final Map<RingKind, Item> rings = new EnumMap<>(RingKind.class);

		for (final RingKind kind : RingKind.values()) {
			rings.put(kind, register(kind.id, id -> new RingItem(
				new Item.Properties()
					.setId(id)
					.stacksTo(1)
					.rarity(kind.netherite ? Rarity.RARE : Rarity.UNCOMMON)
					.component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.MAINHAND).setEquipSound(EssentialisSounds.JEWELRY_EQUIP).build()),
				kind
			)));
		}

		return Map.copyOf(rings);
	}

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

	public static void init() {
	}
}
