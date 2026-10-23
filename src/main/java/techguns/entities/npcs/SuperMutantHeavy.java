package techguns.entities.npcs;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import techguns.damagesystem.TGDamageSource;

public class SuperMutantHeavy extends SuperMutantBasic {

	public SuperMutantHeavy(EntityType<? extends SuperMutantHeavy> type, Level world) {
		super(type, world);
		setTGArmorStats(10.0f, 0);
	}

	@Override
	public int gettype() {
		return 1;
	}

	@Override
	public double getModelHeightOffset() {
		return 0.75d;
	}

	@Override
	public float getModelScale() {
		return 1.5f;
	}

	@Override
	public float getWeaponPosX() {
		return 0.17f;
	}

	@Override
	public float getWeaponPosZ() {
		return -0.3f;
	}

	@Override
	public float getTotalArmorAgainstType(TGDamageSource dmgsrc) {
		switch (dmgsrc.damageType) {
		case EXPLOSION:
		case LIGHTNING:
		case ENERGY:
		case FIRE:
		case ICE:
			return 16.0f;
		case PHYSICAL:
		case PROJECTILE:
			return 12.0f;
		case POISON:
		case RADIATION:
			return 18.0f;
		case UNRESISTABLE:
		default:
			return 0.0f;
		}
	}

	@Override
	public int getArmorValue() {
		return 12;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return createAttributes(0.25D, 45, 9, 50.0D).add(Attributes.ARMOR_TOUGHNESS, 2D);
	}
}
