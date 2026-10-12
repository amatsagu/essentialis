package amatsagu.essentialis;

import amatsagu.essentialis.common.HammerMining;
import amatsagu.essentialis.common.WaybackStoneEffect;
import amatsagu.essentialis.common.ring.RingEffects;
import amatsagu.essentialis.common.ring.RingLoot;
import amatsagu.essentialis.compat.trinkets.TrinketsCompat;
import amatsagu.essentialis.compat.penchant.PenchantCompat;
import amatsagu.essentialis.common.enchantment.EssentialisEnchantments;
import amatsagu.essentialis.common.loot.EssentialisLoot;
import amatsagu.essentialis.common.registry.EssentialisCreativeTabs;
import amatsagu.essentialis.common.registry.EssentialisEntities;
import amatsagu.essentialis.common.registry.EssentialisItems;
import amatsagu.essentialis.common.registry.EssentialisSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Essentialis implements ModInitializer {

	public static final String MOD_ID = "essentialis";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(final String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		EssentialisItems.init();
		EssentialisEntities.init();
		EssentialisSounds.init();
		EssentialisEnchantments.init();
		EssentialisCreativeTabs.init();
		EssentialisLoot.init();
		HammerMining.init();
		WaybackStoneEffect.init();
		RingEffects.init();
		RingLoot.init();
		PenchantCompat.init();

		if (TrinketsCompat.isLoaded()) {
			TrinketsCompat.registerRingSounds();
		}

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			if (TrinketsCompat.isLoaded()) {
				TrinketsCompat.verifyRingSlots(server);
			}
		});

		LOGGER.info("Essentialis initialized successfully!");
	}
}
