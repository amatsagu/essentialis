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
		final List<Component> lines = new ArrayList<>(5);
		final String base = "tooltip.essentialis.ring." + kind.id;

		int line = 1;

		while (true) {
			final String key = base + ".line" + line;
			final String fallback = Fallbacks.get(key);

			if (fallback == null) {
				break;
			}

			lines.add(text(key, fallback, line == 2 && !kind.netherite));
			line++;
		}

		lines.add(Component.empty());
		lines.add(text(
			"tooltip.essentialis.ring.loot." + kind.id,
			LootFallbacks.get(kind),
			false
		));

		return List.copyOf(lines);
	}

	private static Component text(final String key, final String fallback, final boolean gold) {
		final Object[] args = Args.get(key);

		final MutableComponent line = args.length == 0
			? Component.translatableWithFallback(key, fallback)
			: Component.translatableWithFallback(key, fallback, args);

		return line.withStyle(gold ? ChatFormatting.GOLD : ChatFormatting.GRAY);
	}

	private static Component blue(final String value) {
		return Component.literal(value).withStyle(ChatFormatting.BLUE);
	}

	private static final class Args {

		static Object[] get(final String key) {
			return switch (key) {
				case "tooltip.essentialis.ring.citrine_ring.line1" -> new Object[]{blue("16")};
				case "tooltip.essentialis.ring.jade_ring.line1" -> new Object[]{blue("+10%")};
				case "tooltip.essentialis.ring.ruby_ring.line1" -> new Object[]{blue("5%")};
				case "tooltip.essentialis.ring.sapphire_ring.line1" -> new Object[]{blue("25%"), blue("+25%")};
				case "tooltip.essentialis.ring.tanzanite_ring.line1" -> new Object[]{blue("any")};
				case "tooltip.essentialis.ring.topaz_ring.line1" -> new Object[]{blue("+1")};
				case "tooltip.essentialis.ring.netherite_citrine_ring.line1" -> new Object[]{blue("32")};
				case "tooltip.essentialis.ring.netherite_citrine_ring.line2" -> new Object[]{blue("16")};
				case "tooltip.essentialis.ring.netherite_jade_ring.line1" -> new Object[]{blue("+20%")};
				case "tooltip.essentialis.ring.netherite_ruby_ring.line1" -> new Object[]{blue("10%")};
				case "tooltip.essentialis.ring.netherite_sapphire_ring.line1" -> new Object[]{blue("25%"), blue("+50%")};
				case "tooltip.essentialis.ring.netherite_tanzanite_ring.line1" -> new Object[]{blue("positive")};
				case "tooltip.essentialis.ring.netherite_topaz_ring.line1" -> new Object[]{blue("+2")};
				default -> new Object[0];
			};
		}
	}

	private static final class Fallbacks {

		static String get(final String key) {
			return switch (key) {
				case "tooltip.essentialis.ring.citrine_ring.line1" -> "Highlights nearby enemies within %s blocks.";
				case "tooltip.essentialis.ring.citrine_ring.line2" -> "Netherite: 32 blocks and ignites undead.";
				case "tooltip.essentialis.ring.jade_ring.line1" -> "Movement speed and jump height %s.";
				case "tooltip.essentialis.ring.jade_ring.line2" -> "Netherite: doubled, works while riding.";
				case "tooltip.essentialis.ring.ruby_ring.line1" -> "Heals %s of the damage dealt by the player.";
				case "tooltip.essentialis.ring.ruby_ring.line2" -> "Netherite: doubles the effect.";
				case "tooltip.essentialis.ring.sapphire_ring.line1" -> "Burns %s shorter. %s speed in water and lava.";
				case "tooltip.essentialis.ring.sapphire_ring.line2" -> "Netherite: +50% speed and breath.";
				case "tooltip.essentialis.ring.tanzanite_ring.line1" -> "Doubles the duration of %s effect on the player.";
				case "tooltip.essentialis.ring.tanzanite_ring.line2" -> "Netherite: positive effects only.";
				case "tooltip.essentialis.ring.topaz_ring.line1" -> "Grants %s Luck.";
				case "tooltip.essentialis.ring.topaz_ring.line2" -> "Netherite: +2 Luck and fortuitous finds.";
				case "tooltip.essentialis.ring.netherite_citrine_ring.line1" -> "Highlights nearby enemies within %s blocks.";
				case "tooltip.essentialis.ring.netherite_citrine_ring.line2" -> "Ignites undead within %s blocks.";
				case "tooltip.essentialis.ring.netherite_jade_ring.line1" -> "Movement speed and jump height %s, also while riding.";
				case "tooltip.essentialis.ring.netherite_ruby_ring.line1" -> "Heals %s of the damage dealt by the player.";
				case "tooltip.essentialis.ring.netherite_sapphire_ring.line1" -> "Burns %s shorter. %s speed in water and lava and longer breath.";
				case "tooltip.essentialis.ring.netherite_tanzanite_ring.line1" -> "Doubles the duration of %s effects on the player.";
				case "tooltip.essentialis.ring.netherite_topaz_ring.line1" -> "Grants %s Luck and fortuitous finds.";
				default -> null;
			};
		}
	}

	private static final class LootFallbacks {

		static String get(final RingKind kind) {
			if (kind.netherite) {
				return "Upgraded in a smithing table.";
			}

			return switch (kind.family()) {
				case CITRINE -> "Rare loot: mineshafts, outposts and mansions.";
				case JADE, TOPAZ -> "Rare loot: chests and barrels everywhere.";
				case RUBY -> "Rare loot: nether fortresses and bastions.";
				case SAPPHIRE -> "Rare loot: ocean ruins, shipwrecks and the drowned.";
				case TANZANITE -> "Rare loot: witches and illagers.";
			};
		}
	}
}
