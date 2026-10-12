package amatsagu.essentialis.mixin;

import amatsagu.essentialis.common.recipe.EssentialisRecipes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Unlocks the chakram recipe the moment an echo shard first enters a player's inventory.
 *
 * <p>Rather than polling the inventory on a tick, we hook the single place vanilla itself dispatches
 * slot changes ({@code AbstractContainerMenu.triggerSlotListeners}). Vanilla only reaches the
 * listener call after confirming a slot actually changed, so this is event-driven and costs nothing
 * when nothing happens. It covers every way an item can enter the inventory (pickups, chests,
 * crafting, commands) and works for any open container because all menus share this code path.</p>
 */
@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
	@Redirect(
			method = "triggerSlotListeners",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/inventory/ContainerListener;slotChanged(Lnet/minecraft/world/inventory/AbstractContainerMenu;ILnet/minecraft/world/item/ItemStack;)V"
			)
	)
	private void essentialis$unlockChakramOnEchoShard(
			final ContainerListener listener,
			final AbstractContainerMenu menu,
			final int slotIndex,
			final ItemStack stack
	) {
		if (stack.is(Items.ECHO_SHARD)) {
			final Slot slot = menu.getSlot(slotIndex);
			final Container container = slot.container;
			if (container instanceof Inventory inventory && inventory.player instanceof ServerPlayer player) {
				EssentialisRecipes.unlockChakram(player);
			}
		}

		listener.slotChanged(menu, slotIndex, stack);
	}
}
