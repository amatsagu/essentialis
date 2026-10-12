package amatsagu.essentialis.compat.penchant;

import amatsagu.essentialis.Essentialis;
import archives.tater.penchant.component.EnchantmentProgress;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class PenchantCompat {

	public static final String MOD_ID = "penchant";

	public static final String MIN_VERSION = "0.5.7";

	private static boolean ready = false;

	private PenchantCompat() {
	}

	public static void init() {
		final var container = FabricLoader.getInstance().getModContainer(MOD_ID);

		if (container.isEmpty()) {
			return;
		}

		try {
			if (container.get().getMetadata().getVersion().compareTo(Version.parse(MIN_VERSION)) < 0) {
				throw new IncompatiblePenchantVersionException(container.get().getMetadata().getVersion().getFriendlyString());
			}
		} catch (final VersionParsingException e) {
			Essentialis.LOGGER.warn("Could not read the Penchant version, chakram enchantments will not level up.", e);

			return;
		}

		ready = true;
		Essentialis.LOGGER.info("Penchant integration enabled, chakram hits level up enchantments.");
	}

	public static boolean isReady() {
		return ready;
	}

	public static void onChakramHit(final LivingEntity user, final ItemStack stack) {
		if (!ready) {
			return;
		}

		EnchantmentProgress.onDurabilityDamage(stack, user);
	}
}
