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
		int total = 0;

		for (final RingKind kind : RingKind.values()) {
			if (!kind.netherite && kind.family() == family) {
				total += counts.getOrDefault(kind, 0);
			}
		}

		return total;
	}

	public static int netherite(final Map<RingKind, Integer> counts, final RingFamily family) {
		int total = 0;

		for (final RingKind kind : RingKind.values()) {
			if (kind.netherite && kind.family() == family) {
				total += counts.getOrDefault(kind, 0);
			}
		}

		return total;
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

	public static Identifier id(final String path) {
		return Essentialis.id(path);
	}
}
