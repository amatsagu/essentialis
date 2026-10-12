package amatsagu.essentialis.common.ring;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class RingTooltips {

	private RingTooltips() {
	}

	public static List<Component> lines(final RingKind kind) {
		final List<Component> lines = new ArrayList<>(logicalLines(kind));

		lines.add(Component.empty());

		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
			return List.copyOf(lines);
		}

		return wrap(lines, kind);
	}

	private static List<Component> logicalLines(final RingKind kind) {
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

		return lines;
	}

	private static List<Component> wrap(final List<Component> lines, final RingKind kind) {
		if (kind == RingKind.NETHERITE_TOPAZ) {
			return List.copyOf(lines);
		}

		final Minecraft minecraft = Minecraft.getInstance();
		final int standard = standardWidth(minecraft);
		final List<Component> wrapped = new ArrayList<>(lines.size());

		for (final Component line : lines) {
			if (minecraft.font.width(line) <= standard) {
				wrapped.add(line);
				continue;
			}

			for (final FormattedText part : minecraft.font.splitIgnoringLanguage(line, standard)) {
				wrapped.add(flatten(part));
			}
		}

		return List.copyOf(wrapped);
	}

	private static int standardWidth(final Minecraft minecraft) {
		int width = 0;

		for (final Component line : logicalLines(RingKind.NETHERITE_TOPAZ)) {
			width = Math.max(width, minecraft.font.width(line));
		}

		return width;
	}

	private static Component flatten(final FormattedText text) {
		final MutableComponent out = Component.empty();

		text.visit((style, string) -> {
			out.append(Component.literal(string).withStyle(style));

			return Optional.empty();
		}, Style.EMPTY);

		return out;
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
