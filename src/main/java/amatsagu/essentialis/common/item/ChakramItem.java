package amatsagu.essentialis.common.item;

import amatsagu.essentialis.common.entity.ChakramEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * The Echo Chakram.
 *
 * <p>Using it (right click) throws the actual item as a returning projectile. In survival the item
 * leaves the hand while in flight and is given back when it returns; creative players keep their copy
 * and simply spawn a short-lived projectile. This mirrors the original data pack, where the thrown
 * entity carried the item stack and gave it back on pickup.</p>
 */
public class ChakramItem extends Item {
	public ChakramItem(final Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
		final ItemStack stack = player.getItemInHand(hand);

		if (stack.nextDamageWillBreak()) {
			return InteractionResult.FAIL;
		}

		if (level instanceof ServerLevel serverLevel) {
			final ChakramEntity chakram = new ChakramEntity(serverLevel, player, stack.copy(), hand);
			serverLevel.addFreshEntity(chakram);

			if (!player.isCreative()) {
				// The item is physically thrown, so remove it from the hand.
				player.setItemInHand(hand, ItemStack.EMPTY);
			}

			player.awardStat(Stats.ITEM_USED.get(this));
		}

		return InteractionResult.SUCCESS;
	}
}
