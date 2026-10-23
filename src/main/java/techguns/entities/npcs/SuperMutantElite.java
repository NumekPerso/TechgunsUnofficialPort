package techguns.entities.npcs;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import techguns.damagesystem.TGDamageSource;

public class SuperMutantElite extends SuperMutantBasic {

	public SuperMutantElite(EntityType<? extends SuperMutantElite> type, Level world) {
		super(type, world);
		setTGArmorStats(15.0f, 1f);
	}

	@Override
	public int gettype() {
		return 2;
	}

	@Override
	public double getModelHeightOffset() {
		return 0.95d;
	}

	@Override
	public float getModelScale() {
		return 1.65f;
	}

	@Override
	public float getWeaponPosX() {
		return 0.23f;
	}

	@Override
	public float getWeaponPosZ() {
		return -0.39f;
	}

	@Override
	public float getTotalArmorAgainstType(TGDamageSource dmgsrc) {
		switch (dmgsrc.damageType) {
		case EXPLOSION:
		case LIGHTNING:
		case ENERGY:
		case FIRE:
		case ICE:
			return 14.0f;
		case PHYSICAL:
		case PROJECTILE:
			return 10.0f;
		case POISON:
		case RADIATION:
			return 16.0f;
		case UNRESISTABLE:
		default:
			return 0.0f;
		}
	}

	@Override
	public int getArmorValue() {
		return 10;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return createAttributes(0.20D, 60, 11, 50.0D).add(Attributes.ARMOR_TOUGHNESS, 2.5D);
	}
}
