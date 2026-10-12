package amatsagu.essentialis.compat.trinkets;

import amatsagu.essentialis.common.registry.EssentialisSounds;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.callback.TrinketCallback;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class RingEquipSounds implements TrinketCallback {

	@Override
	public Holder<SoundEvent> getEquipSound(final ItemStack stack, final TrinketSlotAccess access, final LivingEntity entity) {
		return EssentialisSounds.JEWELRY_EQUIP;
	}
}
