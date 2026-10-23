package techguns.entities.npcs;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGArmors;
import techguns.TGuns;
import techguns.Techguns;
import techguns.items.armors.GenericArmorMultiCamo;

public class ZombiePoliceman extends GenericNPCUndead {

	public static final ResourceLocation LOOT = new ResourceLocation(Techguns.MODID, "entities/zombiepoliceman");

	public ZombiePoliceman(EntityType<? extends ZombiePoliceman> type, Level world) {
		super(type, world);
		setTGArmorStats(5.0f, 0f);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return createAttributes(0.25D, 25, 4, 50.0D);
	}

	@Override
	protected void addRandomArmor(int difficulty) {

		int camo = 5;
		// Armors
		double chance = 0.5;
		if (Math.random() <= chance)
			this.setItemSlot(EquipmentSlot.HEAD, GenericArmorMultiCamo.getNewWithCamo(TGArmors.t2_combat_Helmet, camo));
		if (Math.random() <= chance)
			this.setItemSlot(EquipmentSlot.CHEST, GenericArmorMultiCamo.getNewWithCamo(TGArmors.t2_combat_Chestplate, camo));
		if (Math.random() <= chance)
			this.setItemSlot(EquipmentSlot.LEGS, GenericArmorMultiCamo.getNewWithCamo(TGArmors.t2_combat_Leggings, camo));
		if (Math.random() <= chance)
			this.setItemSlot(EquipmentSlot.FEET, GenericArmorMultiCamo.getNewWithCamo(TGArmors.t2_combat_Boots, camo));

		// Weapons
		Random r = new Random();
		Item weapon = null;
		switch (r.nextInt(2)) {
		case 0:
			weapon = TGuns.revolver;
			break;
		case 1:
			weapon = TGuns.pistol;
			break;
		default:
			weapon = TGuns.revolver;
			break;
		}
		if (weapon != null) this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(weapon));
	}

	@Override
	public SoundEvent getAmbientSound() {
		return SoundEvents.ZOMBIE_AMBIENT;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ZOMBIE_HURT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.ZOMBIE_DEATH;
	}

	public SoundEvent getStepSound() {
		return SoundEvents.ZOMBIE_STEP;
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
