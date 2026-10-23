package techguns.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import techguns.Techguns;

/**
 * Item of a GenericBlockMetaEnum, the name depends on the variant: block.techguns.<name>.<variant>
 */
public class GenericItemBlockMeta<T extends Enum<T> & StringRepresentable> extends GenericItemBlock {

	protected final GenericBlockMetaEnum<T> metablock;

	public GenericItemBlockMeta(GenericBlockMetaEnum<T> block, String name) {
		super(block, name);
		this.metablock = block;
	}

	public GenericBlockMetaEnum<T> getMetaBlock() {
		return metablock;
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		return this.getDescriptionId() + "." + metablock.getTypeFromStack(stack).getSerializedName();
	}

	/**
	 * All variants, added to the creative tab by TGBlocks
	 */
	public List<ItemStack> getVariantStacks() {
		return metablock.getAllVariantStacks();
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);
		CompoundTag tags = stack.getTag();
		if (tags != null && tags.contains(BLOCK_ENTITY_TAG)) {
			tooltip.add(Component.translatable(Techguns.MODID + ".block.hasTileEntityData"));
		}
	}
}
