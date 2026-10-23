package techguns.tileentities.operation;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class MachineOperation {

	protected ArrayList<ItemStack> inputs = null;
	protected ArrayList<ItemStack> outputs = null;
	protected ArrayList<FluidStack> fluid_inputs = null;
	protected ArrayList<FluidStack> fluid_outputs = null;
	protected int stackMultiplier = 1;

	protected int time = 100;
	protected int powerPerTick = 0;

	public MachineOperation(ItemStack output, ItemStack... inputs) {
		this.outputs = new ArrayList<>();
		outputs.add(output);
		this.inputs = new ArrayList<>();
		Arrays.stream(inputs).forEach(i -> this.inputs.add(i));
	}

	public MachineOperation(ItemStack output, FluidStack fluidOut, ItemStack... inputs) {
		this.outputs = new ArrayList<>();
		outputs.add(output);
		this.inputs = new ArrayList<>();
		Arrays.stream(inputs).forEach(i -> this.inputs.add(i));
		this.fluid_outputs = new ArrayList<>();
		this.fluid_outputs.add(fluidOut);
	}

	public MachineOperation(FluidStack fluidOut, ItemStack... inputs) {
		this.inputs = new ArrayList<>();
		Arrays.stream(inputs).forEach(i -> this.inputs.add(i));
		this.fluid_outputs = new ArrayList<>();
		this.fluid_outputs.add(fluidOut);
	}

	public MachineOperation(ArrayList<ItemStack> inputs, ArrayList<ItemStack> outputs, ArrayList<FluidStack> fluid_inputs, ArrayList<FluidStack> fluid_outputs, int stackMultiplier) {
		super();
		this.inputs = inputs;
		this.outputs = outputs;
		this.fluid_outputs = fluid_outputs;
		this.fluid_inputs = fluid_inputs;
		this.stackMultiplier = stackMultiplier;
	}

	public MachineOperation setStackMultiplier(int mult) {
		this.stackMultiplier = mult;
		return this;
	}

	public int getStackMultiplier() {
		return stackMultiplier;
	}

	public int getPowerPerTick() {
		return powerPerTick * this.stackMultiplier;
	}

	public void setPowerPerTick(int powerPerTick) {
		this.powerPerTick = powerPerTick;
	}

	protected static ListTag writeItemList(ArrayList<ItemStack> list) {
		ListTag nbtTagList = new ListTag();
		for (int i = 0; i < list.size(); i++) {
			if (!list.get(i).isEmpty()) {
				CompoundTag itemTag = new CompoundTag();
				list.get(i).save(itemTag);
				nbtTagList.add(itemTag);
			}
		}
		return nbtTagList;
	}

	protected static ListTag writeFluidList(ArrayList<FluidStack> list) {
		ListTag nbtTagList = new ListTag();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) != null) {
				CompoundTag fluidTag = new CompoundTag();
				list.get(i).writeToNBT(fluidTag);
				nbtTagList.add(fluidTag);
			}
		}
		return nbtTagList;
	}

	public void writeToNBT(CompoundTag compound) {
		CompoundTag tags = new CompoundTag();

		tags.putByte("stackMultiplier", (byte) stackMultiplier);
		tags.putInt("powerPerTick", this.powerPerTick);

		if (inputs != null && inputs.size() > 0) {
			tags.put("InputItems", writeItemList(inputs));
			tags.putByte("InputSize", (byte) inputs.size());
		}
		if (outputs != null && outputs.size() > 0) {
			tags.put("OutputItems", writeItemList(outputs));
			tags.putByte("OutputSize", (byte) outputs.size());
		}

		if (fluid_inputs != null && fluid_inputs.size() > 0) {
			tags.put("FluidsIn", writeFluidList(fluid_inputs));
			tags.putByte("FluidSizeIn", (byte) fluid_inputs.size());
		}

		if (fluid_outputs != null && fluid_outputs.size() > 0) {
			tags.put("Fluids", writeFluidList(fluid_outputs));
			tags.putByte("FluidSize", (byte) fluid_outputs.size());
		}

		this.writeSubclassDataToNBT(tags);
		compound.put("operation", tags);
	}

	protected void writeSubclassDataToNBT(CompoundTag tags) {

	}

	public void readFromNBT(CompoundTag compound) {
		CompoundTag tags = compound.getCompound("operation");

		this.stackMultiplier = tags.getByte("stackMultiplier");
		this.powerPerTick = tags.getInt("powerPerTick");

		if (tags.contains("InputSize")) {
			if (this.inputs != null) {
				this.inputs.clear();
			} else {
				this.inputs = new ArrayList<>();
			}
			int size = tags.getByte("InputSize");

			ListTag tagList = tags.getList("InputItems", Tag.TAG_COMPOUND);
			for (int i = 0; i < size; i++) {
				CompoundTag itemTags = tagList.getCompound(i);
				this.inputs.add(ItemStack.of(itemTags));
			}

		}
		if (tags.contains("OutputSize")) {
			if (this.outputs != null) {
				this.outputs.clear();
			} else {
				this.outputs = new ArrayList<>();
			}
			int size = tags.getByte("OutputSize");

			ListTag tagList = tags.getList("OutputItems", Tag.TAG_COMPOUND);
			for (int i = 0; i < size; i++) {
				CompoundTag itemTags = tagList.getCompound(i);
				this.outputs.add(ItemStack.of(itemTags));
			}

		}

		if (tags.contains("FluidSizeIn")) {
			if (this.fluid_inputs != null) {
				this.fluid_inputs.clear();
			} else {
				this.fluid_inputs = new ArrayList<>();
			}
			int size = tags.getByte("FluidSizeIn");

			ListTag tagList = tags.getList("FluidsIn", Tag.TAG_COMPOUND);
			for (int i = 0; i < size; i++) {
				CompoundTag fluidTags = tagList.getCompound(i);
				this.fluid_inputs.add(FluidStack.loadFluidStackFromNBT(fluidTags));
			}

		}

		if (tags.contains("FluidSize")) {
			if (this.fluid_outputs != null) {
				this.fluid_outputs.clear();
			} else {
				this.fluid_outputs = new ArrayList<>();
			}
			int size = tags.getByte("FluidSize");

			ListTag tagList = tags.getList("Fluids", Tag.TAG_COMPOUND);
			for (int i = 0; i < size; i++) {
				CompoundTag fluidTags = tagList.getCompound(i);
				this.fluid_outputs.add(FluidStack.loadFluidStackFromNBT(fluidTags));
			}

		}
		this.readSubClassDataFromNBT(tags);
	}

	protected void readSubClassDataFromNBT(CompoundTag tags) {

	}

	/**
	 * get the item input i (without stack multiplier)
	 * @return
	 */
	public ItemStack getItemInputI(int i) {
		if (this.inputs != null && !this.inputs.isEmpty() && this.inputs.size() > i) {
			ItemStack in = this.inputs.get(i).copy();
			return in;
		} else {
			return ItemStack.EMPTY;
		}
	}

	/**
	 * get the output item i, without stack multiplier
	 * @return
	 */
	public ItemStack getItemOutputI(int i) {
		if (this.outputs != null && !this.outputs.isEmpty() && this.outputs.size() > i) {
			ItemStack out = this.outputs.get(i).copy();
			return out;
		} else {
			return ItemStack.EMPTY;
		}
	}

	/**
	 * get the first item output
	 * @return
	 */
	public ItemStack getItemOutput0() {
		if (this.outputs != null && !this.outputs.isEmpty()) {
			ItemStack out = this.outputs.get(0).copy();
			out.setCount(this.outputs.get(0).getCount() * this.stackMultiplier);
			return out;
		} else {
			return ItemStack.EMPTY;
		}
	}

	public FluidStack getFluidOutput0() {
		if (this.fluid_outputs != null && !this.fluid_outputs.isEmpty()) {
			FluidStack output = this.fluid_outputs.get(0);
			if (output != null) {
				FluidStack out = output.copy();
				out.setAmount(out.getAmount() * this.stackMultiplier);
				return out;
			}
		}
		return null;
	}

	public ArrayList<ItemStack> getInputs() {
		return inputs;
	}

	public ArrayList<ItemStack> getOutputs() {
		return outputs;
	}

	public ArrayList<ItemStack> getOutputsWithMult() {
		ArrayList<ItemStack> outputs = new ArrayList<>();
		this.outputs.forEach(o -> {
			ItemStack it = o.copy();
			it.setCount(o.getCount() * this.stackMultiplier);
			outputs.add(it);
		});
		return outputs;
	}

	public ArrayList<FluidStack> getFluid_inputs() {
		return fluid_inputs;
	}

	public ArrayList<FluidStack> getFluid_outputs() {
		return fluid_outputs;
	}

	public int getNeededAmountItem(int index) {
		if (this.inputs.size() <= index) {
			return 0;
		}
		ItemStack stack = this.inputs.get(index);
		return stack.getCount() * this.stackMultiplier;
	}

	public int getNeededAmountFluid(int index) {
		if (this.fluid_inputs.size() <= index) {
			return 0;
		}
		FluidStack fluid = this.getFluid_inputs().get(index);
		return fluid.getAmount() * this.stackMultiplier;
	}

	public int getTime() {
		return time;
	}

	public MachineOperation setTime(int time) {
		this.time = time;
		return this;
	}

}
