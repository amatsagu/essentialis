package amatsagu.essentialis;

import amatsagu.essentialis.common.HammerMining;
import amatsagu.essentialis.common.enchantment.EssentialisEnchantments;
import amatsagu.essentialis.common.loot.EssentialisLoot;
import amatsagu.essentialis.common.registry.EssentialisCreativeTabs;
import amatsagu.essentialis.common.registry.EssentialisEntities;
import amatsagu.essentialis.common.registry.EssentialisItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point for the Essentialis mod.
 *
 * <p>Essentialis is a collection of special items (tools, weapons and trinkets). The first item is
 * the returning {@code Echo Chakram}. The mod is intentionally split into small, single-purpose
 * classes so that porting to newer Minecraft versions only requires touching the registries and the
 * handful of vanilla hooks used by each feature.</p>
 */
public class Essentialis implements ModInitializer {
	public static final String MOD_ID = "essentialis";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/** Shorthand for creating an {@link Identifier} in this mod's namespace. */
	public static Identifier id(final String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		// Force static initialisation of the registries in a deterministic order.
		EssentialisItems.init();
		EssentialisEntities.init();
		EssentialisEnchantments.init();
		EssentialisCreativeTabs.init();
		EssentialisLoot.init();
		HammerMining.init();

		LOGGER.info("Essentialis initialized successfully!");
	}
}
