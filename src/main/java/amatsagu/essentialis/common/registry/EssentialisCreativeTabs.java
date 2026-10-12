package amatsagu.essentialis.common.registry;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public final class EssentialisCreativeTabs {

	private EssentialisCreativeTabs() {
	}

	public static void init() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(output ->
			output.accept(EssentialisItems.CHAKRAM)
		);

		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(output -> {
			output.accept(EssentialisItems.HAMMER);
			output.accept(EssentialisItems.WAYBACK_STONE);

			for (final Item item : EssentialisItems.RINGS.values()) {
				output.accept(item);
			}
		});
	}
}
