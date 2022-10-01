package me.kikugie.stt.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.kikugie.stt.FakeScreen;
import me.kikugie.stt.StackSize;
import me.kikugie.stt.StoragetechTools;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class  GetItemsCommand {

    static final String STACK_SIZE = "stack-size";

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess access) {
        var command = literal("get-items")
            .then(
                argument(STACK_SIZE, new StackSizeArgumentType())
                    .executes(GetItemsCommand::getItems)
            );
        dispatcher.register(command);
    }

    private static int getItems(CommandContext<FabricClientCommandSource> context) {
        String stackSizeArgument = context.getArgument(STACK_SIZE, String.class).toUpperCase();
        StackSize stackSize = StackSize.valueOf(stackSizeArgument);
        StoragetechTools.screenToOpen = FakeScreen.createScreenWithInventory(stackSize);
        return 0;
    }
}
