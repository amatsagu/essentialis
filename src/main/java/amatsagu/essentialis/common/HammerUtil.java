package amatsagu.essentialis.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class HammerUtil {

	private HammerUtil() {
	}

	public static Direction.Axis planeAxis(final Vec3 look) {
		final double ax = Math.abs(look.x);
		final double ay = Math.abs(look.y);
		final double az = Math.abs(look.z);

		if (ay >= ax && ay >= az) {
			return Direction.Axis.Y;
		}

		return ax >= az ? Direction.Axis.X : Direction.Axis.Z;
	}

	public static BlockPos offset(final BlockPos pos, final Direction.Axis axis, final int a, final int b) {
		return switch (axis) {
			case X -> pos.offset(0, a, b);
			case Y -> pos.offset(a, 0, b);
			case Z -> pos.offset(a, b, 0);
		};
	}

	public static BlockPos.MutableBlockPos offsetInto(final BlockPos.MutableBlockPos out, final BlockPos pos, final Direction.Axis axis, final int a, final int b) {
		return switch (axis) {
			case X -> out.set(pos.getX(), pos.getY() + a, pos.getZ() + b);
			case Y -> out.set(pos.getX() + a, pos.getY(), pos.getZ() + b);
			case Z -> out.set(pos.getX() + a, pos.getY() + b, pos.getZ());
		};
	}

	public static boolean isMineable(final BlockState state) {
		return state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL);
	}
}
