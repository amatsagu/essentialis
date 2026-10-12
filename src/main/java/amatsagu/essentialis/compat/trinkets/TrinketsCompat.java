package amatsagu.essentialis.compat.trinkets;

import amatsagu.essentialis.common.registry.EssentialisItems;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class TrinketsCompat {

	private TrinketsCompat() {
	}

	public static boolean isLoaded() {
		final var loader = FabricLoader.getInstance();

		return loader.isModLoaded("trinkets_updated") || loader.isModLoaded("trinkets");
	}

	public static ItemStack findInRingSlot(final ServerPlayer player) {
		for (final TrinketSlotAccess access : TrinketsApi.getAttachment(player)
			.equipped(stack -> stack.is(EssentialisItems.WAYBACK_STONE), false)) {
			if (isRingSlot(access)) {
				return access.get();
			}
		}

		return ItemStack.EMPTY;
	}

	private static boolean isRingSlot(final TrinketSlotAccess access) {
		final String slot = access.reference().slot();

		return slot.equals("ring") || slot.endsWith("/ring");
	}
}
