package amatsagu.essentialis.common.ring;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.function.Consumer;

public class RingItem extends Item {

	private final RingKind kind;

	public RingItem(final Item.Properties properties, final RingKind kind) {
		super(properties);

		this.kind = kind;
	}

	public RingKind kind() {
		return this.kind;
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
		for (final Component line : RingTooltips.lines(this.kind)) {
			builder.accept(line);
		}
	}

	public static List<RingKind> all() {
		return List.of(RingKind.values());
	}
}
