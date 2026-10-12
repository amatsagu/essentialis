package amatsagu.essentialis.client;

import amatsagu.essentialis.common.HammerUtil;
import amatsagu.essentialis.common.registry.EssentialisItems;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class HammerOutline {

	private HammerOutline() {
	}

	public static void init() {
		LevelExtractionEvents.AFTER_BLOCK_OUTLINE_EXTRACTION.register(HammerOutline::afterBlockOutlineExtraction);
	}

	private static void afterBlockOutlineExtraction(final LevelExtractionContext context, final HitResult hitResult) {
		final LocalPlayer player = Minecraft.getInstance().player;

		if (player == null || !player.getMainHandItem().is(EssentialisItems.HAMMER)) {
			return;
		}

		if (!(hitResult instanceof BlockHitResult) || hitResult.getType() == HitResult.Type.MISS) {
			return;
		}

		final BlockOutlineRenderState existing = context.levelState().blockOutlineRenderState;

		if (existing == null) {
			return;
		}

		final ClientLevel level = context.level();
		final BlockPos center = existing.pos();
		final Direction.Axis axis = HammerUtil.planeAxis(player.getViewVector(1.0F));
		final CollisionContext collision = CollisionContext.of(player);

		VoxelShape combined = existing.shape();

		for (int a = -1; a <= 1; a++) {
			for (int b = -1; b <= 1; b++) {
				if (a == 0 && b == 0) {
					continue;
				}

				final BlockPos target = HammerUtil.offset(center, axis, a, b);
				final BlockState state = level.getBlockState(target);

				if (state.isAir() || !HammerUtil.isMineable(state)) {
					continue;
				}

				final VoxelShape shape = state.getShape(level, target, collision).move(
					target.getX() - center.getX(),
					target.getY() - center.getY(),
					target.getZ() - center.getZ()
				);

				combined = Shapes.or(combined, shape);
			}
		}

		context.levelState().blockOutlineRenderState = new BlockOutlineRenderState(
			center,
			existing.isTranslucent(),
			existing.highContrast(),
			combined
		);
	}
}
