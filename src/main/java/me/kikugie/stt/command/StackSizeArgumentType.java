package me.kikugie.stt.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.kikugie.stt.StackSize;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

// Just because it looks nicer
public class StackSizeArgumentType implements ArgumentType<String> {
    public static final Collection<String> VALID_STACKABLES = Arrays.stream(StackSize.values())
        .map(StackSize::name)
        .map(String::toLowerCase)
        .toList();

    private final DynamicCommandExceptionType WRONG_TYPE_EXCEPTION = new DynamicCommandExceptionType(
            component -> Text.translatable("Invalid stack size: %s", component)
    );

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String remainder = reader.getRemaining().toLowerCase().trim();
        if (VALID_STACKABLES.contains(remainder)) {
            return reader.readString();
        }
        throw WRONG_TYPE_EXCEPTION.create(remainder);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(VALID_STACKABLES, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return VALID_STACKABLES;
    }
}
