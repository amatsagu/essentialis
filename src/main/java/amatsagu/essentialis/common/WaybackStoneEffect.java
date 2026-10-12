package amatsagu.essentialis.common;

import amatsagu.essentialis.common.registry.EssentialisItems;
import amatsagu.essentialis.compat.trinkets.TrinketsCompat;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

public final class WaybackStoneEffect {

	public static final int COOLDOWN_TICKS = 15 * 60 * 20;

	public static final float SURVIVE_HEALTH = 2.0F;

	public static final int FIRE_RESISTANCE_TICKS = 30 * 20;

	private WaybackStoneEffect() {
	}

	public static void init() {
		ServerLivingEntityEvents.ALLOW_DEATH.register(WaybackStoneEffect::onAllowDeath);
	}

	private static boolean onAllowDeath(final LivingEntity entity, final net.minecraft.world.damagesource.DamageSource source, final float amount) {
		if (!(entity instanceof ServerPlayer player) || player.isCreative() || player.isSpectator()) {
			return true;
		}

		final ItemStack stone = findStone(player);

		if (stone.isEmpty() || player.getCooldowns().isOnCooldown(stone)) {
			return true;
		}

		final TeleportTransition transition = player.findRespawnPositionAndUseSpawnBlock(true, TeleportTransition.DO_NOTHING);

		if (transition.missingRespawnBlock()) {
			return true;
		}

		final ServerLevel fromLevel = (ServerLevel) player.level();
		final Vec3 fromPos = player.position();

		player.setHealth(SURVIVE_HEALTH);
		player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, FIRE_RESISTANCE_TICKS, 0, false, true));
		player.teleport(transition);
		player.getCooldowns().addCooldown(stone, COOLDOWN_TICKS);
		player.awardStat(Stats.ITEM_USED.get(EssentialisItems.WAYBACK_STONE));

		fromLevel.sendParticles(ParticleTypes.PORTAL, fromPos.x, fromPos.y + 1.0D, fromPos.z, 60, 0.4D, 0.8D, 0.4D, 0.1D);
		fromLevel.playSound(null, fromPos.x, fromPos.y, fromPos.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

		final ServerLevel toLevel = (ServerLevel) player.level();
		final Vec3 toPos = player.position();

		toLevel.sendParticles(ParticleTypes.PORTAL, toPos.x, toPos.y + 1.0D, toPos.z, 60, 0.4D, 0.8D, 0.4D, 0.1D);
		toLevel.playSound(null, toPos.x, toPos.y, toPos.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

		return false;
	}

	private static ItemStack findStone(final ServerPlayer player) {
		if (TrinketsCompat.isLoaded()) {
			final ItemStack equipped = TrinketsCompat.findEquipped(player);

			if (!equipped.isEmpty()) {
				return equipped;
			}
		}

		for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
			if (player.getInventory().getItem(i).is(EssentialisItems.WAYBACK_STONE)) {
				return player.getInventory().getItem(i);
			}
		}

		return ItemStack.EMPTY;
	}
}
