package amatsagu.essentialis.compat.trinkets;

import amatsagu.essentialis.Essentialis;
import amatsagu.essentialis.common.registry.EssentialisItems;
import eu.pb4.trinkets.api.SlotType;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TrinketsCompat {

	private TrinketsCompat() {
	}

	public static boolean isLoaded() {
		final var loader = FabricLoader.getInstance();

		return loader.isModLoaded("trinkets_updated") || loader.isModLoaded("trinkets");
	}

	public static ItemStack findInRingSlot(final ServerPlayer player) {
		for (final ItemStack stack : ringSlotStacks(player)) {
			if (stack.is(EssentialisItems.WAYBACK_STONE)) {
				return stack;
			}
		}

		return ItemStack.EMPTY;
	}

	public static List<ItemStack> ringSlotStacks(final ServerPlayer player) {
		final List<ItemStack> stacks = new ArrayList<>();

		for (final TrinketSlotAccess access : TrinketsApi.getAttachment(player).allEquipped(false)) {
			if (!isRingSlot(access)) {
				continue;
			}

			final ItemStack stack = access.get();

			if (!stack.isEmpty()) {
				stacks.add(stack);
			}
		}

		return stacks;
	}

	public static void registerRingSounds() {
		try {
			final Class<?> callbackType = Class.forName("eu.pb4.trinkets.api.callback.TrinketCallback");
			final Object sounds = Class.forName("amatsagu.essentialis.compat.trinkets.RingEquipSounds").getDeclaredConstructor().newInstance();
			final var setCallback = callbackType.getMethod("setCallback", Item.class, callbackType);

			for (final Item item : EssentialisItems.RINGS.values()) {
				setCallback.invoke(null, item, sounds);
			}
		} catch (final ReflectiveOperationException e) {
			Essentialis.LOGGER.warn("Failed to register trinket equip sounds.", e);
		}
	}

	private static boolean isRingSlot(final TrinketSlotAccess access) {
		final String slot = access.reference().slot();

		return slot.equals("ring") || slot.endsWith("/ring");
	}

	public static void verifyRingSlots(final MinecraftServer server) {
		final Set<Item> ours = new HashSet<>(EssentialisItems.RINGS.values());

		ours.add(EssentialisItems.WAYBACK_STONE);

		final var lookup = server.registryAccess().lookupOrThrow(Registries.ITEM);
		int count = 0;

		for (final SlotType type : SlotType.getPlayerSlots(server.overworld()).values()) {
			if (!type.getId().equals("ring") && !type.getId().endsWith("/ring")) {
				continue;
			}

			final TagKey<Item> tag = TagKey.create(Registries.ITEM, Identifier.parse("trinkets:" + type.getId()));
			final boolean fits = lookup.get(tag)
				.map(set -> set.stream().anyMatch(holder -> ours.contains(holder.value())))
				.orElse(false);

			if (fits) {
				count += type.amount();
			}
		}

		if (count >= 2) {
			Essentialis.LOGGER.info("Trinkets integration verified, {} ring slots accept Essentialis jewelry.", count);
		} else {
			Essentialis.LOGGER.error("Only {} ring slots accept Essentialis jewelry, expected at least 2. Rings may be unwearable as trinkets.", count);
		}
	}
}
