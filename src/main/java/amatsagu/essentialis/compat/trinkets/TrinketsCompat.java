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

	public static ItemStack findEquipped(final ServerPlayer player) {
		return TrinketsApi.getAttachment(player)
			.findFirst(EssentialisItems.WAYBACK_STONE)
			.map(TrinketSlotAccess::get)
			.orElse(ItemStack.EMPTY);
	}
}
