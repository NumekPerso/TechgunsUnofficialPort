package techguns.entities.npcs;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGArmors;
import techguns.TGuns;
import techguns.Techguns;

public class DictatorDave extends GenericNPC {

	public static final ResourceLocation LOOT = new ResourceLocation(Techguns.MODID, "entities/dictatordave");

	public DictatorDave(EntityType<? extends DictatorDave> type, Level world) {
		super(type, world);
		setTGArmorStats(15.0f, 0f);
		this.xpReward = 35;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return createAttributes(0.40D, 100, 7, 60.0D).add(Attributes.ARMOR_TOUGHNESS, 2D);
	}

	@Override
	protected void addRandomArmor(int difficulty) {

		// Armors
		this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(TGArmors.t2_beret));
		this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(TGArmors.t1_combat_Chestplate));
		this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(TGArmors.t1_scout_Leggings));
		this.setItemSlot(EquipmentSlot.FEET, new ItemStack(TGArmors.t1_combat_Boots));

		// Weapons
		Item weapon = TGuns.goldenrevolver;

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
