package amatsagu.essentialis.common.registry;

import amatsagu.essentialis.Essentialis;
import amatsagu.essentialis.common.entity.ChakramEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class EssentialisEntities {

	public static final EntityType<ChakramEntity> CHAKRAM = register(
		"chakram",
		EntityType.Builder.<ChakramEntity>of(ChakramEntity::new, MobCategory.MISC)
			.sized(0.5F, 0.5F)
			.clientTrackingRange(8)
			.updateInterval(1)
	);

	private EssentialisEntities() {
	}

	private static <T extends Entity> EntityType<T> register(final String name, final EntityType.Builder<T> builder) {
		final ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Essentialis.id(name));

		return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
	}

	public static void init() {
	}
}
