package techguns.items.additionalslots;

import com.mojang.datafixers.util.Pair;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import techguns.TGConfig;
import techguns.TGPackets;
import techguns.Techguns;
import techguns.api.tginventory.ITGSpecialSlot;
import techguns.capabilities.TGExtendedPlayer;
import techguns.gui.player.TGPlayerInventory;
import techguns.packets.PacketTGExtendedPlayerSync;
import techguns.util.InventoryUtil;

/**
 * Additional slot / Techguns player inventory part of the 1.12 TGTickHandler and TGEventHandler:
 * auto feeder, ticking of the face/back/hand slot items, drops on death, syncing of the slots to the clients.
 */
@Mod.EventBusSubscriber(modid = Techguns.MODID)
public class AdditionalSlotEventHandler {

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		Player ply = event.player;
		TGExtendedPlayer props = TGExtendedPlayer.get(ply);
		if (props == null) {
			return;
		}

		/**
		 * Auto feeder (server only in the port: food stats are synced by vanilla, the inventory by the sync below)
		 */
		if (!ply.level().isClientSide && !TGConfig.disableAutofeeder && ply.getFoodData().getFoodLevel() <= 19) {
			autoFeed(ply, props);
		}

		/**
		 * Tick addition slots
		 */
		props.isGliding = false;

		tickSlot(props.tg_inventory.inventory.get(TGPlayerInventory.SLOT_FACE), event);
		tickSlot(props.tg_inventory.inventory.get(TGPlayerInventory.SLOT_BACK), event);
		tickSlot(props.tg_inventory.inventory.get(TGPlayerInventory.SLOT_HAND), event);

		props.handlePlayerGliding();

		if (ply instanceof ServerPlayer sply) {
			syncSlots(sply, props);
		}
	}

	protected static void tickSlot(ItemStack slot, PlayerTickEvent event) {
		if (!slot.isEmpty() && slot.getItem() instanceof ITGSpecialSlot) {
			ITGSpecialSlot item = (ITGSpecialSlot) slot.getItem();
			item.onPlayerTick(slot, event);
		}
	}

	protected static void autoFeed(Player ply, TGExtendedPlayer props) {
		int needed = 20 - ply.getFoodData().getFoodLevel();
		if (props.foodleft > 0) {
			if (props.foodleft <= needed) {
				ply.getFoodData().eat(props.foodleft, props.lastSaturation);
				props.foodleft = 0;
				props.lastSaturation = 0.0f;
			} else {
				ply.getFoodData().eat(needed, props.lastSaturation);
				props.foodleft -= needed;
			}
			if (ply instanceof ServerPlayer sply) {
				TGPackets.network.sendTo(new PacketTGExtendedPlayerSync(ply, props, true), sply);
			}
		} else {
			ItemStack stack = InventoryUtil.consumeFood(props.tg_inventory.inventory, TGPlayerInventory.SLOTS_AUTOFOOD_START, TGPlayerInventory.SLOTS_AUTOFOOD_END + 1);
			if (!stack.isEmpty()) {
				FoodProperties food = stack.getFoodProperties(ply);
				if (food == null) {
					return;
				}

				//1.12 ItemFood.onFoodEaten: potion effects of the food
				for (Pair<MobEffectInstance, Float> pair : food.getEffects()) {
					if (pair.getFirst() != null && ply.getRandom().nextFloat() < pair.getSecond()) {
						ply.addEffect(new MobEffectInstance(pair.getFirst()));
					}
				}

				ply.level().playSound(null, ply.getX(), ply.getY(), ply.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 1f, 1f);

				short left = (short) (food.getNutrition() - needed);
				if (left > 0) {
					ply.getFoodData().eat(needed, food.getSaturationModifier());
					props.foodleft = left;
					props.lastSaturation = food.getSaturationModifier();
				} else {
					ply.getFoodData().eat(food.getNutrition(), food.getSaturationModifier());
					props.foodleft = 0;
					props.lastSaturation = 0.0f;
				}
				if (ply instanceof ServerPlayer sply) {
					TGPackets.network.sendTo(new PacketTGExtendedPlayerSync(ply, props, true), sply);
				}
			}
		}
	}

	/**
	 * Replaces the 1.12 synced data parameters of the visible slots: sends the slots that changed since the last sync,
	 * the visible slots (face, back, hand) to all tracking players, the whole inventory to the player itself.
	 */
	protected static void syncSlots(ServerPlayer ply, TGExtendedPlayer props) {
		boolean first = props.lastSyncedSlots == null;
		if (first) {
			props.lastSyncedSlots = new ItemStack[TGPlayerInventory.NUMSLOTS];
		}
		boolean visibleChanged = false;
		boolean otherChanged = false;
		for (int i = 0; i < TGPlayerInventory.NUMSLOTS; i++) {
			ItemStack current = props.tg_inventory.inventory.get(i);
			ItemStack last = props.lastSyncedSlots[i];
			if (last == null || !ItemStack.matches(current, last)) {
				props.lastSyncedSlots[i] = current.copy();
				if (i <= TGPlayerInventory.SLOT_HAND) {
					visibleChanged = true;
				} else {
					otherChanged = true;
				}
			}
		}
		if (first) {
			//the join sync (TGEventHandler.onEntityJoinWorld) already sent everything to the player
			if (visibleChanged) {
				TGPackets.network.channel.send(net.minecraftforge.network.PacketDistributor.TRACKING_ENTITY.with(() -> ply), new PacketTGExtendedPlayerSync(ply, props, false));
			}
			return;
		}
		if (visibleChanged) {
			TGPackets.network.channel.send(net.minecraftforge.network.PacketDistributor.TRACKING_ENTITY.with(() -> ply), new PacketTGExtendedPlayerSync(ply, props, false));
		}
		if (visibleChanged || otherChanged) {
			TGPackets.network.sendTo(new PacketTGExtendedPlayerSync(ply, props, true), ply);
		}
	}

	/**
	 * 1.12 requested the sync from the client on StartTracking, StartTracking is fired on the server in 1.20
	 */
	@SubscribeEvent
	public static void onStartTracking(PlayerEvent.StartTracking event) {
		if (event.getTarget() instanceof Player tracked && event.getEntity() instanceof ServerPlayer tracker) {
			TGExtendedPlayer props = TGExtendedPlayer.get(tracked);
			if (props != null) {
				TGPackets.network.sendTo(new PacketTGExtendedPlayerSync(tracked, props, false), tracker);
				if (props.isJumpkeyPressed()) {
					TGPackets.network.sendTo(new techguns.packets.PacketTGPlayerFieldSync(tracked, techguns.packets.PacketTGPlayerFieldSync.FIELD_JUMPBUTTONPRESSED, true), tracker);
				}
			}
		}
	}

	/**
	 * Drop the Techguns inventory on death unless keepInventory (1.12 PlayerDropsEvent)
	 */
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onPlayerDrops(LivingDropsEvent event) {
		if (event.getEntity() instanceof Player ply && !ply.level().isClientSide) {
			TGExtendedPlayer props = TGExtendedPlayer.get(ply);
			if (props != null) {
				java.util.List<net.minecraft.world.entity.item.ItemEntity> list = new java.util.ArrayList<>();
				props.addDropsToList(ply, list);
				event.getDrops().addAll(list);
			}
		}
	}
}
