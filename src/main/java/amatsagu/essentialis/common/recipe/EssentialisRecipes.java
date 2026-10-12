package amatsagu.essentialis.common.recipe;

import amatsagu.essentialis.Essentialis;
import amatsagu.essentialis.common.ring.RingItem;
import amatsagu.essentialis.common.ring.RingKind;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;

public final class EssentialisRecipes {

	public static final ResourceKey<Recipe<?>> CHAKRAM = ResourceKey.create(
		Registries.RECIPE,
		Essentialis.id("chakram")
	);

	public static final ResourceKey<Recipe<?>> HAMMER = ResourceKey.create(
		Registries.RECIPE,
		Essentialis.id("hammer")
	);

	public static final ResourceKey<Recipe<?>> WAYBACK_STONE = ResourceKey.create(
		Registries.RECIPE,
		Essentialis.id("wayback_stone")
	);

	private EssentialisRecipes() {
	}

	public static void unlockForIngredient(final ServerPlayer player, final ItemStack stack) {
		if (stack.is(Items.ECHO_SHARD)) {
			unlock(player, CHAKRAM);
		} else if (stack.is(Items.IRON_BLOCK)) {
			unlock(player, HAMMER);
		} else if (stack.is(Items.HEART_OF_THE_SEA)) {
			unlock(player, WAYBACK_STONE);
		} else if (stack.getItem() instanceof RingItem ring && !ring.kind().netherite) {
			unlock(player, smithingKey(ring.kind()));
		}
	}

	private static ResourceKey<Recipe<?>> smithingKey(final RingKind kind) {
		return ResourceKey.create(Registries.RECIPE, Essentialis.id("netherite_" + kind.id + "_smithing"));
	}

	private static void unlock(final ServerPlayer player, final ResourceKey<Recipe<?>> recipe) {
		if (!player.getRecipeBook().contains(recipe)) {
			player.awardRecipesByKey(List.of(recipe));
		}
	}
}
