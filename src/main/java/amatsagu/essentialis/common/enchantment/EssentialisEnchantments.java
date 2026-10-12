package amatsagu.essentialis.common.enchantment;

import amatsagu.essentialis.Essentialis;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

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

	public static boolean has(final ItemStack stack, final ResourceKey<Enchantment> enchantment) {
		return getLevel(stack, enchantment) > 0;
	}

	public static void init() {
	}
}
