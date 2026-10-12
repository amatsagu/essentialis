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
