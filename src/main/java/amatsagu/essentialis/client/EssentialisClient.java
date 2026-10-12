package amatsagu.essentialis.client;

import amatsagu.essentialis.client.render.ChakramRenderer;
import amatsagu.essentialis.common.registry.EssentialisEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;

public class EssentialisClient implements ClientModInitializer {

	@Override
	@SuppressWarnings({"unchecked", "rawtypes", "deprecation"})
	public void onInitializeClient() {
		EntityRendererRegistry.register(
			(EntityType) EssentialisEntities.CHAKRAM,
			(EntityRendererProvider) ChakramRenderer::new
		);

		ProjectileStats.init();

		HammerOutline.init();
	}
}
