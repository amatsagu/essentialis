package amatsagu.essentialis.common.recipe;

import amatsagu.essentialis.Essentialis;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;

/**
 * Recipe keys and helpers.
 *
 * <p>Recipes themselves are data-driven JSON. The only thing we do in code is unlock the chakram
 * recipe for a player once they obtain their first echo shard, so it appears in the recipe book
 * without shipping an advancement or polling every tick.</p>
 */
public final class EssentialisRecipes {
	public static final ResourceKey<Recipe<?>> CHAKRAM = ResourceKey.create(
			Registries.RECIPE,
			Essentialis.id("chakram")
	);

	private EssentialisRecipes() {
	}

	/** Awards the chakram recipe to {@code player} if they do not already know it. */
	public static void unlockChakram(final ServerPlayer player) {
		if (!player.getRecipeBook().contains(CHAKRAM)) {
			player.awardRecipesByKey(List.of(CHAKRAM));
		}
	}
}
