package amatsagu.essentialis.client;

import amatsagu.essentialis.common.enchantment.EssentialisEnchantments;
import amatsagu.essentialis.common.entity.ChakramEntity;
import amatsagu.essentialis.common.registry.EssentialisItems;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ProjectileStats {

	private static final double ARROW_DAMAGE = 2.0D;
	private static final double TRIDENT_DAMAGE = 8.0D;
	private static final double CHAKRAM_DAMAGE = 5.0D;
	private static final double CHAKRAM_RANGE = 15.0D;
	private static final double RANGE_PER_LIGHTWEIGHT = 7.5D;
	private static final double SMITE_PER_LEVEL = 2.5D;
	private static final double BANE_PER_LEVEL = 2.5D;
	private static final double IMPALING_PER_LEVEL = 2.5D;

	private ProjectileStats() {
	}

	public static void init() {
		ItemTooltipCallback.EVENT.register(ProjectileStats::onTooltip);
	}

	private static void onTooltip(
		final ItemStack stack,
		final Item.TooltipContext context,
		final TooltipFlag flag,
		final List<Component> lines
	) {
		if (stack.isEmpty() || lines.isEmpty()) {
			return;
		}

		final List<Component> stats = statsFor(stack);

		if (stats != null) {
			lines.addAll(1, stats);
		}
	}

	private static List<Component> statsFor(final ItemStack stack) {
		if (stack.is(EssentialisItems.CHAKRAM)) {
			return chakramStats(stack);
		}

		if (stack.getItem() instanceof BowItem) {
			final int power = EssentialisEnchantments.getLevel(stack, Enchantments.POWER);
			double damage = ARROW_DAMAGE;

			if (power > 0) {
				damage += 1.0D + 0.5D * (power - 1);
			}

			return List.of(header("When Shot:"), stat("Projectile Damage", damage));
		}

		if (stack.getItem() instanceof CrossbowItem) {
			return List.of(header("When Shot:"), stat("Projectile Damage", ARROW_DAMAGE));
		}

		if (stack.getItem() instanceof TridentItem) {
			return tridentStats(stack);
		}

		return null;
	}

	private static List<Component> tridentStats(final ItemStack stack) {
		final int sharpness = EssentialisEnchantments.getLevel(stack, Enchantments.SHARPNESS);
		final int smite = EssentialisEnchantments.getLevel(stack, Enchantments.SMITE);
		final int bane = EssentialisEnchantments.getLevel(stack, Enchantments.BANE_OF_ARTHROPODS);
		final int impaling = EssentialisEnchantments.getLevel(stack, Enchantments.IMPALING);

		double damage = TRIDENT_DAMAGE;

		if (sharpness > 0) {
			damage += 0.5D * sharpness + 0.5D;
		}

		final List<Component> stats = new ArrayList<>(5);

		stats.add(header("When Thrown:"));
		stats.add(stat("Projectile Damage", damage));

		if (impaling > 0) {
			stats.add(stat("Bonus vs Aquatic", IMPALING_PER_LEVEL * impaling));
		}

		if (smite > 0) {
			stats.add(stat("Bonus vs Undead", SMITE_PER_LEVEL * smite));
		}

		if (bane > 0) {
			stats.add(stat("Bonus vs Arthropods", BANE_PER_LEVEL * bane));
		}

		return stats;
	}

	private static List<Component> chakramStats(final ItemStack stack) {
		final int ricochet = EssentialisEnchantments.getLevel(stack, EssentialisEnchantments.RICOCHET);
		final int bouncing = EssentialisEnchantments.getLevel(stack, EssentialisEnchantments.BOUNCING);
		final int sharpness = EssentialisEnchantments.getLevel(stack, Enchantments.SHARPNESS);
		final int smite = EssentialisEnchantments.getLevel(stack, Enchantments.SMITE);
		final int bane = EssentialisEnchantments.getLevel(stack, Enchantments.BANE_OF_ARTHROPODS);
		final int lightweight = EssentialisEnchantments.getLevel(stack, EssentialisEnchantments.LIGHTWEIGHT);

		double damage = CHAKRAM_DAMAGE;

		if (ricochet > 0) {
			damage += 0.5D * ricochet;
		} else if (sharpness > 0) {
			damage += 0.5D * sharpness + 0.5D;
		}

		final double range = CHAKRAM_RANGE + RANGE_PER_LIGHTWEIGHT * lightweight;

		final List<Component> stats = new ArrayList<>(6);

		stats.add(header("When Thrown:"));
		stats.add(stat("Projectile Damage", damage));
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

		return stats;
	}

	private static Component header(final String text) {
		return Component.literal(text).withStyle(ChatFormatting.GRAY);
	}

	private static Component stat(final String label, final double value) {
		return Component.literal(" ")
			.append(Component.literal(number(value)).withStyle(ChatFormatting.DARK_GREEN))
			.append(Component.literal(" " + label).withStyle(ChatFormatting.GRAY));
	}

	private static String number(final double value) {
		if (value == Math.rint(value)) {
			return Long.toString((long) value);
		}

		return String.format(Locale.ROOT, "%.1f", value);
	}
}
