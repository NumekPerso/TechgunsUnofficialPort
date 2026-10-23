package techguns.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/**
 * Block with an enum "type" property. In 1.12 the type was stored in the metadata and the item damage, in the port the
 * type is a block state property and the item stack stores it in the vanilla "BlockStateTag" (applied when placing).
 * A stack without BlockStateTag is the first (default) variant, stacks of other variants are created with getStackFor().
 * <p>
 * Only used for the blocks whose variants are decorations (camo changeable), material variants are separate blocks.
 */
public class GenericBlockMetaEnum<T extends Enum<T> & StringRepresentable> extends Block implements IGenericBlock {

	/**
	 * createBlockStateDefinition is called by the Block constructor, before TYPE is set
	 */
	private static final ThreadLocal<EnumProperty<?>> PROPERTY_INIT = new ThreadLocal<>();

	protected static Properties withTypeProperty(Properties props, EnumProperty<?> type) {
		PROPERTY_INIT.set(type);
		return props;
	}

	public final EnumProperty<T> TYPE;
	protected final Class<T> clazz;

	public GenericBlockMetaEnum(Properties props, EnumProperty<T> type) {
		super(withTypeProperty(props, type));
		PROPERTY_INIT.remove();
		this.TYPE = type;
		this.clazz = type.getValueClass();
		this.registerDefaultState(this.stateDefinition.any().setValue(TYPE, clazz.getEnumConstants()[0]));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(PROPERTY_INIT.get());
	}

	public Class<T> getClazz() {
		return clazz;
	}

	public BlockState getStateFor(T type) {
		return this.defaultBlockState().setValue(TYPE, type);
	}

	/**
	 * Stack of the given variant, the default variant has no BlockStateTag
	 */
	public ItemStack getStackFor(T type, int count) {
		ItemStack stack = new ItemStack(this, count);
		setTypeForStack(stack, type);
		return stack;
	}

	public ItemStack getStackFor(T type) {
		return getStackFor(type, 1);
	}

	public List<ItemStack> getAllVariantStacks() {
		List<ItemStack> list = new ArrayList<>();
		for (T t : clazz.getEnumConstants()) {
			list.add(getStackFor(t));
		}
		return list;
	}

	public void setTypeForStack(ItemStack stack, T type) {
		CompoundTag tags = stack.getTag();
		if (type.ordinal() == 0) {
			if (tags != null && tags.contains(BlockItem.BLOCK_STATE_TAG)) {
				CompoundTag bst = tags.getCompound(BlockItem.BLOCK_STATE_TAG);
				bst.remove(TYPE.getName());
				if (bst.isEmpty()) {
					tags.remove(BlockItem.BLOCK_STATE_TAG);
				}
				if (tags.isEmpty()) {
					stack.setTag(null);
				}
			}
		} else {
			CompoundTag bst = stack.getOrCreateTagElement(BlockItem.BLOCK_STATE_TAG);
			bst.putString(TYPE.getName(), type.getSerializedName());
		}
	}

	public T getTypeFromStack(ItemStack stack) {
		CompoundTag tags = stack.getTag();
		if (tags != null && tags.contains(BlockItem.BLOCK_STATE_TAG)) {
			CompoundTag bst = tags.getCompound(BlockItem.BLOCK_STATE_TAG);
			if (bst.contains(TYPE.getName())) {
				return TYPE.getValue(bst.getString(TYPE.getName())).orElse(clazz.getEnumConstants()[0]);
			}
		}
		return clazz.getEnumConstants()[0];
	}

	/**
	 * 1.12 getPickBlock / damageDropped: the stack of the variant
	 */
	@Override
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
		return getStackFor(state.getValue(TYPE));
	}

	@Override
	public BlockItem createItemBlock(String name) {
		return new GenericItemBlockMeta<>(this, name);
	}
}
