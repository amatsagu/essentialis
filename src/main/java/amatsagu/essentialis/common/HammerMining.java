package amatsagu.essentialis.common;

import amatsagu.essentialis.common.registry.EssentialisItems;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Gives the hammer its 3x3 mining.
 *
 * <p>We let vanilla break the targeted block normally and then, from the {@code AFTER} block-break
 * event, break the surrounding blocks by invoking the player's own game mode break logic. That means
 * every extra block goes through the exact vanilla path: correct drops (including Fortune/Silk
 * Touch and iron-tier checks), durability cost of one per block, stats, and the block break events
 * other mods (land protection, etc.) listen to.</p>
 *
 * <p>A simple re-entrancy guard stops the extra breaks from triggering further 3x3 breaks.</p>
 */
public final class HammerMining {
	private static boolean breaking;

	private HammerMining() {
	}

	public static void init() {
		PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
			if (breaking || !(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
				return;
			}
			if (!player.getMainHandItem().is(EssentialisItems.HAMMER)) {
				return;
			}

			final Direction.Axis axis = HammerUtil.planeAxis(player.getViewVector(1.0F));

			breaking = true;
			try {
				for (int a = -1; a <= 1; a++) {
					for (int b = -1; b <= 1; b++) {
						if (a == 0 && b == 0) {
							continue;
						}

						final BlockPos target = HammerUtil.offset(pos, axis, a, b);
						final BlockState targetState = serverLevel.getBlockState(target);
						if (targetState.isAir() || !HammerUtil.isMineable(targetState) || !serverLevel.mayInteract(player, target)) {
							continue;
						}

						// Break it exactly like a normal player break. Re-entrant breaks bail out above.
						serverPlayer.gameMode.destroyBlock(target);
						if (player.getMainHandItem().isEmpty()) {
							return;
						}
					}
				}
			} finally {
				breaking = false;
			}
		});
	}
}
