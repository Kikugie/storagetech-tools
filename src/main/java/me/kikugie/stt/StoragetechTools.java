package me.kikugie.stt;

import me.kikugie.stt.command.GetItemsCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.gui.screen.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoragetechTools implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("StoragetechTools");
    public static Screen screenToOpen = null;

    @Override
    public void onInitialize() {
        LOGGER.info("Locking your hoppers...");

        ClientCommandRegistrationCallback.EVENT.register(GetItemsCommand::register);
    }
}
