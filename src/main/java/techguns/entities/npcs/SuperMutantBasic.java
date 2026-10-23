package techguns.entities.npcs;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGSounds;
import techguns.TGuns;
import techguns.Techguns;
import techguns.damagesystem.TGDamageSource;

/**
 * 1.12 setSize(getMutantWidth(), 2*getModelScale()): EntityType size (see TGNpcEntities)
 */
public class SuperMutantBasic extends GenericNPC {

	public static final ResourceLocation LOOT = new ResourceLocation(Techguns.MODID, "entities/supermutantbasic");

	public SuperMutantBasic(EntityType<? extends SuperMutantBasic> type, Level world) {
		super(type, world);
		setTGArmorStats(5.0f, 0f);
	}

	public int gettype() {
		return 0;
	}

	public double getModelHeightOffset() {
		return 0.55d;
	}

	public float getModelScale() {
		return 1.35f;
	}

	@Override
	public float getWeaponPosY() {
		return 0f;
	}

	@Override
	public float getWeaponPosX() {
		return 0.13f;
	}

	@Override
	public float getWeaponPosZ() {
		return -0.18f;
	}

	@Override
	public float getTotalArmorAgainstType(TGDamageSource dmgsrc) {
		switch (dmgsrc.damageType) {
		case EXPLOSION:
		case LIGHTNING:
		case ENERGY:
		case FIRE:
		case ICE:
			return 10.0f;
		case PHYSICAL:
		case PROJECTILE:
			return 7.0f;
		case POISON:
		case RADIATION:
			return 15.0f;
		case UNRESISTABLE:
		default:
			return 0.0f;
		}
	}

	@Override
	public int getArmorValue() {
		return 7;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return createAttributes(0.30D, 35, 7, 50.0D).add(Attributes.ARMOR_TOUGHNESS, 1D);
	}

	@Override
	protected void addRandomArmor(int difficulty) {

		// Weapons
		Random r = new Random();
		Item weapon = null;
		switch (r.nextInt(5)) {
		case 0:
			weapon = TGuns.rocketlauncher;
			break;
		case 1:
			weapon = TGuns.ak47;
			break;
		case 2:
			weapon = TGuns.combatshotgun;
			break;
		default:
			weapon = TGuns.lasergun;
			break;
		}
		if (weapon != null) this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(weapon));
	}

	@Override
	public SoundEvent getAmbientSound() {
		return TGSounds.CYBERDEMON_IDLE;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return TGSounds.CYBERDEMON_HURT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return TGSounds.CYBERDEMON_DEATH;
	}

	public SoundEvent getStepSound() {
		return TGSounds.CYBERDEMON_STEP;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(this.getStepSound(), 0.15F, 1.0F);
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return LOOT;
	}
}
