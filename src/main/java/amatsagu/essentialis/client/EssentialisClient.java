package amatsagu.essentialis.client;

import amatsagu.essentialis.client.render.ChakramRenderer;
import amatsagu.essentialis.common.registry.EssentialisEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;

/**
 * Client entry point. The chakram is an {@code ItemDisplay} subclass, so it reuses vanilla's item
 * display renderer; we only need to bind that renderer to our custom entity type.
 */
public class EssentialisClient implements ClientModInitializer {
	@Override
	@SuppressWarnings({"unchecked", "rawtypes", "deprecation"})
	public void onInitializeClient() {
		EntityRendererRegistry.register(
				(EntityType) EssentialisEntities.CHAKRAM,
				(EntityRendererProvider) ChakramRenderer::new
		);

		// Chakram stat block (thrown damage, range, ...) that adapts to enchantments.
		ChakramStats.init();

		// 3x3 block outline while holding the hammer.
		HammerOutline.init();
	}
}
