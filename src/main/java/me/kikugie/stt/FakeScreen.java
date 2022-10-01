package me.kikugie.stt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("DataFlowIssue") // Suppressing "MAY proDucE nUllPointErExcepTiOn"
public class FakeScreen extends GenericContainerScreen {
    private static final Set<String> UNOBTAINABLES = new HashSet<>(Set.of("bedrock", "end_portal_frame", "barrier", "light", "command_block", "repeating_command_block", "chain_command_block", "structure_void", "structure_block", "jigsaw", "sculk_sensor", "petrified_oak_slab", "spawner", "player_head", "budding_amethyst", "chorus_plant", "dirt_path", "grass_path", "farmland", "frogspawn", "infested_stone", "infested_cobblestone", "infested_stone_bricks", "infested_mossy_stone_bricks", "infested_cracked_stone_bricks", "infested_chiseled_stone_bricks", "infested_deepslate", "reinforced_deepslate", "command_block_minecart", "knowledge_book", "debug_stick", "bundle"));
    private static final Set<String> JUNK = new HashSet<>(Set.of("filled_map", "written_book", "tipped_arrow", "firework_star"));

    private static final int MAX_SLOTS = 54;

    private FakeScreen(String title, PlayerEntity player, Inventory inventory) {
        super(
            GenericContainerScreenHandler.createGeneric9x6(0, player.getInventory(), inventory),
            player.getInventory(),
            Text.translatable(title)
        );
    }

    private static boolean isItemObtainableAndNotJunk(Item item) {
        String name = Registry.ITEM.getId(item).getPath();
        return !name.contains("spawn_egg") && !UNOBTAINABLES.contains(name) && !JUNK.contains(name);
    }

    private static Inventory generateInventoryWithSurvivalObtainableBoxes(StackSize stackSize) {
        Inventory inventory = new SimpleInventory(FakeScreen.MAX_SLOTS);
        List<ItemStack> survivalObtainableStacks = new ArrayList<>();

        for (Item item: Registry.ITEM) {
            ItemStack stack = item.getDefaultStack();
            if (!stack.isEmpty() && isItemObtainableAndNotJunk(item) && stackSize.getSizes().contains(stack.getMaxCount())) {
                survivalObtainableStacks.add(stack);
            }
        }

        int totalItems = survivalObtainableStacks.size();

        final int SHULKER_BOX_SIZE = 27;

        for (int i = 0; i < Math.ceil((double) totalItems / SHULKER_BOX_SIZE); i++) {
            ItemStack box = Items.WHITE_SHULKER_BOX.getDefaultStack();
            NbtList boxItems = new NbtList();

            int boxSlot = 0;
            for (ItemStack stack: survivalObtainableStacks.subList(i * SHULKER_BOX_SIZE, Math.min(totalItems, (i + 1) * SHULKER_BOX_SIZE))) {
                NbtCompound compound = new NbtCompound();
                compound.putByte("Slot", (byte) boxSlot++);
                stack.writeNbt(compound);
                boxItems.add(compound);
            }

            NbtCompound boxData = new NbtCompound();
            boxData.put("Items", boxItems);
            box.setSubNbt("BlockEntityTag", boxData);

            inventory.setStack(i, box);
        }

        return inventory;
    }

    public static Screen createScreenWithInventory(StackSize stackSize) {
        Inventory inventory = generateInventoryWithSurvivalObtainableBoxes(stackSize);
        return new FakeScreen(stackSize.getTitle(), MinecraftClient.getInstance().player, inventory);
    }

    @Override
    protected void onMouseClick(Slot clickedSlot, int slotId, int button, SlotActionType actionType) {
        if (clickedSlot != null) {
            if (actionType == SlotActionType.PICKUP || actionType == SlotActionType.QUICK_CRAFT) {

                ItemStack slotStack = clickedSlot.getStack();
                clickedSlot.setStack(this.handler.getCursorStack());
                this.handler.setCursorStack(slotStack);

                if (clickedSlot.inventory instanceof PlayerInventory) {
                    this.client.interactionManager.clickCreativeStack(clickedSlot.getStack(), slotId - 45);
                }
            } else if (actionType == SlotActionType.QUICK_MOVE) {

                for (int i = 44; i >= 9; i--) {
                    if (this.handler.slots.get(i + 45).getStack().isEmpty()) {
                        this.handler.slots.get(i + 45).setStack(clickedSlot.getStack());
                        this.client.interactionManager.clickCreativeStack(this.handler.slots.get(i + 45).getStack(), i);
                        clickedSlot.setStack(ItemStack.EMPTY);
                        break;
                    }
                }
            } else if (actionType == SlotActionType.THROW) {

                this.client.interactionManager.dropCreativeStack(clickedSlot.getStack());
                clickedSlot.setStack(ItemStack.EMPTY);
            }
        } else if (!this.handler.getCursorStack().isEmpty() && actionType != SlotActionType.QUICK_CRAFT) {
            this.client.interactionManager.dropCreativeStack(this.handler.getCursorStack());
            this.handler.setCursorStack(ItemStack.EMPTY);
        }
    }
}
