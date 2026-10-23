package techguns.items.guns.ammo;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AmmoVariant {
	protected String key;
	protected Item[] ammoItems;
	protected Item[] bulletItems;

	protected ItemStack[] ammo;
	protected ItemStack[] bullet;

	public AmmoVariant(Item[] ammo, Item[] bullet) {
		this(AmmoTypes.TYPE_DEFAULT, ammo, bullet);
	}

	public AmmoVariant(String key, Item[] ammo, Item[] bullet) {
		this.key = key;
		this.ammoItems = ammo;
		this.bulletItems = bullet;
	}

	public String getKey() {
		return key;
	}

	/**
	 * ItemStacks are created lazily, items are created before the registries are filled
	 */
	public ItemStack[] getAmmo() {
		if (ammo == null) {
			ammo = AmmoType.toStacks(ammoItems);
		}
		return ammo;
	}

	public ItemStack[] getBullet() {
		if (bullet == null) {
			bullet = AmmoType.toStacks(bulletItems);
		}
		return bullet;
	}
}
