package techguns.items.guns.ammo;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import techguns.util.ItemUtil;

public class AmmoType {

	protected Item[] emptyMagItems = { null };
	protected ItemStack[] emptyMag = null;

	protected ArrayList<AmmoVariant> variants = new ArrayList<>();
	protected HashMap<String, Integer> ammoVariantIDs = new HashMap<>();

	protected int bulletsPerMag = 0;

	public AmmoType(Item... ammo) {
		this.variants.add(new AmmoVariant(ammo, ammo));
		ammoVariantIDs.put(AmmoTypes.TYPE_DEFAULT, 0);
	}

	public AmmoType addVariant(String key, Item ammo, Item bullet) {
		return this.addVariant(key, new Item[] { ammo }, new Item[] { bullet });
	}

	public AmmoType addVariant(String key, Item... ammo) {
		return this.addVariant(key, ammo, ammo);
	}

	public int getIDforVariantKey(String key) {
		if (ammoVariantIDs.containsKey(key)) {
			return ammoVariantIDs.get(key);
		} else {
			return 0;
		}
	}

	public AmmoType addVariant(String key, Item[] ammo, Item[] bullet) {
		this.variants.add(new AmmoVariant(key, ammo, bullet));
		ammoVariantIDs.put(key, this.variants.size() - 1);
		return this;
	}

	public ItemStack[] getAmmo(int variant) {
		return this.variants.get(variant).getAmmo();
	}

	public ItemStack[] getEmptyMag() {
		if (emptyMag == null) {
			emptyMag = toStacks(emptyMagItems);
		}
		return emptyMag;
	}

	public ItemStack[] getBullet(int variant) {
		return this.variants.get(variant).getBullet();
	}

	/**
	 * null entries mean no item (ItemStack.EMPTY)
	 */
	public AmmoType(Item ammo, Item emptyMag, Item bullet, int bulletsPerMag) {
		this(new Item[] { ammo }, new Item[] { emptyMag }, new Item[] { bullet }, bulletsPerMag);
	}

	public AmmoType(Item[] ammo, Item[] emptyMag, Item[] bullet, int bulletsPerMag) {
		this.variants.add(new AmmoVariant(ammo, bullet));
		ammoVariantIDs.put(AmmoTypes.TYPE_DEFAULT, 0);
		this.emptyMagItems = emptyMag;
		this.bulletsPerMag = bulletsPerMag;
	}

	public String getAmmoVariantKeyfor(ItemStack stack, int index) {
		for (int i = 0; i < this.variants.size(); i++) {
			if (ItemUtil.isItemEqual(variants.get(i).getAmmo()[index], stack)) {
				return variants.get(i).key;
			}
		}
		return AmmoTypes.TYPE_DEFAULT;
	}

	public ArrayList<AmmoVariant> getVariants() {
		return variants;
	}

	public boolean hasMultipleVariants() {
		return this.variants.size() > 1;
	}

	public float getShotsPerBullet(int clipsize, int ammoCount) {
		if (ammoCount == 1 && bulletsPerMag == 0) {
			return clipsize;
		}
		float bpm = bulletsPerMag == 0 ? (float) clipsize / (float) ammoCount : bulletsPerMag;
		return ((float) clipsize) / bpm;
	}

	public int getBulletsPerMag() {
		return bulletsPerMag;
	}

	public static ItemStack[] toStacks(Item[] items) {
		ItemStack[] stacks = new ItemStack[items.length];
		for (int i = 0; i < items.length; i++) {
			stacks[i] = items[i] == null ? ItemStack.EMPTY : new ItemStack(items[i]);
		}
		return stacks;
	}
}
