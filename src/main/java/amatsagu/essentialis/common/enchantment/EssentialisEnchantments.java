package amatsagu.essentialis.common.enchantment;

import amatsagu.essentialis.Essentialis;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/**
 * Resource keys and lookup helpers for the chakram enchantments.
 *
 * <p>Enchantments in modern Minecraft are a data-driven registry, so the actual definitions live in
 * {@code data/essentialis/enchantment/*.json}. Here we only keep the keys needed from code plus a
 * small helper to read the level of one of our enchantments from an item stack. Reading through
 * {@link ItemEnchantments} (instead of comparing translation keys or NBT) means this works no matter
 * how the enchantment was applied - enchanting table, anvil, commands or another mod.</p>
 */
public final class EssentialisEnchantments {
	public static final ResourceKey<Enchantment> BOUNCING = key("bouncing");
	public static final ResourceKey<Enchantment> HARVESTING = key("harvesting");
	public static final ResourceKey<Enchantment> LIGHTWEIGHT = key("lightweight");
	public static final ResourceKey<Enchantment> PHASING = key("phasing");
	public static final ResourceKey<Enchantment> RICOCHET = key("ricochet");

	private EssentialisEnchantments() {
	}

	private static ResourceKey<Enchantment> key(final String name) {
		return ResourceKey.create(Registries.ENCHANTMENT, Essentialis.id(name));
	}

	/**
	 * Returns the level of {@code enchantment} on {@code stack}, or {@code 0} if it is not present.
	 */
	public static int getLevel(final ItemStack stack, final ResourceKey<Enchantment> enchantment) {
		if (stack == null || stack.isEmpty()) {
			return 0;
		}

		final ItemEnchantments enchantments = stack.getEnchantments();
		for (final Holder<Enchantment> holder : enchantments.keySet()) {
			if (holder.is(enchantment)) {
				return enchantments.getLevel(holder);
			}
		}

		return 0;
	}

	/** Returns whether {@code stack} has at least one level of {@code enchantment}. */
	public static boolean has(final ItemStack stack, final ResourceKey<Enchantment> enchantment) {
		return getLevel(stack, enchantment) > 0;
	}

	public static void init() {
		// Triggers static initialisation.
	}
}
