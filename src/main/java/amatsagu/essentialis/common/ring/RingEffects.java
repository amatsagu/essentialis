package amatsagu.essentialis.common.ring;

import amatsagu.essentialis.common.registry.EssentialisSounds;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.TeamColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;

public final class RingEffects {

	private static final int SLOW_INTERVAL = 20;

	private static final double CITRINE_RADIUS = 16.0D;

	private static final double NETHERITE_CITRINE_RADIUS = 32.0D;

	private static final double UNDEAD_FIRE_RADIUS = 16.0D;

	private static final int UNDEAD_FIRE_TICKS = 100;

	private static final String CITRINE_TEAM = "essentialis_citrine";

	private static final EntityTypeTest<Entity, LivingEntity> LIVING = EntityTypeTest.forClass(LivingEntity.class);

	private static final Map<ServerPlayer, Entity> LAST_VEHICLES = new WeakHashMap<>();

	private static final Map<ServerPlayer, CitrineState> CITRINE_STATES = new WeakHashMap<>();

	private static final Map<ServerPlayer, Long> TANZANITE_SOUNDS = new WeakHashMap<>();

	private static final int CITRINE_REARM_TICKS = 100;

	private static final int TANZANITE_SOUND_COOLDOWN = 60;

	private record CitrineState(boolean armed, long lastActive) {
	}

	private static final List<Item> FISHING_BONUS = List.of(
		Items.NAUTILUS_SHELL,
		Items.SADDLE,
		Items.NAME_TAG
	);

	private static final List<Item> CHEST_BONUS = List.of(
		Items.DIAMOND,
		Items.EMERALD,
		Items.GOLD_INGOT,
		Items.AMETHYST_SHARD
	);

	private RingEffects() {
	}

