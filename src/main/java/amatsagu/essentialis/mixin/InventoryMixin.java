package amatsagu.essentialis.mixin;

import amatsagu.essentialis.common.registry.EssentialisSounds;
import amatsagu.essentialis.common.ring.RingItem;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public abstract class InventoryMixin {

	@Inject(method = "setItem", at = @At("HEAD"))
	private void essentialis$ringEquipSound(final int slot, final ItemStack stack, final CallbackInfo ci) {
		final Inventory self = (Inventory) (Object) this;

		if (self.player.level().isClientSide()) {
			return;
		}

		if (!(stack.getItem() instanceof RingItem)) {
			return;
		}

		if (slot != self.getSelectedSlot() && slot != Inventory.SLOT_OFFHAND) {
			return;
		}

		if (self.getItem(slot).getItem() == stack.getItem()) {
			return;
		}

		self.player.level().playSound(null, self.player.getX(), self.player.getY(), self.player.getZ(), EssentialisSounds.JEWELRY_EQUIP.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
	}
}
