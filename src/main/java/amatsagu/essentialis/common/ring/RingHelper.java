package amatsagu.essentialis.common.ring;

import amatsagu.essentialis.Essentialis;
import amatsagu.essentialis.compat.trinkets.TrinketsCompat;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.Map;

public final class RingHelper {

	public static final Identifier JADE_SPEED = Essentialis.id("jade_speed");

	public static final Identifier JADE_JUMP = Essentialis.id("jade_jump");

	public static final Identifier TOPAZ_LUCK = Essentialis.id("topaz_luck");

	public static final Identifier SAPPHIRE_WATER = Essentialis.id("sapphire_water");

	public static final Identifier SAPPHIRE_OXYGEN = Essentialis.id("sapphire_oxygen");

	public static final Identifier VEHICLE_SPEED = Essentialis.id("vehicle_speed");

	public static final Identifier VEHICLE_JUMP = Essentialis.id("vehicle_jump");

	public static final Identifier VEHICLE_FLYING_SPEED = Essentialis.id("vehicle_flying_speed");

	private static final Map<RingFamily, RingKind> NORMAL_KIND = new EnumMap<>(RingFamily.class);

	private static final Map<RingFamily, RingKind> NETHERITE_KIND = new EnumMap<>(RingFamily.class);

	static {
		for (final RingKind kind : RingKind.values()) {
			(kind.netherite ? NETHERITE_KIND : NORMAL_KIND).put(kind.family(), kind);
		}
	}

	private RingHelper() {
	}

	public static Map<RingKind, Integer> collect(final ServerPlayer player) {
		final Map<RingKind, Integer> counts = new EnumMap<>(RingKind.class);

		add(counts, player.getMainHandItem());
		add(counts, player.getOffhandItem());

		if (TrinketsCompat.isLoaded()) {
			for (final ItemStack stack : TrinketsCompat.ringSlotStacks(player)) {
				add(counts, stack);
			}
		}

		return counts;
	}

	private static void add(final Map<RingKind, Integer> counts, final ItemStack stack) {
		if (stack.getItem() instanceof RingItem ring) {
			counts.merge(ring.kind(), 1, Integer::sum);
		}
	}

	public static int normal(final Map<RingKind, Integer> counts, final RingFamily family) {
		return counts.getOrDefault(NORMAL_KIND.get(family), 0);
	}

	public static int netherite(final Map<RingKind, Integer> counts, final RingFamily family) {
		return counts.getOrDefault(NETHERITE_KIND.get(family), 0);
	}

	public static void setModifier(
		final LivingEntity entity,
		final Holder<Attribute> attribute,
		final Identifier id,
		final double amount,
		final AttributeModifier.Operation operation
	) {
		final AttributeInstance instance = entity.getAttribute(attribute);

		if (instance == null) {
			return;
		}

		final AttributeModifier existing = instance.getModifier(id);

		if (amount == 0.0D) {
			if (existing != null) {
				instance.removeModifier(id);
			}

			return;
		}

		if (existing == null || existing.amount() != amount || existing.operation() != operation) {
			instance.removeModifier(id);
			instance.addTransientModifier(new AttributeModifier(id, amount, operation));
		}
	}
}