	public static void init() {
		ServerTickEvents.END_SERVER_TICK.register(RingEffects::tickServer);
		ServerLivingEntityEvents.AFTER_DAMAGE.register(RingEffects::lifesteal);
		PlayerBlockBreakEvents.AFTER.register(RingEffects::topazMining);
		LootTableEvents.MODIFY_DROPS.register(RingEffects::topazFishing);
		LootTableEvents.MODIFY_DROPS.register(RingEffects::topazChests);
		LootTableEvents.MODIFY_DROPS.register(RingEffects::topazLooting);
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			final Entity last = LAST_VEHICLES.remove(handler.player);

			if (last instanceof LivingEntity living) {
				clearVehicleModifiers(living);
			}
		});
	}

	private static void tickServer(final MinecraftServer server) {
		for (final ServerPlayer player : server.getPlayerList().getPlayers()) {
			tickPlayer(player);
		}

		tickCitrineGlobal(server);
	}

	private static void tickPlayer(final ServerPlayer player) {
		final Map<RingKind, Integer> rings = RingHelper.collect(player);

		final int jade = RingHelper.normal(rings, RingFamily.JADE);
		final int netherJade = RingHelper.netherite(rings, RingFamily.JADE);
		final int topazLuck = RingHelper.normal(rings, RingFamily.TOPAZ) + 2 * RingHelper.netherite(rings, RingFamily.TOPAZ);

		final double speedBonus = 0.1D * jade + 0.2D * netherJade;

		RingHelper.setModifier(player, Attributes.MOVEMENT_SPEED, RingHelper.JADE_SPEED, speedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		RingHelper.setModifier(player, Attributes.JUMP_STRENGTH, RingHelper.JADE_JUMP, speedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		RingHelper.setModifier(player, Attributes.LUCK, RingHelper.TOPAZ_LUCK, topazLuck, AttributeModifier.Operation.ADD_VALUE);

		final double waterBonus = 0.25D * RingHelper.normal(rings, RingFamily.SAPPHIRE) + 0.5D * RingHelper.netherite(rings, RingFamily.SAPPHIRE);
		final double oxygenBonus = 0.5D * RingHelper.netherite(rings, RingFamily.SAPPHIRE);

		RingHelper.setModifier(player, Attributes.WATER_MOVEMENT_EFFICIENCY, RingHelper.SAPPHIRE_WATER, waterBonus, AttributeModifier.Operation.ADD_VALUE);
		RingHelper.setModifier(player, Attributes.OXYGEN_BONUS, RingHelper.SAPPHIRE_OXYGEN, oxygenBonus, AttributeModifier.Operation.ADD_VALUE);

		tickVehicle(player, 0.2D * netherJade);
		tickSapphire(player, RingHelper.normal(rings, RingFamily.SAPPHIRE), RingHelper.netherite(rings, RingFamily.SAPPHIRE));
	}

	public static void playTanzaniteSound(final ServerPlayer player) {
		final long now = player.level().getGameTime();

		if (now - TANZANITE_SOUNDS.getOrDefault(player, -1000L) < TANZANITE_SOUND_COOLDOWN) {
			return;
		}

		TANZANITE_SOUNDS.put(player, now);
		playEffect(player);
	}

	private static void playEffect(final ServerPlayer player) {
		playEffect((ServerLevel) player.level(), player.position());
	}

	private static void playEffect(final ServerLevel level, final Vec3 pos) {
		level.playSound(null, pos.x, pos.y, pos.z, EssentialisSounds.JEWELRY_EFFECT.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
	}

	private static void tickVehicle(final ServerPlayer player, final double bonus) {
		final Entity current = player.getVehicle();
		final Entity last = current == null ? LAST_VEHICLES.remove(player) : LAST_VEHICLES.put(player, current);

		if (last != null && last != current && last instanceof LivingEntity living) {
			clearVehicleModifiers(living);
		}

		if (current == null) {
			return;
		}

		if (current instanceof LivingEntity living) {
			RingHelper.setModifier(living, Attributes.MOVEMENT_SPEED, RingHelper.VEHICLE_SPEED, bonus, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
			RingHelper.setModifier(living, Attributes.JUMP_STRENGTH, RingHelper.VEHICLE_JUMP, bonus, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
			RingHelper.setModifier(living, Attributes.FLYING_SPEED, RingHelper.VEHICLE_FLYING_SPEED, bonus, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		} else if (bonus > 0.0D && current instanceof VehicleEntity) {
			final Vec3 motion = current.getDeltaMovement();
			current.setDeltaMovement(motion.x * (1.0D + bonus), motion.y, motion.z * (1.0D + bonus));
		}
	}

	private static void clearVehicleModifiers(final LivingEntity vehicle) {
		RingHelper.setModifier(vehicle, Attributes.MOVEMENT_SPEED, RingHelper.VEHICLE_SPEED, 0.0D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		RingHelper.setModifier(vehicle, Attributes.JUMP_STRENGTH, RingHelper.VEHICLE_JUMP, 0.0D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		RingHelper.setModifier(vehicle, Attributes.FLYING_SPEED, RingHelper.VEHICLE_FLYING_SPEED, 0.0D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
	}

	private static void tickSapphire(final ServerPlayer player, final int normal, final int netherite) {
		if (normal + netherite == 0) {
			return;
		}

		final int fire = player.getRemainingFireTicks();

		if (fire > 0 && player.getRandom().nextFloat() < Math.min(1.0D, normal / 3.0D + netherite)) {
			player.setRemainingFireTicks(fire - 1);
		}

		if (player.isInLava()) {
			final double factor = 1.0D + 0.25D * normal + 0.5D * netherite;
			final Vec3 motion = player.getDeltaMovement();

			player.setDeltaMovement(motion.x * factor, motion.y, motion.z * factor);
		}

		if (netherite > 0 && player.isUnderWater()) {
			final int air = player.getAirSupply();

			if (air < player.getMaxAirSupply() && air % 3 == 0) {
				player.setAirSupply(air + 1);
			}
		}
	}

	private record Wearer(ServerPlayer player, double radius, boolean netherite) {
	}

	private static void tickCitrineGlobal(final MinecraftServer server) {
		if (server.getTickCount() % SLOW_INTERVAL != 0) {
			return;
		}

		final Map<ServerLevel, List<Wearer>> wearers = new HashMap<>();
		final Set<ServerPlayer> withRings = new HashSet<>();

		for (final ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (!(player.level() instanceof ServerLevel level) || player.isSpectator()) {
				continue;
			}

			final Map<RingKind, Integer> rings = RingHelper.collect(player);
			final boolean netherite = RingHelper.netherite(rings, RingFamily.CITRINE) > 0;

			if (RingHelper.normal(rings, RingFamily.CITRINE) == 0 && !netherite) {
				continue;
			}

			withRings.add(player);
			wearers.computeIfAbsent(level, key -> new ArrayList<>())
				.add(new Wearer(player, netherite ? NETHERITE_CITRINE_RADIUS : CITRINE_RADIUS, netherite));
		}

		CITRINE_STATES.keySet().removeIf(player -> !withRings.contains(player));

		for (final var entry : wearers.entrySet()) {
			tickCitrineLevel(entry.getKey(), entry.getValue());
		}

		for (final ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (player.level() instanceof ServerLevel level && !wearers.containsKey(level)) {
				syncCitrineTeam(level, Set.of());
			}
		}
	}

	private static void tickCitrineLevel(final ServerLevel level, final List<Wearer> wearers) {
		AABB union = null;

		for (final Wearer wearer : wearers) {
			final AABB box = wearer.player.getBoundingBox().inflate(wearer.radius);
			union = union == null ? box : union.minmax(box);
		}

		final Set<String> highlighted = new HashSet<>();
		final Set<ServerPlayer> active = new HashSet<>();
		final Scoreboard board = level.getScoreboard();

		for (final LivingEntity mob : level.getEntities(LIVING, union, RingEffects::isEnemy)) {
			boolean covered = false;

			for (final Wearer wearer : wearers) {
				final double distance = mob.position().distanceToSqr(wearer.player.position());

				if (distance > wearer.radius * wearer.radius) {
					continue;
				}

				covered = true;
				active.add(wearer.player);

				if (wearer.netherite
					&& distance <= UNDEAD_FIRE_RADIUS * UNDEAD_FIRE_RADIUS
					&& mob.is(EntityTypeTags.UNDEAD)
					&& mob.getRemainingFireTicks() < UNDEAD_FIRE_TICKS - SLOW_INTERVAL) {
					mob.setRemainingFireTicks(UNDEAD_FIRE_TICKS);
				}
			}

			if (!covered) {
				continue;
			}

			ensureGlow(mob);

			final var mobTeam = board.getPlayersTeam(mob.getScoreboardName());

			if (mobTeam == null || mobTeam.getName().equals(CITRINE_TEAM)) {
				highlighted.add(mob.getScoreboardName());
			}
		}

		final long now = level.getGameTime();

		for (final Wearer wearer : wearers) {
			final CitrineState state = CITRINE_STATES.getOrDefault(wearer.player, new CitrineState(false, 0L));

			if (active.contains(wearer.player)) {
				if (!state.armed()) {
					playEffect(wearer.player);
				}

				CITRINE_STATES.put(wearer.player, new CitrineState(true, now));
			} else if (state.armed() && now - state.lastActive() > CITRINE_REARM_TICKS) {
				CITRINE_STATES.put(wearer.player, new CitrineState(false, state.lastActive()));
			}
		}

		syncCitrineTeam(level, highlighted);
	}

	private static void ensureGlow(final LivingEntity mob) {
		final MobEffectInstance current = mob.getEffect(MobEffects.GLOWING);

		if (current == null || current.getDuration() <= SLOW_INTERVAL) {
			mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, SLOW_INTERVAL * 3, 0, false, false));
		}
	}

	private static boolean isEnemy(final LivingEntity entity) {
		return entity.isAlive() && !entity.isSpectator() && entity.getType().getCategory() == MobCategory.MONSTER;
	}

	private static void syncCitrineTeam(final ServerLevel level, final Set<String> highlighted) {
		final Scoreboard board = level.getScoreboard();
		PlayerTeam team = board.getPlayerTeam(CITRINE_TEAM);

		if (highlighted.isEmpty() && (team == null || team.getPlayers().isEmpty())) {
			return;
		}

		if (team == null) {
			team = board.addPlayerTeam(CITRINE_TEAM);
			team.setColor(Optional.of(TeamColor.RED));
			team.setNameTagVisibility(Team.Visibility.NEVER);
			team.setDeathMessageVisibility(Team.Visibility.NEVER);
			team.setCollisionRule(Team.CollisionRule.NEVER);
		}

		for (final String member : List.copyOf(team.getPlayers())) {
			if (!highlighted.contains(member)) {
				board.removePlayerFromTeam(member, team);
			}
		}

		for (final String member : highlighted) {
			if (board.getPlayersTeam(member) == null) {
				board.addPlayerToTeam(member, team);
			}
		}
	}

	private static void lifesteal(final LivingEntity victim, final DamageSource source, final float original, final float dealt, final boolean blocked) {
		if (!(source.getEntity() instanceof ServerPlayer attacker) || attacker == victim || dealt <= 0.0F) {
			return;
		}

		final Map<RingKind, Integer> rings = RingHelper.collect(attacker);
		final double rate = RingHelper.netherite(rings, RingFamily.RUBY) > 0 ? 0.25D : (RingHelper.normal(rings, RingFamily.RUBY) > 0 ? 0.10D : 0.0D);

		if (rate > 0.0D) {
			attacker.heal((float) (dealt * rate));
		}
	}

	private static void topazMining(final Level level, final Player player, final BlockPos pos, final BlockState state, final BlockEntity blockEntity) {
		if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
			return;
		}

		if (RingHelper.netherite(RingHelper.collect(serverPlayer), RingFamily.TOPAZ) == 0) {
			return;
		}

		final Block block = state.getBlock();

		if (block == Blocks.LAPIS_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE || block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) {
			if (serverPlayer.getRandom().nextFloat() < 0.05F) {
				Block.popResource(serverLevel, pos, new ItemStack(Items.DIAMOND));
				playEffect(serverLevel, Vec3.atCenterOf(pos));
			}

			return;
		}

		if (block == Blocks.NETHER_QUARTZ_ORE && serverPlayer.getRandom().nextFloat() < 0.10F) {
			Block.popResource(serverLevel, pos, new ItemStack(Items.GOLD_NUGGET, 2 + serverPlayer.getRandom().nextInt(7)));
			playEffect(serverLevel, Vec3.atCenterOf(pos));
		}
	}

	private static void topazLooting(final Holder<LootTable> table, final LootContext context, final List<ItemStack> drops) {
		if (!(context.getOptional(LootContextParams.LAST_DAMAGE_PLAYER) instanceof ServerPlayer killer)) {
			return;
		}

		if (RingHelper.netherite(RingHelper.collect(killer), RingFamily.TOPAZ) == 0) {
			return;
		}

		if (drops.isEmpty() || killer.getRandom().nextFloat() >= 0.10F) {
			return;
		}

		final List<ItemStack> bonus = new ArrayList<>(drops.size());

		for (final ItemStack drop : drops) {
			if (!drop.isEmpty()) {
				bonus.add(drop.copy());
			}
		}

		if (bonus.isEmpty()) {
			return;
		}

		drops.addAll(bonus);
		playEffect(killer);
	}

	private static void topazFishing(final Holder<LootTable> table, final LootContext context, final List<ItemStack> drops) {
		final var key = table.unwrapKey();

		if (key.isEmpty() || !(key.get() == BuiltInLootTables.FISHING_FISH || key.get() == BuiltInLootTables.FISHING_JUNK || key.get() == BuiltInLootTables.FISHING_TREASURE)) {
			return;
		}

		if (!(context.getOptional(LootContextParams.THIS_ENTITY) instanceof FishingHook hook)) {
			return;
		}

		if (!(hook.getPlayerOwner() instanceof ServerPlayer owner)) {
			return;
		}

		if (RingHelper.netherite(RingHelper.collect(owner), RingFamily.TOPAZ) == 0) {
			return;
		}

		if (owner.getRandom().nextFloat() < 0.10F) {
			drops.add(new ItemStack(FISHING_BONUS.get(owner.getRandom().nextInt(FISHING_BONUS.size()))));
			playEffect(owner);
		}
	}

	private static void topazChests(final Holder<LootTable> table, final LootContext context, final List<ItemStack> drops) {
		final var key = table.unwrapKey();

		if (key.isEmpty() || !key.get().identifier().getPath().startsWith("chests/")) {
			return;
		}

		if (!(context.getOptional(LootContextParams.THIS_ENTITY) instanceof ServerPlayer opener)) {
			return;
		}

		if (RingHelper.netherite(RingHelper.collect(opener), RingFamily.TOPAZ) == 0) {
			return;
		}

		if (opener.getRandom().nextFloat() < 0.10F) {
			drops.add(new ItemStack(CHEST_BONUS.get(opener.getRandom().nextInt(CHEST_BONUS.size()))));
			playEffect(opener);
		}
	}
}
