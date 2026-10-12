package amatsagu.essentialis.client.render;

import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Display;

/**
 * Renderer for the chakram.
 *
 * <p>Extending vanilla's {@link DisplayRenderer.ItemDisplayRenderer} gives us the full item display
 * rendering pipeline (including the transformation we animate for the spin) without duplicating any
 * version-specific rendering code. The entity is an {@link Display.ItemDisplay}, so all of the
 * render-state extraction still type-checks through the {@code ? super E} signature used by
 * {@code EntityRenderDispatcher}.</p>
 */
public class ChakramRenderer extends DisplayRenderer.ItemDisplayRenderer {
	public ChakramRenderer(final EntityRendererProvider.Context context) {
		super(context);
	}
}
