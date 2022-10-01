package me.kikugie.stt.mixin;

import me.kikugie.stt.StoragetechTools;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @ModifyArg(
        method = "keyPressed",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V",
            ordinal = 1
        )
    )
    private Screen afterSuccessfullyProcessingChatMessage(Screen thisArgIsAlwaysNull) {
        Screen required = StoragetechTools.screenToOpen;
        if (required != null) {
            StoragetechTools.screenToOpen = null;
        }
        return required;
    }
}
