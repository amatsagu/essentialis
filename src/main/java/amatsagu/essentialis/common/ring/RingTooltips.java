package amatsagu.essentialis.common.ring;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

public final class RingTooltips {

	private RingTooltips() {
	}

	public static List<Component> lines(final RingKind kind) {
		final List<Component> lines = new ArrayList<>(4);
		final String base = "tooltip.essentialis.ring." + kind.id;

		int line = 1;

		while (true) {
			final String key = base + ".line" + line;
			final String fallback = Fallbacks.get(key);

			if (fallback == null) {
				break;
			}

			lines.add(text(key, fallback, kind == RingKind.NETHERITE_TOPAZ && line > 1));
			line++;
		}

		lines.add(Component.empty());

		return List.copyOf(lines);
	}

	private static Component text(final String key, final String fallback, final boolean dark) {
		final Object[] args = Args.get(key);

		final MutableComponent line = args.length == 0
			? Component.translatableWithFallback(key, fallback)
			: Component.translatableWithFallback(key, fallback, args);

		return line.withStyle(dark ? ChatFormatting.DARK_GRAY : ChatFormatting.GRAY);
	}

	private static Component text(final String key, final String fallback) {
		return text(key, fallback, false);
	}

	private static Component blue(final String value) {
		return Component.literal(value).withStyle(ChatFormatting.BLUE);
	}

	private static Component red(final String value) {
		return Component.literal(value).withStyle(ChatFormatting.RED);
	}

	private static Component orange(final String value) {
		return Component.literal(value).withStyle(ChatFormatting.GOLD);
	}

	private static Component green(final String value) {
		return Component.literal(value).withStyle(ChatFormatting.GREEN);
	}

	private static final class Args {

		static Object[] get(final String key) {
			return switch (key) {
				case "tooltip.essentialis.ring.citrine_ring.line2" -> new Object[]{blue("16 blocks")};
				case "tooltip.essentialis.ring.jade_ring.line1" -> new Object[]{blue("10%")};
				case "tooltip.essentialis.ring.ruby_ring.line1" -> new Object[]{red("10%")};
				case "tooltip.essentialis.ring.sapphire_ring.line1" -> new Object[]{blue("25%"), blue("25%")};
				case "tooltip.essentialis.ring.tanzanite_ring.line1" -> new Object[]{blue("Doubles"), orange("any")};
				case "tooltip.essentialis.ring.topaz_ring.line1" -> new Object[]{blue("+1")};
				case "tooltip.essentialis.ring.netherite_citrine_ring.line2" -> new Object[]{blue("32 blocks")};
				case "tooltip.essentialis.ring.netherite_citrine_ring.line3" -> new Object[]{red("undead mobs"), orange("fire"), blue("16 blocks")};
				case "tooltip.essentialis.ring.netherite_jade_ring.line1" -> new Object[]{blue("20%")};
				case "tooltip.essentialis.ring.netherite_ruby_ring.line1" -> new Object[]{red("25%")};
				case "tooltip.essentialis.ring.netherite_sapphire_ring.line1" -> new Object[]{blue("50%"), blue("50%"), blue("50%")};
				case "tooltip.essentialis.ring.netherite_tanzanite_ring.line1" -> new Object[]{blue("Doubles"), green("any positive")};
				case "tooltip.essentialis.ring.netherite_topaz_ring.line1" -> new Object[]{blue("+2")};
				default -> new Object[0];
			};
		}
	}

	private static final class Fallbacks {

		static String get(final String key) {
			return switch (key) {
				case "tooltip.essentialis.ring.citrine_ring.line1" -> "Signals about danger.";
				case "tooltip.essentialis.ring.citrine_ring.line2" -> "All hostile mobs in range of %s will be highlighted.";
				case "tooltip.essentialis.ring.citrine_ring.line3" -> "This effect cannot be stacked.";
				case "tooltip.essentialis.ring.jade_ring.line1" -> "Grants %s bonus movement speed and jump boost.";
				case "tooltip.essentialis.ring.ruby_ring.line1" -> "Heals back %s of dealt damage to others.";
				case "tooltip.essentialis.ring.ruby_ring.line2" -> "This effect cannot be stacked.";
				case "tooltip.essentialis.ring.sapphire_ring.line1" -> "Grants %s bonus movement speed in water or lava and also reduces burn time by %s.";
				case "tooltip.essentialis.ring.tanzanite_ring.line1" -> "%s duration of %s status effect.";
				case "tooltip.essentialis.ring.tanzanite_ring.line2" -> "This effect cannot be stacked.";
				case "tooltip.essentialis.ring.topaz_ring.line1" -> "Grants %s Luck.";
				case "tooltip.essentialis.ring.netherite_citrine_ring.line1" -> "Signals about danger.";
				case "tooltip.essentialis.ring.netherite_citrine_ring.line2" -> "All hostile mobs in range of %s will be highlighted.";
				case "tooltip.essentialis.ring.netherite_citrine_ring.line3" -> "It will additionally set all %s on %s within %s.";
				case "tooltip.essentialis.ring.netherite_citrine_ring.line4" -> "This effect cannot be stacked.";
				case "tooltip.essentialis.ring.netherite_jade_ring.line1" -> "Grants %s bonus movement speed and jump boost.";
				case "tooltip.essentialis.ring.netherite_jade_ring.line2" -> "This effect additionally works while riding (on tamed mobs or vehicles).";
				case "tooltip.essentialis.ring.netherite_ruby_ring.line1" -> "Heals back %s of dealt damage to others.";
				case "tooltip.essentialis.ring.netherite_ruby_ring.line2" -> "This effect cannot be stacked.";
				case "tooltip.essentialis.ring.netherite_sapphire_ring.line1" -> "Grants %s bonus movement speed in water or lava, %s more breath and reduces burn time by %s.";
				case "tooltip.essentialis.ring.netherite_tanzanite_ring.line1" -> "%s duration of %s status effect.";
				case "tooltip.essentialis.ring.netherite_tanzanite_ring.line2" -> "This effect cannot be stacked.";
				case "tooltip.essentialis.ring.netherite_topaz_ring.line1" -> "Grants %s luck and provides bonus chance for:";
				case "tooltip.essentialis.ring.netherite_topaz_ring.line2" -> "- Rolling bonus rare loot inside chest. (10%)";
				case "tooltip.essentialis.ring.netherite_topaz_ring.line3" -> "- Applying looting twice on mob kill. (10%)";
				case "tooltip.essentialis.ring.netherite_topaz_ring.line4" -> "- Getting extra catch when fishing. (10%)";
				case "tooltip.essentialis.ring.netherite_topaz_ring.line5" -> "- Mining 2-8 gold nuggets out of quartz ore. (10%)";
				case "tooltip.essentialis.ring.netherite_topaz_ring.line6" -> "- Mining diamonds out of lapis or emerald ores. (5%)";
				default -> null;
			};
		}
	}
}
