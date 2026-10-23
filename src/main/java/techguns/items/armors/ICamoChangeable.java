package techguns.items.armors;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface ICamoChangeable {

	public int getCamoCount();

	public default int switchCamo(ItemStack item) {
		return this.switchCamo(item, false);
	}

	public default int switchCamo(ItemStack item, boolean back) {
		CompoundTag tags = item.getOrCreateTag();

		byte camoID = 0;
		if (tags.contains("camo")) {
			camoID = tags.getByte("camo");
		}

		ICamoChangeable it = (ICamoChangeable) item.getItem();
		if (back) {
			camoID--;
		} else {
			camoID++;
		}
		if (camoID >= it.getCamoCount()) {
			camoID = 0;
		} else if (camoID < 0) {
			camoID = (byte) (it.getCamoCount() - 1);
		}

		tags.putByte("camo", camoID);
		return camoID;
	}

	public default int getCurrentCamoIndex(ItemStack item) {
		CompoundTag tags = item.getTag();
		byte camoID = 0;
		if (tags != null && tags.contains("camo")) {
			camoID = tags.getByte("camo");
		}
		return camoID;
	}

	public Component getCurrentCamoName(ItemStack item);
}
