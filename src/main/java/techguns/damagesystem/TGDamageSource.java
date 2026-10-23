package techguns.damagesystem;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import techguns.Techguns;
import techguns.api.damagesystem.DamageType;
import techguns.deatheffects.EntityDeathUtils.DeathType;

/**
 * Port of the 1.12 TGDamageSource. The vanilla damage type is looked up by name: "player" maps to minecraft:player_attack,
 * every other name to techguns:&lt;name&gt; (see data/techguns/damage_type). The mutable fields carry the Techguns specific
 * behaviour (armor penetration, knockback multiplier, hurt cooldown bypass, death effect type).
 */
public class TGDamageSource extends DamageSource {

	protected boolean attackSuccessful = false;

	public DamageType damageType;
	public DeathType deathType;
	public float goreChance = 0.5f;
	public boolean knockbackOnShieldBlock = true;

	/**
	 * Anti-Toughness
	 */
	public float armorPenetration = 0.0f;

	public boolean ignoreHurtresistTime = false;

	public float knockbackMultiplier = 1.0f;

	/**
	 * set to true when this damagesource was converted from another damage source, with new TGDamageSource(DamageSource src)
	 */
	public boolean wasConverted = false;

	/**
	 * used by DamageSystem to restore the hurt cooldown when ignoreHurtresistTime is set
	 */
	protected LivingEntity cooldownEntity = null;
	protected int savedInvulnerableTime = 0;
	protected float savedLastHurt = 0f;

	protected static ArrayList<String> unresistableTypes = new ArrayList<String>();
	static {
		unresistableTypes.add("inWall");
		unresistableTypes.add("drown");
		unresistableTypes.add("starve");
		unresistableTypes.add("fall");
		unresistableTypes.add("outOfWorld");
		unresistableTypes.add("outsideBorder");
		unresistableTypes.add("genericKill");
	}

	public void setAttackSuccessful() {
		this.attackSuccessful = true;
	}

	public boolean wasSuccessful() {
		return this.attackSuccessful;
	}

	public static TGDamageSource causeBulletDamage(Entity projectile, Entity shooter, DeathType deathType) {
		TGDamageSource src = new TGDamageSource("tg_bullet", projectile, shooter, DamageType.PROJECTILE, deathType);
		src.knockbackOnShieldBlock = false;
		src.ignoreHurtresistTime = true;
		return src;
	}

	public static TGDamageSource causeExplosionDamage(Entity projectile, Entity shooter, DeathType deathType) {
		return new TGDamageSource("tg_explosion", projectile, shooter, DamageType.EXPLOSION, deathType);
	}

	public static TGDamageSource causePoisonDamage(Entity projectile, Entity shooter, DeathType deathType) {
		TGDamageSource src = new TGDamageSource("tg_poison", projectile, shooter, DamageType.POISON, deathType);
		src.ignoreHurtresistTime = true;
		src.knockbackOnShieldBlock = false;
		return src;
	}

	public static TGDamageSource causeFireDamage(Entity projectile, Entity shooter, DeathType deathType) {
		TGDamageSource src = new TGDamageSource("tg_fire", projectile, shooter, DamageType.FIRE, deathType);
		src.ignoreHurtresistTime = true;
		src.knockbackOnShieldBlock = false;
		return src;
	}

	public static TGDamageSource getKnockbackDummyDmgSrc(Entity projectile, Entity shooter) {
		return new TGDamageSource("tg_knockback", projectile, shooter, DamageType.PHYSICAL, DeathType.DEFAULT);
	}

	public static TGDamageSource causeEnergyDamage(Entity projectile, Entity shooter, DeathType deathType) {
		TGDamageSource src = new TGDamageSource("tg_energy", projectile, shooter, DamageType.ENERGY, deathType);
		src.ignoreHurtresistTime = true;
		src.knockbackOnShieldBlock = false;
		return src;
	}

	public static TGDamageSource causeRadiationDamage(Entity projectile, Entity shooter, DeathType deathType) {
		TGDamageSource src = new TGDamageSource("tg_rad", projectile, shooter, DamageType.RADIATION, deathType);
		src.ignoreHurtresistTime = true;
		src.knockbackOnShieldBlock = false;
		return src;
	}

	public static TGDamageSource causeLethalRadPoisoningDamage(Entity projectile, Entity shooter, DeathType deathType) {
		TGDamageSource src = new TGDamageSource("tg_rad_poisoning", projectile, shooter, DamageType.UNRESISTABLE, deathType);
		src.ignoreHurtresistTime = true;
		src.knockbackOnShieldBlock = false;
		src.goreChance = 1.0f;
		return src;
	}

	public static TGDamageSource causeLightningDamage(Entity projectile, Entity shooter, DeathType deathType) {
		TGDamageSource src = new TGDamageSource("tg_lightning", projectile, shooter, DamageType.LIGHTNING, deathType);
		src.ignoreHurtresistTime = true;
		src.knockbackOnShieldBlock = false;
		return src;
	}

	public static TGDamageSource causeDarkDamage(Entity projectile, Entity shooter, DeathType deathType) {
		TGDamageSource src = new TGDamageSource("tg_dark", projectile, shooter, DamageType.DARK, deathType);
		src.ignoreHurtresistTime = true;
		src.knockbackOnShieldBlock = false;
		return src;
	}

	public static TGDamageSource getFromGenericDamageSource(DamageSource src) {
		if (src instanceof TGDamageSource) {
			return (TGDamageSource) src;
		}
		return new TGDamageSource(src);
	}

	public TGDamageSource setNoKnockback() {
		this.knockbackMultiplier = 0.0f;
		return this;
	}

