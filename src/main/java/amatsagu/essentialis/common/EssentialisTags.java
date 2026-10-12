package amatsagu.essentialis.common;

import amatsagu.essentialis.Essentialis;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class EssentialisTags {

	public static final TagKey<Block> CHAKRAM_PASSABLE = TagKey.create(
		Registries.BLOCK,
		Essentialis.id("chakram_passable")
	);

	public static final TagKey<Block> CHAKRAM_HARVESTING = TagKey.create(
		Registries.BLOCK,
		Essentialis.id("chakram_harvesting")
	);

	private EssentialisTags() {
	}
}
