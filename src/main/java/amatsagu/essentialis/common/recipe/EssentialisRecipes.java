package amatsagu.essentialis.common.recipe;

import amatsagu.essentialis.Essentialis;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;

public final class EssentialisRecipes {

	public static final ResourceKey<Recipe<?>> CHAKRAM = ResourceKey.create(
		Registries.RECIPE,
		Essentialis.id("chakram")
	);

	private EssentialisRecipes() {
	}

	public static void unlockChakram(final ServerPlayer player) {
		if (!player.getRecipeBook().contains(CHAKRAM)) {
			player.awardRecipesByKey(List.of(CHAKRAM));
		}
	}
}
