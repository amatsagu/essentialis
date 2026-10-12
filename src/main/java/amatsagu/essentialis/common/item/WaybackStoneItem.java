package amatsagu.essentialis.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class WaybackStoneItem extends Item {

	public WaybackStoneItem(final Item.Properties properties) {
		super(properties);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void appendHoverText(
		final ItemStack stack,
		final Item.TooltipContext context,
		final TooltipDisplay display,
		final Consumer<Component> builder,
		final TooltipFlag flag
	) {
		builder.accept(Component.translatableWithFallback(
			"tooltip.essentialis.wayback_stone.line1",
			"Cheats death and returns you to your respawn point."
		).withStyle(ChatFormatting.GRAY));

		builder.accept(Component.translatableWithFallback(
			"tooltip.essentialis.wayback_stone.line2",
			"Recharges over 15 minutes."
		).withStyle(ChatFormatting.GRAY));
	}
}
