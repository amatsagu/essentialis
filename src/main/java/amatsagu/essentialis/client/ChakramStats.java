package amatsagu.essentialis.client;

import amatsagu.essentialis.common.enchantment.EssentialisEnchantments;
import amatsagu.essentialis.common.entity.ChakramEntity;
import amatsagu.essentialis.common.registry.EssentialisItems;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ChakramStats {

	private static final double BASE_DAMAGE = 5.0D;

	private static final double BASE_RANGE = 15.0D;

	private static final double RANGE_PER_LIGHTWEIGHT = 7.5D;

	private static final double SMITE_PER_LEVEL = 2.5D;

	private static final double BANE_PER_LEVEL = 2.5D;

	private ChakramStats() {
	}

	public static void init() {
		ItemTooltipCallback.EVENT.register(ChakramStats::onTooltip);
	}

	private static void onTooltip(
		final ItemStack stack,
		final Item.TooltipContext context,
		final TooltipFlag flag,
		final List<Component> lines
	) {
		if (!stack.is(EssentialisItems.CHAKRAM) || lines.isEmpty()) {
			return;
		}

		final int ricochet = EssentialisEnchantments.getLevel(stack, EssentialisEnchantments.RICOCHET);
		final int bouncing = EssentialisEnchantments.getLevel(stack, EssentialisEnchantments.BOUNCING);
		final int sharpness = EssentialisEnchantments.getLevel(stack, Enchantments.SHARPNESS);
		final int smite = EssentialisEnchantments.getLevel(stack, Enchantments.SMITE);
		final int bane = EssentialisEnchantments.getLevel(stack, Enchantments.BANE_OF_ARTHROPODS);
		final int lightweight = EssentialisEnchantments.getLevel(stack, EssentialisEnchantments.LIGHTWEIGHT);

		double damage = BASE_DAMAGE;

		if (ricochet > 0) {
			damage += 0.5D * ricochet;
		} else if (sharpness > 0) {
			damage += 0.5D * sharpness + 0.5D;
		}

		final double range = BASE_RANGE + RANGE_PER_LIGHTWEIGHT * lightweight;

		final List<Component> stats = new ArrayList<>(6);

		stats.add(Component.literal("When Thrown:").withStyle(ChatFormatting.GRAY));
		stats.add(stat("Thrown Damage", damage));
		stats.add(stat("Block Range", range));
		stats.add(stat("Max Bounces", ChakramEntity.MAX_BOUNCES));

		if (bouncing > 0) {
			stats.add(stat("Max Ricochets", bouncing));
		}

		if (smite > 0) {
			stats.add(stat("Bonus vs Undead", SMITE_PER_LEVEL * smite));
		}

		if (bane > 0) {
			stats.add(stat("Bonus vs Arthropods", BANE_PER_LEVEL * bane));
		}

		lines.addAll(1, stats);
	}

	private static Component stat(final String label, final double value) {
		return Component.literal(" ")
			.append(Component.literal(number(value)).withStyle(ChatFormatting.BLUE))
			.append(Component.literal(" " + label).withStyle(ChatFormatting.GRAY));
	}

	private static String number(final double value) {
		if (value == Math.rint(value)) {
			return Long.toString((long) value);
		}

		return String.format(Locale.ROOT, "%.1f", value);
	}
}
