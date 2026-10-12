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
				player.setItemInHand(hand, ItemStack.EMPTY);
			}

			player.awardStat(Stats.ITEM_USED.get(this));
		}

		return InteractionResult.SUCCESS;
	}
}
