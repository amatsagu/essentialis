package amatsagu.essentialis.common.item;

import amatsagu.essentialis.common.WaybackStoneEffect;
import amatsagu.essentialis.common.registry.EssentialisSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class WaybackStoneItem extends Item {

	public static final int CHARGE_TICKS = 40;

	public WaybackStoneItem(final Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
		if (player.getCooldowns().isOnCooldown(player.getItemInHand(hand))) {
			return InteractionResult.FAIL;
		}

		player.startUsingItem(hand);

		return InteractionResult.CONSUME;
	}

	@Override
	public ItemUseAnimation getUseAnimation(final ItemStack stack) {
		return ItemUseAnimation.BOW;
	}

	@Override
	public int getUseDuration(final ItemStack stack, final LivingEntity entity) {
		return CHARGE_TICKS;
	}

	@Override
	public void onUseTick(final Level level, final LivingEntity entity, final ItemStack stack, final int remaining) {
		if (!(level instanceof ServerLevel serverLevel) || !(entity instanceof ServerPlayer player)) {
			return;
		}

		final int elapsed = CHARGE_TICKS - remaining;

		// Vanilla never calls onUseTick with remaining == 0, the last call has remaining == 1,
		// so the trigger fires one tick early. Stopping here also skips completeUsingItem.
		if (elapsed >= CHARGE_TICKS - 1) {
			serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), EssentialisSounds.JEWELRY_EFFECT.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
			WaybackStoneEffect.tryTrigger(player, stack);
			player.stopUsingItem();

			return;
		}

		final int count = 1 + elapsed / 8;
		final var random = serverLevel.getRandom();

		for (int i = 0; i < count; i++) {
			final double angle = random.nextDouble() * Math.PI * 2.0D;
			final double radius = 0.5D + random.nextDouble() * 0.8D;

			serverLevel.sendParticles(
				ParticleTypes.PORTAL,
				player.getX() + Math.cos(angle) * radius,
				player.getY() + random.nextDouble() * 2.0D,
				player.getZ() + Math.sin(angle) * radius,
				1,
				0.0D,
				0.0D,
				0.0D,
				0.1D
			);
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public void appendHoverText(
		final ItemStack stack,
		final Item.TooltipContext context,
		final TooltipDisplay display,
		final Consumer<Component> builder,
		final TooltipFlag flag
	) {
		builder.accept(Component.translatableWithFallback(
			"tooltip.essentialis.wayback_stone.line1",
			"Cheats death and returns the player to their respawn point."
		).withStyle(ChatFormatting.GRAY));

		builder.accept(Component.translatableWithFallback(
			"tooltip.essentialis.wayback_stone.line2",
			"Recharges over 15 minutes."
		).withStyle(ChatFormatting.GRAY));
	}
}
