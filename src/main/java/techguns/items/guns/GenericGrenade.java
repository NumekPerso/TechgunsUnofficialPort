package techguns.items.guns;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import techguns.TGPackets;
import techguns.Techguns;
import techguns.api.damagesystem.DamageType;
import techguns.api.guns.IChargeableItem;
import techguns.api.render.IItemTGRenderer;
import techguns.entities.projectiles.EnumBulletFirePos;
import techguns.entities.projectiles.GrenadeProjectile;
import techguns.items.GenericItem;
import techguns.packets.PacketPlaySound;
import techguns.util.TGSoundCategory;
import techguns.util.TextUtil;

/**
 * Throwable grenade, charged by holding the use key (throw strength), thrown on release.
 */
public class GenericGrenade extends GenericItem implements IItemTGRenderer, IChargeableItem {
	private int maxUseDur;

	public float damage = 20.0f;
	public float radius = 3.0f;
	public int maxBounces = 3;

	public float damageMin = 10.0f;
	public float radiusMin = 5.0f;

	public float fullChargeTime = 30.0f;

	public int ticksToLive = 200;
	public float speed = 0.75f;

	public float spread = 0.1f;

	public float penetration = 0f;

	protected SoundEvent startSound = null;

	IGrenadeProjectileFactory<? extends GrenadeProjectile> projectile_factory;

	/**
	 * 1.12 registered grenades separately (addToItemList false), the port adds them to the item list
	 */
	public GenericGrenade(String name, int stacksize, int maxUseDur, IGrenadeProjectileFactory<? extends GrenadeProjectile> projectile_factory) {
		super(name, true, new Properties().stacksTo(stacksize).setNoRepair());
		this.maxUseDur = maxUseDur;
		this.projectile_factory = projectile_factory;
	}

	public GenericGrenade setStartSound(SoundEvent sound) {
		this.startSound = sound;
		return this;
	}

	public GenericGrenade setDamageAndRadius(float damage, float radiusMax, float damageMin, float radiusMin) {
		this.damage = damage;
		this.radius = radiusMax;
		this.damageMin = damageMin;
		this.radiusMin = radiusMin;
		return this;
	}

	public GenericGrenade setMaxBounces(int maxBounces) {
		this.maxBounces = maxBounces;
		return this;
	}

	public GenericGrenade setFullChargeTime(float fullChargeTime) {
		this.fullChargeTime = fullChargeTime;
		return this;
	}

	public GenericGrenade setTicksToLive(int ticksToLive) {
		this.ticksToLive = ticksToLive;
		return this;
	}

	public GenericGrenade setSpeed(float speed) {
		this.speed = speed;
		return this;
	}

	public GenericGrenade setSpread(float spread) {
		this.spread = spread;
		return this;
	}

	public GenericGrenade setPenetration(float penetration) {
		this.penetration = penetration;
		return this;
	}

	@Override
	public float getFullChargeTime() {
		return this.fullChargeTime;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		playerIn.startUsingItem(handIn);
		if (!worldIn.isClientSide && this.startSound != null) {
			TGPackets.network.sendToAllAround(new PacketPlaySound(this.startSound, playerIn, 1, 1, false, false, true, true, TGSoundCategory.PLAYER_EFFECT), TGPackets.targetPointAroundEnt(playerIn, 24.0f));
		}
		return InteractionResultHolder.consume(playerIn.getItemInHand(handIn));
	}

	@Override
	public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
		int j = this.getUseDuration(stack) - timeLeft;

		InteractionHand hand = entityLiving.getUsedItemHand();
		boolean leftHand = hand == InteractionHand.OFF_HAND;
		if (entityLiving.getMainArm() == HumanoidArm.LEFT) {
			leftHand = !leftHand;
		}

		EnumBulletFirePos firePos = leftHand ? EnumBulletFirePos.LEFT : EnumBulletFirePos.RIGHT;

		float f = j / (this.fullChargeTime);

		if (f > 1.0F) {
			f = 1.0F;
		}

		if (entityLiving instanceof Player ply) {
			if (!ply.getAbilities().instabuild) {
				stack.shrink(1);
			}
		}

		if (!worldIn.isClientSide) {
			spawnGrenade(worldIn, entityLiving, f, firePos);
		}
		entityLiving.swing(hand);
	}

	public void spawnGrenade(Level world, LivingEntity entityLiving, float charge, EnumBulletFirePos firePos) {
		//1.12 divided by the raw charge, a release in the same tick (charge 0) gave an infinite gravity
		float grav = (0.015f / Math.max(charge, 1.0f / this.fullChargeTime));

		GrenadeProjectile proj = this.projectile_factory.createProjectile(world, entityLiving, this.damage, speed, this.ticksToLive, spread, this.radius, this.radiusMin, this.damageMin, penetration, GenericGun.getDoBlockDamage(entityLiving), firePos, radiusMin, grav, charge, maxBounces);

		world.addFreshEntity(proj);
	}

	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getUseDuration(ItemStack item) {
		return 72000;
	}

	public int getMaxUseDur() {
		return maxUseDur;
	}

	protected DamageType getDamageType() {
		return DamageType.EXPLOSION;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		if (Techguns.proxy.isShiftKeyDown()) {
			tooltip.add(Component.literal(TextUtil.trans("techguns.gun.tooltip.damageType") + ": ").withStyle(ChatFormatting.GRAY).append(this.getDamageType().getDisplayComponent()));
			tooltip.add(Component.literal(TextUtil.trans("techguns.gun.tooltip.damage") + ": " + this.damage).withStyle(ChatFormatting.GRAY));
			tooltip.add(Component.literal(TextUtil.trans("techguns.gun.tooltip.radius") + ": " + this.radius).withStyle(ChatFormatting.GRAY));
			tooltip.add(Component.literal(TextUtil.trans("techguns.gun.tooltip.bounces") + ": " + this.maxBounces).withStyle(ChatFormatting.GRAY));
		} else {
			tooltip.add(Component.literal(TextUtil.trans("techguns.gun.tooltip.damage") + ": " + this.damage).withStyle(ChatFormatting.GRAY));
			tooltip.add(Component.literal(TextUtil.trans("techguns.gun.tooltip.shift1") + " " + ChatFormatting.GREEN + TextUtil.trans("techguns.gun.tooltip.shift2") + " " + ChatFormatting.GRAY + TextUtil.trans("techguns.gun.tooltip.shift3")).withStyle(ChatFormatting.GRAY));
		}
	}
}
