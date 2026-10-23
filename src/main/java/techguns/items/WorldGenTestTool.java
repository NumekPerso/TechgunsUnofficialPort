package techguns.items;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import techguns.world.StructureBlockUtils;
import techguns.world.dungeon.Dungeon;
import techguns.world.dungeon.presets.IDungeonPreset;
import techguns.world.structures.MilitaryCamp;
import techguns.world.structures.NetherDungeonEntrance;
import techguns.world.structures.WorldgenStructure.BiomeColorType;

/**
 * Debug tool (1.12 WorldGenTestTool): sneak + use switches the mode, click 2 corners to run the mode in the area.
 */
public class WorldGenTestTool extends GenericItem {

	static String[] modes = new String[] { "MilitaryBase", "Nether Dungeon", "Smooth", "Flatten", "Remove Junk" };

	public WorldGenTestTool(String name) {
		super(name, true, new Properties().stacksTo(1));
	}

	protected void switchMode(Level world, Player player, ItemStack item) {
		CompoundTag tag = item.getOrCreateTag();
		int mode = 1;
		if (tag.contains("mode")) {
			mode = (tag.getInt("mode") + 1) % modes.length;
		}
		tag.putInt("mode", mode);
		if (world.isClientSide) {
			player.displayClientMessage(Component.literal("WorldGen tool mode set to \"" + modes[mode] + "\"."), false);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack item = player.getItemInHand(hand);
		if (player.isShiftKeyDown()) {
			switchMode(world, player, item);
		}
		return super.use(world, player, hand);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Player player = ctx.getPlayer();
		Level world = ctx.getLevel();
		ItemStack item = ctx.getItemInHand();
		BlockPos pos = ctx.getClickedPos();
		if (player == null) {
			return InteractionResult.PASS;
		}
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		CompoundTag tag = item.getOrCreateTag();

		if (player.isShiftKeyDown()) {
			switchMode(world, player, item);
		} else if (tag.contains("x1") && tag.contains("y1") && tag.contains("z1")) {
			int x1 = tag.getInt("x1");
			int y1 = tag.getInt("y1");
			int z1 = tag.getInt("z1");
			int sizeX = Math.abs(x1 - x) + 1;
			int sizeY = Math.abs(y1 - y) + 1;
			int sizeZ = Math.abs(z1 - z) + 1;
			if (world.isClientSide) {
				player.displayClientMessage(Component.literal("Position2 set (" + x + "/" + y + "/" + z + "). Size: (" + sizeX + "/" + sizeY + "/" + sizeZ + ")."), false);
			} else {
				doOperation((ServerLevel) world, Math.min(x, x1), Math.min(y, y1), Math.min(z, z1), sizeX, sizeY, sizeZ, tag.getInt("mode"));
			}
			tag.remove("x1");
			tag.remove("y1");
			tag.remove("z1");
		} else {
			tag.putInt("x1", x);
			tag.putInt("y1", y);
			tag.putInt("z1", z);
			if (world.isClientSide) {
				player.displayClientMessage(Component.literal("Position1 set (" + x + "/" + y + "/" + z + ")."), false);
			}
		}
		return InteractionResult.SUCCESS;
	}

	private void doOperation(ServerLevel world, int x, int y, int z, int sizeX, int sizeY, int sizeZ, int mode) {
		Random rnd = new Random();
		switch (mode) {
		case 0: //structure
			MilitaryCamp camp = new MilitaryCamp(3, rnd);
			camp.init(x, y, z, sizeX, sizeZ);
			camp.setBlocks(world, rnd);
			break;
		case 1: //dungeon
			IDungeonPreset preset = IDungeonPreset.Presets.nether();
			Dungeon dungeon = new Dungeon(preset);
			int height = 24 + rnd.nextInt(25);
			int yOffset = preset.getSizeY() + (height % preset.getSizeY()) - height;
			dungeon.generate(world, x, y + yOffset, z, sizeX, height, sizeZ, rnd);

			NetherDungeonEntrance e = new NetherDungeonEntrance();
			int px = x + (dungeon.startPos.getX() * preset.getSizeXZ());
			int py = y + (dungeon.startPos.getY() * preset.getSizeY()) + yOffset;
			int pz = z + (dungeon.startPos.getZ() * preset.getSizeXZ());
			e.setBlocks(world, px + 1 - preset.getSizeXZ() / 2, py, pz + 1 - preset.getSizeXZ() / 2, e.sizeX, e.sizeY, e.sizeZ,
					StructureBlockUtils.facingToRotation(dungeon.startRotation), BiomeColorType.WOODLAND, rnd);
			break;
		case 2: //Smooth
			StructureBlockUtils.apply2DHeightmapFilter(world, x, z, sizeX, sizeZ, StructureBlockUtils.FILTER_GAUSSIAN_5x5);
			break;
		case 3: //Flatten
			StructureBlockUtils.flattenArea(world, x, z, sizeX, sizeZ, 2);
			break;
		case 4: //remove junk
			StructureBlockUtils.removeJunkInArea(world, x, z, sizeX, sizeZ);
			break;
		}
	}

}
