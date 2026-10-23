package techguns.entities.npcs;

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

/**
 * Fire immunity: EntityType builder (fireImmune)
 */
public class CyberDemon extends GenericNPCUndead {

	public static final ResourceLocation LOOT = new ResourceLocation(Techguns.MODID, "entities/cyberdemon");

	public CyberDemon(EntityType<? extends CyberDemon> type, Level world) {
		super(type, world);
		setTGArmorStats(10.0f, 0f);
		this.hasAimedBowAnim = false;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return createAttributes(0.35D, 30, 7, 50.0D).add(Attributes.ARMOR_TOUGHNESS, 1D);
	}

	@Override
	protected void addRandomArmor(int difficulty) {

		// No Armor
		// Weapons
		Item weapon = null;

		weapon = TGuns.netherblaster;

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
	public boolean hasWeaponArmPose() {
		return false;
	}

	@Override
	public float getWeaponPosX() {
		return 0.16f;
	}

	@Override
	public float getWeaponPosY() {
		return 0.72f;
	}

	@Override
	public float getWeaponPosZ() {
		return 0.1f;
	}

	@Override
	public float getGunScale() {
		return 1.5f;
	}

	@Override
	public float getBulletOffsetSide() {
		return 0.3f;
	}

	@Override
	public float getBulletOffsetHeight() {
		return -0.59f;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(this.getStepSound(), 0.15F, 1.0F);
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return LOOT;
	}

	@Override
	protected boolean shouldBurnInDay() {
		return false;
	}

}
