package techguns.api.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IGenericGun {

	public boolean isShootWithLeftClick();

	public boolean isSemiAuto();

	/**
	 * Client only
	 */
	public boolean isZooming();

	public void shootGunPrimary(ItemStack stack, Level world, Player player, boolean zooming, InteractionHand hand, Entity target);

	public int getAmmoLeft(ItemStack stack);

	public GunHandType getGunHandType();

	public boolean isHoldZoom();

	public float getZoomMult();

	public default boolean canCharge() {
		return false;
	}

	public ResourceLocation getCurrentTexture(ItemStack stack);
}
