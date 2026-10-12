package amatsagu.essentialis.mixin;

import amatsagu.essentialis.common.ring.RingEffects;
import amatsagu.essentialis.common.ring.RingFamily;
import amatsagu.essentialis.common.ring.RingHelper;
import amatsagu.essentialis.common.ring.RingKind;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class TanzaniteMixin {

	@ModifyVariable(
		method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
		at = @At("HEAD"),
		argsOnly = true
	)
	private MobEffectInstance essentialis$doubleDuration(final MobEffectInstance effect) {
		if (!((Object) this instanceof ServerPlayer player)) {
			return effect;
		}

		final Map<RingKind, Integer> rings = RingHelper.collect(player);
		final int normal = RingHelper.normal(rings, RingFamily.TANZANITE);
		final int netherite = RingHelper.netherite(rings, RingFamily.TANZANITE);

		if (normal == 0 && netherite == 0) {
			return effect;
		}

		if (normal > 0 || effect.getEffect().value().getCategory() == MobEffectCategory.BENEFICIAL) {
			RingEffects.playTanzaniteSound(player);
			return doubled(effect);
		}

		return effect;
	}

	private static MobEffectInstance doubled(final MobEffectInstance effect) {
		if (effect.getDuration() == MobEffectInstance.INFINITE_DURATION || effect.getDuration() > Integer.MAX_VALUE / 2) {
			return effect;
		}

		return new MobEffectInstance(
			effect.getEffect(),
			effect.getDuration() * 2,
			effect.getAmplifier(),
			effect.isAmbient(),
			effect.isVisible(),
			effect.showIcon()
		);
	}
}
