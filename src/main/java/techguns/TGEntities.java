package techguns;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegisterEvent;
import techguns.entities.projectiles.AdvancedBulletProjectile;
import techguns.entities.projectiles.AlienBlasterProjectile;
import techguns.entities.projectiles.BioGunProjectile;
import techguns.entities.projectiles.BlasterProjectile;
import techguns.entities.projectiles.ChainsawProjectile;
import techguns.entities.projectiles.CyberdemonBlasterProjectile;
import techguns.entities.projectiles.DeatomizerProjectile;
import techguns.entities.projectiles.FlamethrowerProjectile;
import techguns.entities.projectiles.FragGrenadeProjectile;
import techguns.entities.projectiles.GaussProjectile;
import techguns.entities.projectiles.GenericProjectile;
import techguns.entities.projectiles.GenericProjectileExplosive;
import techguns.entities.projectiles.GenericProjectileIncendiary;
import techguns.entities.projectiles.Grenade40mmProjectile;
import techguns.entities.projectiles.GrenadeProjectile;
import techguns.entities.projectiles.GuidedMissileProjectile;
import techguns.entities.projectiles.GuidedMissileProjectileHV;
import techguns.entities.projectiles.LaserProjectile;
import techguns.entities.projectiles.NDRProjectile;
import techguns.entities.projectiles.PowerHammerProjectile;
import techguns.entities.projectiles.RocketProjectile;
import techguns.entities.projectiles.RocketProjectileHV;
import techguns.entities.projectiles.RocketProjectileNuke;
import techguns.entities.projectiles.SonicShotgunProjectile;
import techguns.entities.projectiles.StoneBulletProjectile;
import techguns.entities.projectiles.TFGProjectile;
import techguns.entities.projectiles.TeslaProjectile;

/**
 * Entity types of the port. 1.12 registry names were CamelCase ("GenericProjectile"), 1.20 requires lowercase.
 */
public class TGEntities {

	public static final int bulletTrackRange = 128;

	public static EntityType<GenericProjectile> GENERIC_PROJECTILE;
	public static EntityType<GenericProjectileIncendiary> GENERIC_PROJECTILE_INCENDIARY;
	public static EntityType<RocketProjectile> ROCKET_PROJECTILE;
	public static EntityType<StoneBulletProjectile> STONE_BULLET_PROJECTILE;
	public static EntityType<BioGunProjectile> BIOGUN_PROJECTILE;
	public static EntityType<FlamethrowerProjectile> FLAMETHROWER_PROJECTILE;
	public static EntityType<GrenadeProjectile> GRENADE_PROJECTILE;
	public static EntityType<Grenade40mmProjectile> GRENADE_40MM_PROJECTILE;
	public static EntityType<LaserProjectile> LASER_PROJECTILE;
	public static EntityType<TeslaProjectile> TESLA_PROJECTILE;
	public static EntityType<NDRProjectile> NDR_PROJECTILE;
	public static EntityType<BlasterProjectile> BLASTER_PROJECTILE;
	public static EntityType<GaussProjectile> GAUSS_PROJECTILE;
	public static EntityType<AdvancedBulletProjectile> ADVANCED_BULLET_PROJECTILE;
	public static EntityType<GuidedMissileProjectile> GUIDED_MISSILE_PROJECTILE;
	public static EntityType<DeatomizerProjectile> DEATOMIZER_PROJECTILE;
	public static EntityType<AlienBlasterProjectile> ALIEN_BLASTER_PROJECTILE;
	public static EntityType<SonicShotgunProjectile> SONIC_SHOTGUN_PROJECTILE;
	public static EntityType<CyberdemonBlasterProjectile> CYBERDEMON_BLASTER_PROJECTILE;
	public static EntityType<PowerHammerProjectile> POWERHAMMER_PROJECTILE;
	public static EntityType<ChainsawProjectile> CHAINSAW_PROJECTILE;
	public static EntityType<FragGrenadeProjectile> FRAG_GRENADE_PROJECTILE;
	public static EntityType<RocketProjectileNuke> ROCKET_PROJECTILE_NUKE;
	public static EntityType<TFGProjectile> TFG_PROJECTILE;
	public static EntityType<GenericProjectileExplosive> GENERIC_PROJECTILE_EXPLOSIVE;
	public static EntityType<RocketProjectileHV> ROCKET_PROJECTILE_HV;
	public static EntityType<GuidedMissileProjectileHV> GUIDED_MISSILE_PROJECTILE_HV;

