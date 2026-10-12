package amatsagu.essentialis.common;

import amatsagu.essentialis.Essentialis;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/**
 * Data-pack tags owned by Essentialis.
 *
 * <p>These mirror the original Chakram data pack's block tags so the flight and harvesting behaviour
 * is identical, but they live in our own namespace to avoid clashing with other mods.</p>
 */
public final class EssentialisTags {
	/**
	 * Blocks the chakram can fly through. Anything not in this tag causes the chakram to bounce.
	 * This is the exact list used by the original data pack's {@code chakram:blocks} tag.
	 */
	public static final TagKey<Block> CHAKRAM_PASSABLE = TagKey.create(
			Registries.BLOCK,
			Essentialis.id("chakram_passable")
	);

	/** Blocks that are destroyed (as if by hand) when flying with the Harvesting enchantment. */
	public static final TagKey<Block> CHAKRAM_HARVESTING = TagKey.create(
			Registries.BLOCK,
			Essentialis.id("chakram_harvesting")
	);

	private EssentialisTags() {
	}
}
