package techguns.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import techguns.Techguns;

public class TGExtendedPlayerCapProvider implements ICapabilitySerializable<CompoundTag> {

	public static final ResourceLocation ID = new ResourceLocation(Techguns.MODID, "extendedplayer");

	private final TGExtendedPlayer instance;
	private final LazyOptional<TGExtendedPlayer> optional;

	public TGExtendedPlayerCapProvider(TGExtendedPlayer caps) {
		this.instance = caps;
		this.optional = LazyOptional.of(() -> this.instance);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return TGExtendedPlayer.TG_EXTENDED_PLAYER.orEmpty(cap, this.optional);
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tags = new CompoundTag();
		this.instance.saveToNBT(tags);
		return tags;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		this.instance.loadFromNBT(nbt);
	}

	public void invalidate() {
		this.optional.invalidate();
	}
}
