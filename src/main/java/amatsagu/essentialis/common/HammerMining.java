package amatsagu.essentialis.common;

import amatsagu.essentialis.common.registry.EssentialisItems;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

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

			// Extra breaks re-enter this event; the guard above stops them from cascading.
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
