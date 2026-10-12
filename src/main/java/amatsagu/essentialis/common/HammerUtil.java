package amatsagu.essentialis.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Shared geometry/rules for the hammer's 3x3 mining behaviour, used by both the server break handler
 * and the client outline so the two always agree on which blocks are affected.
 */
public final class HammerUtil {
	private HammerUtil() {
	}

	/**
	 * Picks the plane the 3x3 is mined in from the player's look direction. The dominant axis of the
	 * look vector becomes the plane's normal, so looking at a wall mines a vertical 3x3 and looking
	 * up/down mines a horizontal 3x3.
	 */
	public static Direction.Axis planeAxis(final Vec3 look) {
		final double ax = Math.abs(look.x);
		final double ay = Math.abs(look.y);
		final double az = Math.abs(look.z);
		if (ay >= ax && ay >= az) {
			return Direction.Axis.Y;
		}
		return ax >= az ? Direction.Axis.X : Direction.Axis.Z;
	}

	/** Offsets {@code pos} by {@code (a, b)} within the plane perpendicular to {@code axis}. */
	public static BlockPos offset(final BlockPos pos, final Direction.Axis axis, final int a, final int b) {
		return switch (axis) {
			case X -> pos.offset(0, a, b);
			case Y -> pos.offset(a, 0, b);
			case Z -> pos.offset(a, b, 0);
		};
	}

	/** Whether the hammer may mine this block (iron pickaxe + iron shovel blocks). */
	public static boolean isMineable(final BlockState state) {
		return state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL);
	}
}