	public static void registerEntities(RegisterEvent event) {
		event.register(Registries.ENTITY_TYPE, helper -> {
			GENERIC_PROJECTILE = register(helper, "genericprojectile", EntityType.Builder.<GenericProjectile>of(GenericProjectile::new, MobCategory.MISC));
			techguns.entities.projectiles.FlyingGibs.TYPE = register(helper, "flyinggibs", EntityType.Builder.<techguns.entities.projectiles.FlyingGibs>of(techguns.entities.projectiles.FlyingGibs::new, MobCategory.MISC).noSave().noSummon());
			GENERIC_PROJECTILE_INCENDIARY = register(helper, "genericprojectileincendiary", EntityType.Builder.<GenericProjectileIncendiary>of(GenericProjectileIncendiary::new, MobCategory.MISC));
			ROCKET_PROJECTILE = register(helper, "rocketprojectile", EntityType.Builder.<RocketProjectile>of(RocketProjectile::new, MobCategory.MISC));
			STONE_BULLET_PROJECTILE = register(helper, "stonebulletprojectile", EntityType.Builder.<StoneBulletProjectile>of(StoneBulletProjectile::new, MobCategory.MISC));
			BIOGUN_PROJECTILE = register(helper, "biogunprojectile", EntityType.Builder.<BioGunProjectile>of(BioGunProjectile::new, MobCategory.MISC));
			FLAMETHROWER_PROJECTILE = register(helper, "flamethrowerprojectile", EntityType.Builder.<FlamethrowerProjectile>of(FlamethrowerProjectile::new, MobCategory.MISC));
			GRENADE_PROJECTILE = register(helper, "grenadeprojectile", EntityType.Builder.<GrenadeProjectile>of(GrenadeProjectile::new, MobCategory.MISC));
			GRENADE_40MM_PROJECTILE = register(helper, "grenade40mmprojectile", EntityType.Builder.<Grenade40mmProjectile>of(Grenade40mmProjectile::new, MobCategory.MISC));
			LASER_PROJECTILE = register(helper, "laserprojectile", EntityType.Builder.<LaserProjectile>of(LaserProjectile::new, MobCategory.MISC));
			TESLA_PROJECTILE = register(helper, "teslaprojectile", EntityType.Builder.<TeslaProjectile>of(TeslaProjectile::new, MobCategory.MISC));
			NDR_PROJECTILE = register(helper, "ndrprojectile", EntityType.Builder.<NDRProjectile>of(NDRProjectile::new, MobCategory.MISC));
			BLASTER_PROJECTILE = register(helper, "blasterprojectile", EntityType.Builder.<BlasterProjectile>of(BlasterProjectile::new, MobCategory.MISC));
			GAUSS_PROJECTILE = register(helper, "gaussprojectile", EntityType.Builder.<GaussProjectile>of(GaussProjectile::new, MobCategory.MISC));
			ADVANCED_BULLET_PROJECTILE = register(helper, "advancedbulletprojectile", EntityType.Builder.<AdvancedBulletProjectile>of(AdvancedBulletProjectile::new, MobCategory.MISC));
			GUIDED_MISSILE_PROJECTILE = register(helper, "guidedmissileprojectile", EntityType.Builder.<GuidedMissileProjectile>of(GuidedMissileProjectile::new, MobCategory.MISC));
			DEATOMIZER_PROJECTILE = register(helper, "deatomizerprojectile", EntityType.Builder.<DeatomizerProjectile>of(DeatomizerProjectile::new, MobCategory.MISC));
			ALIEN_BLASTER_PROJECTILE = register(helper, "alienblasterprojectile", EntityType.Builder.<AlienBlasterProjectile>of(AlienBlasterProjectile::new, MobCategory.MISC));
			SONIC_SHOTGUN_PROJECTILE = register(helper, "sonicshotgunprojectile", EntityType.Builder.<SonicShotgunProjectile>of(SonicShotgunProjectile::new, MobCategory.MISC));
			CYBERDEMON_BLASTER_PROJECTILE = register(helper, "cyberdemonblasterprojectile", EntityType.Builder.<CyberdemonBlasterProjectile>of(CyberdemonBlasterProjectile::new, MobCategory.MISC));
			POWERHAMMER_PROJECTILE = register(helper, "powerhammerprojectile", EntityType.Builder.<PowerHammerProjectile>of(PowerHammerProjectile::new, MobCategory.MISC));
			CHAINSAW_PROJECTILE = register(helper, "chainsawprojectile", EntityType.Builder.<ChainsawProjectile>of(ChainsawProjectile::new, MobCategory.MISC));
			FRAG_GRENADE_PROJECTILE = register(helper, "fraggrenadeprojectile", EntityType.Builder.<FragGrenadeProjectile>of(FragGrenadeProjectile::new, MobCategory.MISC));
			ROCKET_PROJECTILE_NUKE = register(helper, "rocketprojectilenuke", EntityType.Builder.<RocketProjectileNuke>of(RocketProjectileNuke::new, MobCategory.MISC));
			TFG_PROJECTILE = register(helper, "tfgprojectile", EntityType.Builder.<TFGProjectile>of(TFGProjectile::new, MobCategory.MISC));
			GENERIC_PROJECTILE_EXPLOSIVE = register(helper, "genericprojectileexplosive", EntityType.Builder.<GenericProjectileExplosive>of(GenericProjectileExplosive::new, MobCategory.MISC));
			ROCKET_PROJECTILE_HV = register(helper, "rocketprojectilehv", EntityType.Builder.<RocketProjectileHV>of(RocketProjectileHV::new, MobCategory.MISC));
			GUIDED_MISSILE_PROJECTILE_HV = register(helper, "guidedmissileprojectilehv", EntityType.Builder.<GuidedMissileProjectileHV>of(GuidedMissileProjectileHV::new, MobCategory.MISC));
		});
	}

	/**
	 * Projectile settings of 1.12: tracking range 128, update frequency 1, velocity updates on, size 0.25
	 */
	public static <T extends Entity> EntityType<T> register(RegisterEvent.RegisterHelper<EntityType<?>> helper, String name, EntityType.Builder<T> builder) {
		EntityType<T> type = builder.sized(0.25F, 0.25F).clientTrackingRange(bulletTrackRange / 16).updateInterval(1).setShouldReceiveVelocityUpdates(true).build(Techguns.MODID + ":" + name);
		helper.register(new ResourceLocation(Techguns.MODID, name), type);
		return type;
	}
}
