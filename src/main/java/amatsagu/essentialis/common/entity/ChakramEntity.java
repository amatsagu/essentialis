package amatsagu.essentialis.common.entity;

import amatsagu.essentialis.common.EssentialisTags;
import amatsagu.essentialis.common.enchantment.EssentialisEnchantments;
import amatsagu.essentialis.common.registry.EssentialisEntities;
import com.mojang.math.Transformation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChakramEntity extends Display.ItemDisplay {

	private static final int STEPS_PER_TICK = 30;

	private static final float STEP_DISTANCE = 0.05F;

	private static final float LIQUID_STEP_DISTANCE = 0.025F;

	private static final float BASE_DAMAGE = 5.0F;

	private static final int BASE_MAX_DISTANCE = 300;

	private static final int LIGHTWEIGHT_DISTANCE_PER_LEVEL = 150;

	private static final int MIN_HIT_DISTANCE = 30;

	private static final int BOUNCE_COOLDOWN_STEPS = 15;

	private static final double BOUNCE_SEARCH_RADIUS = 10.0D;

	private static final double HIT_MARGIN = 0.35D;

	private static final EntityTypeTest<Entity, LivingEntity> LIVING_TEST = EntityTypeTest.forClass(LivingEntity.class);

	public static final int MAX_BOUNCES = 10;

	private EntityReference<Entity> ownerReference;

	private InteractionHand throwHand = InteractionHand.MAIN_HAND;

	private boolean creativeThrow;

	private boolean fresh = true;

	private boolean returning;

	private boolean pickupPending;

	private boolean broken;

	private int age;

	private int distance;

	private int maxDistance = BASE_MAX_DISTANCE;

	private int bounces;

	private int bounceCooldown;

	private boolean bouncing;

	private boolean wasInAir;

	private int phasingCount;

	private int bouncingCount;

	private float lastSpinAngle = Float.NaN;

	private final Set<Integer> hitEntities = new HashSet<>();

	private final Set<Integer> bouncedEntities = new HashSet<>();

	private double dirX;

	private double dirY;

	private double dirZ;

	private final List<Entity> hitCandidates = new ArrayList<>();

	private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

	public ChakramEntity(final EntityType<ChakramEntity> type, final Level level) {
		super(type, level);
	}

	public ChakramEntity(final ServerLevel level, final Player owner, final ItemStack stack, final InteractionHand hand) {
		this(EssentialisEntities.CHAKRAM, level);

		this.ownerReference = EntityReference.of(owner);
		this.throwHand = hand;
		this.creativeThrow = owner.isCreative();

		this.setItemStack(stack.copyWithCount(1));
		this.setItemTransform(ItemDisplayContext.NONE);
		this.setTransformationInterpolationDuration(3);
		this.setPosRotInterpolationDuration(1);

		final Vec3 eye = owner.getEyePosition();

		this.setPos(eye.x, eye.y - 0.12D, eye.z);
		this.setYRot(owner.getYRot());
		this.setXRot(owner.getXRot());
		this.updateDirection();

		level.playSound(null, eye.x, eye.y, eye.z, SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.0F, 1.3F);
		level.playSound(null, eye.x, eye.y, eye.z, SoundEvents.BREEZE_DEFLECT, SoundSource.PLAYERS, 0.5F, 1.1F);

		this.maxDistance = BASE_MAX_DISTANCE
			+ LIGHTWEIGHT_DISTANCE_PER_LEVEL * EssentialisEnchantments.getLevel(stack, EssentialisEnchantments.LIGHTWEIGHT);

		this.updateTransformation(0.0F);
	}

	@Override
	public void tick() {
		super.tick();

		if (this.level().isClientSide()) {
			return;
		}

		if (this.fresh) {
			this.fresh = false;

			return;
		}

		this.age++;

		final float spinAngle = this.currentSpinAngle();

		if (spinAngle != this.lastSpinAngle) {
			this.lastSpinAngle = spinAngle;
			this.updateTransformation(spinAngle);
		}

		if (this.pickupPending) {
			this.completePickup();

			return;
		}

		if (this.returning) {
			this.faceOwner();
		}

		final ServerLevel level = (ServerLevel) this.level();

		this.refreshHitCandidates(level);

		for (int step = 0; step < STEPS_PER_TICK; step++) {
			if (this.isRemoved()) {
				return;
			}

			this.step();
		}
	}

	private void step() {
		final ServerLevel level = (ServerLevel) this.level();

		if (this.bounceCooldown > 0) {
			this.bounceCooldown--;

			if (this.bounceCooldown == 0) {
				this.bouncing = false;
			}
		}

		this.distance++;

		this.moveStep(level);

		if (!this.returning && !this.pickupPending) {
			this.harvest(level);
		}

		if (!this.returning && !this.pickupPending && this.distance >= this.maxDistance) {
			this.startReturn();
		}

		if (!this.returning && !this.bouncing && !this.pickupPending) {
			this.checkBounce(level);
		}

		if (!this.pickupPending && this.distance >= MIN_HIT_DISTANCE) {
			final Entity target = this.findEntityHit();

			if (target != null) {
				this.onHitEntity(level, target);
			}
		}

		if (this.returning && !this.pickupPending) {
			final Entity owner = this.getOwnerEntity();

			if (owner == null) {
				this.dropAndDiscard(level);
			} else if (this.position().distanceToSqr(owner.getEyePosition()) < 1.0D) {
				this.pickupPending = true;
			}
		}
	}

	private void moveStep(final ServerLevel level) {
		this.mutablePos.set(this.getX(), this.getY(), this.getZ());

		final boolean liquid = !level.getFluidState(this.mutablePos).isEmpty();

		if (liquid) {
			if (this.wasInAir) {
				level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_SPLASH, SoundSource.PLAYERS, 0.3F, 1.0F);
				level.sendParticles(ParticleTypes.SPLASH, this.getX(), this.getY(), this.getZ(), 10, 0.35D, 0.35D, 0.35D, 0.5D);
			}

			this.wasInAir = false;

			level.sendParticles(ParticleTypes.BUBBLE, this.getX(), this.getY(), this.getZ(), 1, 0.05D, 0.05D, 0.05D, 0.0D);

			this.advance(LIQUID_STEP_DISTANCE);
		} else {
			this.wasInAir = true;

			this.advance(STEP_DISTANCE);
		}
	}

	private void advance(final double distance) {
		this.setPos(
			this.getX() + this.dirX * distance,
			this.getY() + this.dirY * distance,
			this.getZ() + this.dirZ * distance
		);
	}

	private void updateDirection() {
		final Vec3 direction = this.getViewVector(1.0F);

		this.dirX = direction.x;
		this.dirY = direction.y;
		this.dirZ = direction.z;
	}

	private void harvest(final ServerLevel level) {
		if (!EssentialisEnchantments.has(this.getItemStack(), EssentialisEnchantments.HARVESTING)) {
			return;
		}

		this.mutablePos.set(this.getX(), this.getY(), this.getZ());

		if (level.getBlockState(this.mutablePos).is(EssentialisTags.CHAKRAM_HARVESTING)) {
			level.destroyBlock(this.mutablePos, true);
		}
	}

	private void checkBounce(final ServerLevel level) {
		this.mutablePos.set(
			this.getX() + this.dirX * STEP_DISTANCE,
			this.getY() + this.dirY * STEP_DISTANCE,
			this.getZ() + this.dirZ * STEP_DISTANCE
		);

		final BlockState state = level.getBlockState(this.mutablePos);

		if (state.is(EssentialisTags.CHAKRAM_PASSABLE)) {
			return;
		}

		this.startBounce(level, this.mutablePos);
	}

	private void startBounce(final ServerLevel level, final BlockPos ahead) {
		if (level.getBlockState(ahead).is(Blocks.TARGET)) {
			level.setBlock(ahead, Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
			level.setBlock(ahead, Blocks.TARGET.defaultBlockState(), 3);
			this.startReturn();

			return;
		}

		if (this.bounces >= MAX_BOUNCES) {
			this.startReturn();

			return;
		}

		this.bouncing = true;
		this.bounceCooldown = BOUNCE_COOLDOWN_STEPS;
		this.bounces++;

		level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SHIELD_BREAK, SoundSource.PLAYERS, 0.1F, 2.0F);

		final double x = this.getX();
		final double y = this.getY();
		final double z = this.getZ();

		float yaw = this.getYRot();
		float pitch = this.getXRot();

		if (!this.isPassable(level, x + 0.251D, y, z)) {
			yaw = -yaw;
		}

		if (!this.isPassable(level, x - 0.251D, y, z)) {
			yaw = -yaw;
		}

		if (!this.isPassable(level, x, y + 0.251D, z)) {
			pitch = -pitch;
		}

		if (!this.isPassable(level, x, y - 0.251D, z)) {
			pitch = -pitch;
		}

		if (!this.isPassable(level, x, y, z + 0.251D)) {
			yaw = 180.0F - yaw;
		}

		if (!this.isPassable(level, x, y, z - 0.251D)) {
			yaw = 180.0F - yaw;
		}

		this.setYRot(yaw);
		this.setXRot(Mth.clamp(pitch, -90.0F, 90.0F));

		this.updateDirection();
		this.refreshHitCandidates(level);
	}

	private boolean isPassable(final ServerLevel level, final double x, final double y, final double z) {
		return level.getBlockState(BlockPos.containing(x, y, z)).is(EssentialisTags.CHAKRAM_PASSABLE);
	}

	private Entity findEntityHit() {
		final double x = this.getX();
		final double y = this.getY();
		final double z = this.getZ();

		Entity best = null;
		double bestDistance = HIT_MARGIN * HIT_MARGIN;

		for (final Entity candidate : this.hitCandidates) {
			if (!this.canHit(candidate)) {
				continue;
			}

			final double distance = distanceToBoxSqr(candidate.getBoundingBox(), x, y, z);

			if (distance <= bestDistance) {
				bestDistance = distance;
				best = candidate;
			}
		}

		return best;
	}

	private void refreshHitCandidates(final ServerLevel level) {
		this.hitCandidates.clear();

		final double x = this.getX();
		final double y = this.getY();
		final double z = this.getZ();
		final double sweep = STEP_DISTANCE * STEPS_PER_TICK;
		final double endX = x + this.dirX * sweep;
		final double endY = y + this.dirY * sweep;
		final double endZ = z + this.dirZ * sweep;
		final double margin = HIT_MARGIN + STEP_DISTANCE;

		final AABB box = new AABB(
			Math.min(x, endX) - margin,
			Math.min(y, endY) - margin,
			Math.min(z, endZ) - margin,
			Math.max(x, endX) + margin,
			Math.max(y, endY) + margin,
			Math.max(z, endZ) + margin
		);

		level.getEntities(LIVING_TEST, box, entity -> true, this.hitCandidates);
	}

	private static double distanceToBoxSqr(final AABB box, final double x, final double y, final double z) {
		final double dx = Math.max(Math.max(box.minX - x, 0.0D), x - box.maxX);
		final double dy = Math.max(Math.max(box.minY - y, 0.0D), y - box.maxY);
		final double dz = Math.max(Math.max(box.minZ - z, 0.0D), z - box.maxZ);

		return dx * dx + dy * dy + dz * dz;
	}

	private boolean canHit(final Entity entity) {
		if (entity == this || !entity.isAlive() || entity.isSpectator()) {
			return false;
		}

		if (!(entity instanceof LivingEntity) || entity instanceof ArmorStand) {
			return false;
		}

		if (this.hitEntities.contains(entity.getId())) {
			return false;
		}

		return true;
	}

	private void onHitEntity(final ServerLevel level, final Entity target) {
		if (target == this.getOwnerEntity()) {
			if (this.returning || this.bounces >= 1) {
				this.pickupPending = true;
			}

			return;
		}

		if (this.returning) {
			return;
		}

		if (!this.hitEntities.add(target.getId())) {
			return;
		}

		if (target.getType() == EntityTypes.ENDERMAN) {
			this.hitEnderman(level, target);

			return;
		}

		final ItemStack weapon = this.getItemStack();
		final DamageSource source = this.damageSource();

		float damage = BASE_DAMAGE + this.ricochetBonus();

		damage = EnchantmentHelper.modifyDamage(level, weapon, target, source, damage);

		final boolean hurt = target.hurtServer(level, source, damage);

		if (hurt) {
			EnchantmentHelper.doPostAttackEffectsWithItemSource(level, target, source, weapon);
		}

		this.applyDurability(level);

		if (this.tryPhasing(target)) {
			return;
		}

		if (this.tryBouncing(level, target)) {
			return;
		}

		this.startReturn();
	}

	private float ricochetBonus() {
		final int level = EssentialisEnchantments.getLevel(this.getItemStack(), EssentialisEnchantments.RICOCHET);

		if (level <= 0) {
			return 0.0F;
		}

		float bonus = 0.5F * level;

		for (int bounce = Math.min(this.bounces, MAX_BOUNCES); bounce >= 1; bounce--) {
			bonus += bounce <= 5 ? 2.0F : 1.0F;
		}

		return bonus;
	}

	private boolean tryPhasing(final Entity target) {
		final int level = EssentialisEnchantments.getLevel(this.getItemStack(), EssentialisEnchantments.PHASING);

		if (level <= 0 || this.phasingCount >= level) {
			return false;
		}

		this.phasingCount++;

		return true;
	}

	private boolean tryBouncing(final ServerLevel level, final Entity target) {
		final int bouncingLevel = EssentialisEnchantments.getLevel(this.getItemStack(), EssentialisEnchantments.BOUNCING);

		if (bouncingLevel <= 0 || this.bouncingCount >= bouncingLevel) {
			return false;
		}

		final Entity next = this.findBounceTarget(level);

		if (next == null) {
			return false;
		}

		this.bouncedEntities.add(next.getId());
		this.bouncingCount++;

		this.faceTowards(next.getEyePosition());
		this.refreshHitCandidates(level);

		level.sendParticles(ParticleTypes.GUST, this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.1D);
		level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BREEZE_DEFLECT, SoundSource.PLAYERS, 0.5F, 1.3F);

		return true;
	}

	private Entity findBounceTarget(final ServerLevel level) {
		final AABB search = this.getBoundingBox().inflate(BOUNCE_SEARCH_RADIUS);

		Entity best = null;
		double bestDistance = Double.MAX_VALUE;

		for (final Entity candidate : level.getEntities(this, search, entity -> this.canBounceTo(entity))) {
			if (!this.hasLineOfSight(level, candidate)) {
				continue;
			}

			final double distance = candidate.distanceToSqr(this);

			if (distance < bestDistance) {
				bestDistance = distance;
				best = candidate;
			}
		}

		return best;
	}

	private boolean canBounceTo(final Entity entity) {
		if (entity == this || !entity.isAlive() || entity.isSpectator()) {
			return false;
		}

		if (!(entity instanceof LivingEntity) || entity instanceof ArmorStand) {
			return false;
		}

		if (entity == this.getOwnerEntity() || this.bouncedEntities.contains(entity.getId())) {
			return false;
		}

		return true;
	}

	private boolean hasLineOfSight(final ServerLevel level, final Entity target) {
		final Vec3 from = this.position();
		final Vec3 to = target.getEyePosition();

		return level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
	}

	private void hitEnderman(final ServerLevel level, final Entity enderman) {
		level.sendParticles(ParticleTypes.PORTAL, enderman.getX(), enderman.getY() + 1.0D, enderman.getZ(), 100, 0.35D, 0.8D, 0.35D, 0.0D);

		final double dx = (level.getRandom().nextDouble() - 0.5D) * 64.0D;
		final double dz = (level.getRandom().nextDouble() - 0.5D) * 64.0D;

		enderman.teleportTo(enderman.getX() + dx, enderman.getY(), enderman.getZ() + dz);
	}

	private void applyDurability(final ServerLevel level) {
		final ItemStack stack = this.getItemStack();

		if (!stack.isDamageableItem()) {
			return;
		}

		final int amount = EnchantmentHelper.processDurabilityChange(level, stack, 1);

		if (amount <= 0) {
			return;
		}

		stack.setDamageValue(stack.getDamageValue() + amount);

		if (stack.getDamageValue() >= stack.getMaxDamage()) {
			this.broken = true;
			this.onBroken(level);
		}
	}

	private void onBroken(final ServerLevel level) {
		level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_BREAK.value(), SoundSource.PLAYERS, 1.0F, 1.0F);

		level.sendParticles(
			new ItemParticleOption(ParticleTypes.ITEM, Items.ECHO_SHARD),
			this.getX(),
			this.getY(),
			this.getZ(),
			10,
			0.1D,
			0.1D,
			0.1D,
			0.06D
		);

		this.discard();
	}

	private void startReturn() {
		if (this.returning) {
			return;
		}

		this.returning = true;

		final Entity owner = this.getOwnerEntity();

		if (owner != null) {
			this.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.TRIDENT_RETURN, SoundSource.PLAYERS, 0.5F, 1.5F);
		}

		this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BREEZE_DEFLECT, SoundSource.PLAYERS, 1.0F, 1.3F);
	}

	private void faceOwner() {
		final Entity owner = this.getOwnerEntity();

		if (owner != null) {
			this.faceTowards(owner.getEyePosition());
		}
	}

	private void faceTowards(final Vec3 target) {
		final Vec3 delta = target.subtract(this.position());
		final double horizontal = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
		final float yaw = (float) (Mth.atan2(delta.z, delta.x) * (180.0F / (float) Math.PI)) - 90.0F;
		final float pitch = (float) (-(Mth.atan2(delta.y, horizontal) * (180.0F / (float) Math.PI)));

		this.setYRot(yaw);
		this.setXRot(Mth.clamp(pitch, -90.0F, 90.0F));
		this.updateDirection();
	}

	private DamageSource damageSource() {
		final Entity owner = this.getOwnerEntity();
		final ServerLevel level = (ServerLevel) this.level();

		if (owner instanceof Player player) {
			return level.damageSources().playerAttack(player);
		}

		return level.damageSources().thrown(this, owner);
	}

	private void completePickup() {
		final ServerLevel level = (ServerLevel) this.level();
		final Entity owner = this.getOwnerEntity();
		final ItemStack stack = this.getItemStack().copy();

		if (!this.broken && !this.creativeThrow && owner instanceof ServerPlayer player) {
			if (player.getItemInHand(this.throwHand).isEmpty()) {
				player.setItemInHand(this.throwHand, stack);
			} else if (!player.getInventory().add(stack)) {
				this.spawnAtLocation(level, stack, 0.1F);
			}

			level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, 1.0F);
		} else if (!this.broken && !this.creativeThrow) {
			this.spawnAtLocation(level, stack, 0.1F);
		}

		this.discard();
	}

	private void dropAndDiscard(final ServerLevel level) {
		if (!this.broken && !this.creativeThrow) {
			this.spawnAtLocation(level, this.getItemStack().copy(), 0.1F);
		}

		this.discard();
	}

	private Entity getOwnerEntity() {
		return this.ownerReference == null ? null : this.ownerReference.getEntity(this.level(), Entity.class);
	}

	private float currentSpinAngle() {
		final int step = (this.age / 3) % 4;

		return (float) (Math.PI / 2.0D) * ((step + 1) % 4);
	}

	private void updateTransformation(final float spinAngle) {
		final Quaternionf leftRotation = new Quaternionf().rotationAxis(-(float) (Math.PI / 2.0D), 1.0F, 0.0F, 0.0F);
		final Quaternionf rightRotation = new Quaternionf().rotationAxis(spinAngle, 0.0F, 0.0F, 1.0F);

		this.setTransformation(new Transformation(new Vector3f(0.0F, 0.0F, 0.0F), leftRotation, new Vector3f(1.0F, 1.0F, 1.0F), rightRotation));
	}

	@Override
	protected void readAdditionalSaveData(final ValueInput input) {
		super.readAdditionalSaveData(input);

		this.ownerReference = EntityReference.read(input, "Owner");
		this.throwHand = input.getIntOr("ThrowHand", 0) == 1 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
		this.creativeThrow = input.getBooleanOr("CreativeThrow", false);
		this.fresh = input.getBooleanOr("Fresh", false);
		this.returning = input.getBooleanOr("Returning", false);
		this.age = input.getIntOr("Age", 0);
		this.distance = input.getIntOr("Distance", 0);
		this.maxDistance = input.getIntOr("MaxDistance", BASE_MAX_DISTANCE);
		this.bounces = input.getIntOr("Bounces", 0);
		this.phasingCount = input.getIntOr("PhasingCount", 0);
		this.bouncingCount = input.getIntOr("BouncingCount", 0);
	}

	@Override
	protected void addAdditionalSaveData(final ValueOutput output) {
		super.addAdditionalSaveData(output);

		EntityReference.store(this.ownerReference, output, "Owner");
		output.putInt("ThrowHand", this.throwHand == InteractionHand.OFF_HAND ? 1 : 0);
		output.putBoolean("CreativeThrow", this.creativeThrow);
		output.putBoolean("Fresh", this.fresh);
		output.putBoolean("Returning", this.returning);
		output.putInt("Age", this.age);
		output.putInt("Distance", this.distance);
		output.putInt("MaxDistance", this.maxDistance);
		output.putInt("Bounces", this.bounces);
		output.putInt("PhasingCount", this.phasingCount);
		output.putInt("BouncingCount", this.bouncingCount);
	}
}
