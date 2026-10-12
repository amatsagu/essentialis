package amatsagu.essentialis.common;

import amatsagu.essentialis.common.registry.EssentialisItems;
import amatsagu.essentialis.common.registry.EssentialisSounds;
import amatsagu.essentialis.compat.trinkets.TrinketsCompat;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

public final class WaybackStoneEffect {

	public static final float SURVIVE_HEALTH = 2.0F;

	public static final int FIRE_RESISTANCE_TICKS = 30 * 20;

	private WaybackStoneEffect() {
	}

	public static void init() {
		ServerLivingEntityEvents.ALLOW_DEATH.register(WaybackStoneEffect::onAllowDeath);
	}

	private static boolean onAllowDeath(final LivingEntity entity, final DamageSource source, final float amount) {
		if (!(entity instanceof ServerPlayer player) || player.isCreative() || player.isSpectator()) {
			return true;
		}

		return !tryTrigger(player, findStone(player));
	}

	public static boolean tryTrigger(final ServerPlayer player, final ItemStack stone) {
		if (stone.isEmpty() || WaybackCooldowns.isOnCooldown(player)) {
			return false;
		}

		final TeleportTransition transition = player.findRespawnPositionAndUseSpawnBlock(true, TeleportTransition.DO_NOTHING);

		if (transition.missingRespawnBlock()) {
			return false;
		}

		apply(player, transition);

		return true;
	}

	private static void apply(final ServerPlayer player, final TeleportTransition transition) {
		final ServerLevel fromLevel = (ServerLevel) player.level();
		final Vec3 fromPos = player.position();

		player.setHealth(SURVIVE_HEALTH);
		player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, FIRE_RESISTANCE_TICKS, 0, true, true));
		player.teleport(transition);
		WaybackCooldowns.start(player);
		player.awardStat(Stats.ITEM_USED.get(EssentialisItems.WAYBACK_STONE));

		burst(fromLevel, fromPos);
		burst((ServerLevel) player.level(), player.position());
	}

	private static void burst(final ServerLevel level, final Vec3 pos) {
		level.sendParticles(ParticleTypes.PORTAL, pos.x, pos.y + 1.0D, pos.z, 60, 0.4D, 0.8D, 0.4D, 0.1D);
		level.playSound(null, pos.x, pos.y, pos.z, EssentialisSounds.JEWELRY_EFFECT.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
	}

	private static ItemStack findStone(final ServerPlayer player) {
		if (TrinketsCompat.isLoaded()) {
			final ItemStack equipped = TrinketsCompat.findInRingSlot(player);

			if (!equipped.isEmpty()) {
				return equipped;
			}
		}

		final ItemStack mainHand = player.getMainHandItem();

		if (mainHand.is(EssentialisItems.WAYBACK_STONE)) {
			return mainHand;
		}

		final ItemStack offHand = player.getOffhandItem();

		if (offHand.is(EssentialisItems.WAYBACK_STONE)) {
			return offHand;
		}

		return ItemStack.EMPTY;
	}
}