	public TGDamageSource setKnockback(float mult) {
		this.knockbackMultiplier = mult;
		return this;
	}

	public boolean hasKnockback() {
		return this.knockbackMultiplier > 0.0f;
	}

	/**
	 * Gets the death message that is displayed when the player dies
	 */
	@Override
	public Component getLocalizedDeathMessage(LivingEntity entityLivingBaseIn) {
		Entity attacker = this.getEntity();
		Entity damageSourceEntity = this.getDirectEntity();
		Component itextcomponent;
		ItemStack itemstack = ItemStack.EMPTY;
		if (attacker == null && damageSourceEntity == null) {
			itextcomponent = entityLivingBaseIn.getDisplayName();
		} else {
			itextcomponent = attacker == null ? damageSourceEntity.getDisplayName() : attacker.getDisplayName();
			itemstack = attacker instanceof LivingEntity ? ((LivingEntity) attacker).getMainHandItem() : ItemStack.EMPTY;
		}
		String s = "death.attack." + this.getMsgId();
		String s1 = s + ".item";
		return !itemstack.isEmpty() && itemstack.hasCustomHoverName() ? Component.translatable(s1, entityLivingBaseIn.getDisplayName(), itextcomponent, itemstack.getDisplayName())
				: Component.translatable(s, entityLivingBaseIn.getDisplayName(), itextcomponent);
	}

	public static TGDamageSource copyWithNewEnt(TGDamageSource other, Entity damagingEntity, Entity attacker) {
		TGDamageSource newSrc = new TGDamageSource(other.typeHolder(), damagingEntity, attacker, other.damageType, other.deathType);
		newSrc.knockbackMultiplier = other.knockbackMultiplier;
		newSrc.armorPenetration = other.armorPenetration;
		newSrc.goreChance = other.goreChance;
		newSrc.ignoreHurtresistTime = other.ignoreHurtresistTime;
		return newSrc;
	}

	public TGDamageSource(String name, @Nullable Entity damagingEntity, @Nullable Entity attacker, DamageType damageType, DeathType deathType) {
		this(getDamageTypeHolder(name, damagingEntity, attacker), damagingEntity, attacker, damageType, deathType);
	}

	public TGDamageSource(Holder<net.minecraft.world.damagesource.DamageType> type, @Nullable Entity damagingEntity, @Nullable Entity attacker, DamageType damageType, DeathType deathType) {
		super(type, damagingEntity, attacker);
		this.damageType = damageType;
		this.deathType = deathType;
	}

	/**
	 * Converts a vanilla damage source
	 */
	public TGDamageSource(DamageSource dmg) {
		super(dmg.typeHolder(), dmg.getDirectEntity(), dmg.getEntity());
		this.damageType = determineTGDamageType(dmg);
		this.deathType = DeathType.DEFAULT;
		this.wasConverted = true;
		if (dmg.is(DamageTypeTags.IS_PROJECTILE)) {
			this.knockbackOnShieldBlock = false;
		}
	}

	public static DamageType determineTGDamageType(DamageSource dmg) {
		if (dmg instanceof TGDamageSource) {
			return ((TGDamageSource) dmg).damageType;
		}
		String id = dmg.getMsgId();
		if (dmg.is(DamageTypeTags.IS_EXPLOSION)) {
			return DamageType.EXPLOSION;
		} else if (dmg.is(DamageTypes.MAGIC) || dmg.is(DamageTypes.INDIRECT_MAGIC)) {
			return DamageType.ENERGY;
		} else if (dmg.is(DamageTypeTags.IS_FIRE) || dmg.is(DamageTypes.DRAGON_BREATH)) {
			return DamageType.FIRE;
		} else if (dmg.is(DamageTypeTags.IS_PROJECTILE)) {
			return DamageType.PROJECTILE;
		} else if (dmg.is(DamageTypes.WITHER)) {
			return DamageType.POISON;
		} else if (dmg.is(DamageTypeTags.IS_LIGHTNING)) {
			return DamageType.LIGHTNING;
		} else if (dmg.is(DamageTypeTags.IS_FREEZING)) {
			return DamageType.ICE;
		} else if (dmg.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || unresistableTypes.contains(id)) {
			return DamageType.UNRESISTABLE;
		} else {
			return DamageType.PHYSICAL;
		}
	}

	public boolean knockbackOnShieldBlock() {
		return this.knockbackOnShieldBlock;
	}

	public TGDamageSource setKnockbackOnShieldBlock(boolean knockbackOnShieldBlock) {
		this.knockbackOnShieldBlock = knockbackOnShieldBlock;
		return this;
	}

	public static ResourceKey<net.minecraft.world.damagesource.DamageType> getDamageTypeKey(String name) {
		if ("player".equals(name)) {
			return DamageTypes.PLAYER_ATTACK;
		}
		return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Techguns.MODID, name));
	}

	public static Holder<net.minecraft.world.damagesource.DamageType> getDamageTypeHolder(String name, @Nullable Entity e1, @Nullable Entity e2) {
		RegistryAccess access = null;
		if (e1 != null) {
			access = e1.level().registryAccess();
		} else if (e2 != null) {
			access = e2.level().registryAccess();
		} else if (ServerLifecycleHooks.getCurrentServer() != null) {
			access = ServerLifecycleHooks.getCurrentServer().registryAccess();
		} else {
			access = Techguns.proxy.getClientRegistryAccess();
		}
		return access.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(getDamageTypeKey(name));
	}

	public static Holder<net.minecraft.world.damagesource.DamageType> getDamageTypeHolder(String name, Level level) {
		return level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(getDamageTypeKey(name));
	}
}
