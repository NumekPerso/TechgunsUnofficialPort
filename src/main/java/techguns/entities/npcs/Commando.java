package techguns.entities.npcs;

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

public class Commando extends GenericNPC {

	public static final ResourceLocation LOOT = new ResourceLocation(Techguns.MODID, "entities/commando");

	public Commando(EntityType<? extends Commando> type, Level world) {
		super(type, world);
		setTGArmorStats(8.0f, 0f);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return createAttributes(0.35D, 30, 5, 75.0D);
	}

	@Override
	protected void addRandomArmor(int difficulty) {

		// Armors
		this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(TGArmors.t2_commando_Helmet));
		this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(TGArmors.t2_commando_Chestplate));
		this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(TGArmors.t2_commando_Leggings));
		this.setItemSlot(EquipmentSlot.FEET, new ItemStack(TGArmors.t2_commando_Boots));

		// Weapons
		Item weapon = TGuns.m4_infiltrator;

		if (weapon != null) this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(weapon));
	}

	@Override
	public SoundEvent getAmbientSound() {
		return SoundEvents.VILLAGER_AMBIENT;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.VILLAGER_HURT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.VILLAGER_DEATH;
	}

	public SoundEvent getStepSound() {
		return SoundEvents.ZOMBIE_VILLAGER_STEP;
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
