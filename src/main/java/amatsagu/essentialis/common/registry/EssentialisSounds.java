package amatsagu.essentialis.common.registry;

import amatsagu.essentialis.Essentialis;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public final class EssentialisSounds {

	public static final Holder<SoundEvent> JEWELRY_EQUIP = register("jewelry_equip");

	public static final Holder<SoundEvent> JEWELRY_EFFECT = register("jewelry_effect");

	private EssentialisSounds() {
	}

	private static Holder<SoundEvent> register(final String name) {
		return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, Essentialis.id(name), SoundEvent.createVariableRangeEvent(Essentialis.id(name)));
	}

	public static void init() {
	}
}
