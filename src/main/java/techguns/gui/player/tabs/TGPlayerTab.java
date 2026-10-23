package techguns.gui.player.tabs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.event.ScreenEvent;
import techguns.TGItems;
import techguns.gui.player.TGGuiTabButton;

/**
 * Inventory tabs (1.12 used the Galacticraft tab API: InventoryTabVanilla + TGPlayerTab registered in the TabRegistry,
 * which added the tab buttons to GuiInventory in InitGuiEvent.Post). Tabs are drawn above the gui, the first one
 * opens the vanilla inventory, the second one the Techguns player inventory.
 */
public class TGPlayerTab {

	/**
	 * tabs added to the current vanilla inventory screen, repositioned when the recipe book moves the gui
	 */
	private static final List<TGGuiTabButton> inventoryScreenTabs = new ArrayList<>();
	private static Screen inventoryScreen = null;

	public static List<TGGuiTabButton> createTabs(int guiLeft, int guiTop, boolean techgunsTabSelected) {
		List<TGGuiTabButton> tabs = new ArrayList<>();
		tabs.add(new TGGuiTabButton(0, 0, 0, techgunsTabSelected, new ItemStack(Blocks.CRAFTING_TABLE), -1));
		tabs.add(new TGGuiTabButton(1, 0, 0, !techgunsTabSelected, TGItems.newStack(TGItems.PISTOL_ROUNDS, 1), 0));
		updateTabValues(tabs, guiLeft, guiTop);
		return tabs;
	}

	/**
	 * Galacticraft TabRegistry.updateTabValues
	 */
	public static void updateTabValues(List<TGGuiTabButton> tabs, int cornerX, int cornerY) {
		for (int i = 0; i < tabs.size(); i++) {
			TGGuiTabButton t = tabs.get(i);
			t.setX(cornerX + i * (TGGuiTabButton.TAB_WIDTH + 1));
			t.setY(cornerY - 28);
		}
	}

	public static void addTabs(int guiLeft, int guiTop, boolean techgunsTabSelected, Consumer<TGGuiTabButton> add) {
		createTabs(guiLeft, guiTop, techgunsTabSelected).forEach(add);
	}

	public static void onScreenInitPost(ScreenEvent.Init.Post event) {
		if (event.getScreen() instanceof InventoryScreen screen) {
			inventoryScreenTabs.clear();
			inventoryScreen = screen;
			List<TGGuiTabButton> tabs = createTabs(screen.getGuiLeft(), screen.getGuiTop(), false);
			inventoryScreenTabs.addAll(tabs);
			tabs.forEach(event::addListener);
		}
	}

	public static void onScreenRenderPre(ScreenEvent.Render.Pre event) {
		if (event.getScreen() instanceof InventoryScreen screen && screen == inventoryScreen) {
			updateTabValues(inventoryScreenTabs, screen.getGuiLeft(), screen.getGuiTop());
		}
	}

	public static void onScreenClosing(ScreenEvent.Closing event) {
		if (event.getScreen() == inventoryScreen) {
			inventoryScreen = null;
			inventoryScreenTabs.clear();
		}
	}
}
