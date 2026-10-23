package techguns.items.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import techguns.TGItems;
import techguns.Techguns;
import techguns.api.damagesystem.DamageType;
import techguns.api.damagesystem.IToolTGDamageSystem;
import techguns.damagesystem.TGDamageSource;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.util.TextUtil;

public class TGPickaxe extends PickaxeItem implements IToolTGDamageSystem, TGItems.ITGRegistryNamed {

	protected DamageType dmgType = DamageType.PHYSICAL;
	protected float penetration = 0.0f;

	protected final String name;

	/**
	 * Same attack damage (1 + material damage) and speed (-2.8) as the 1.12 ItemPickaxe
	 */
	public TGPickaxe(Tier mat, String name) {
		super(mat, 1, -2.8f, new Properties());
		this.name = name;
		TGItems.ITEMLIST.add(this);
	}

	public TGPickaxe(Tier mat, String name, DamageType dmgType, float penetration) {
		this(mat, name);
		this.dmgType = dmgType;
		this.penetration = penetration;
	}

	@Override
	public ResourceLocation getTGRegistryName() {
		return new ResourceLocation(Techguns.MODID, name);
	}

	@Override
	public TGDamageSource getDamageSource(DamageSource original) {
		TGDamageSource src = new TGDamageSource(original.typeHolder(), original.getDirectEntity(), original.getEntity(), this.dmgType, DeathType.GORE);
		src.ignoreHurtresistTime = false;
		src.goreChance = 0.25f;
		return src;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(Component.literal(TextUtil.trans("techguns.gun.tooltip.damageType") + ": ").withStyle(ChatFormatting.GRAY).append(this.dmgType.getDisplayComponent()));
		if (this.penetration > 0.0f) {
			tooltip.add(Component.literal(TextUtil.trans("techguns.gun.tooltip.armorPen") + ": " + this.penetration));
		}
	}

}
