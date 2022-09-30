package me.kikugie.stt.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class GetItemsCommand {
    static final List<String> survivalUnobtainables = List.of("bedrock", "end_portal_frame", "barrier", "light", "command_block", "repeating_command_block", "chain_command_block", "structure_void", "structure_block", "jigsaw", "sculk_sensor", "petrified_oak_slab", "spawner", "player_head", "budding_amethyst", "chorus_plant", "dirt_path", "grass_path", "farmland", "frogspawn", "infested_stone", "infested_cobblestone", "infested_stone_bricks", "infested_mossy_stone_bricks", "infested_cracked_stone_bricks", "infested_chiseled_stone_bricks", "infested_deepslate", "reinforced_deepslate", "command_block_minecart", "knowledge_book", "debug_stick", "bundle");
    static final List<String> junkItems = List.of("filled_map", "written_book", "tipped_arrow", "firework_star");
    static final Map<String, List<Integer>> stackMap = Map.of(
            "all", List.of(1, 16, 64),
            "stackables", List.of(16, 64),
            "64-stackables", List.of(64),
            "16-stackables", List.of(16),
            "unstackables", List.of(1)
    );
    static final String stackParam = "stackability";
    public static final Logger LOGGER = LoggerFactory.getLogger("StoragetechTools");
    static final ClientPlayerEntity player = MinecraftClient.getInstance().player;
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess access) {
        var command = literal("getitems")
                .then(argument(stackParam, StringArgumentType.string())
                        .suggests(((context, builder) -> CommandSource.suggestMatching(stackMap.keySet(), builder)))
                        .executes(GetItemsCommand::getItems)
                );
        dispatcher.register(command);
    }
    private static int getItems(CommandContext<FabricClientCommandSource> context) {
        var selectedStackability = stackMap.get(context.getArgument(stackParam, String.class));
        if (selectedStackability == null) {
            return 1;
        }
        var allItems = Registry.ITEM.stream()
                .filter(item -> !survivalUnobtainables.contains(Registry.ITEM.getId(item).getPath()))
                .filter(item -> !junkItems.contains(Registry.ITEM.getId(item).getPath()))
                .filter(item -> selectedStackability.contains(item.getMaxCount()))
                .toList();

        var inventory = new SimpleInventory(new ItemStack(Items.AMETHYST_BLOCK)); // pass in actual contents later
        var factory = new SimpleNamedScreenHandlerFactory((syncId, inv, pl) -> openChestMenu(syncId, inventory, player), Text.translatable("Title of screen"));

        player.openHandledScreen(factory);

        return 0;
    }
    public static ScreenHandler openChestMenu(int syncId, SimpleInventory inventory, ClientPlayerEntity player) {
        assert MinecraftClient.getInstance().player != null;
        return GenericContainerScreenHandler.createGeneric9x6(syncId, player.getInventory(), inventory);
    }
}
