package dev.simplix.cirrus.common.i18n;

import dev.simplix.cirrus.common.item.CirrusItem;
import lombok.NonNull;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class Localizer {

    public static List<String> localize(
            LocalizedStringList localizedStringList,
            @NonNull Locale locale,
            String... replacements) {
        if (localizedStringList == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(Replacer.of(localizedStringList.translated(locale))
                .replaceAll((Object[]) replacements).replacedMessage());
    }

    public static String localize(
            LocalizedString localizedString,
            @NonNull Locale locale,
            String... replacements) {
        if (localizedString == null) {
            return "";
        }
        return Replacer
                .of(localizedString.translated(locale))
                .replaceAll((Object[]) replacements)
                .replacedMessageJoined();
    }

    public static CirrusItem localize(
            @NonNull LocalizedItemStackModel model,
            @NonNull Locale locale,
            @NonNull String... replacements) {
        return CirrusItem.of(localize(model.displayName(), locale, replacements))
                .actionArguments(Arrays.asList(Replacer
                        .of(model.actionArguments() == null ? Collections.emptyList() : model.actionArguments())
                        .replaceAll((Object[]) replacements)
                        .replacedMessage()))
                .actionHandler(model.actionHandler())
                .amount(model.amount())
                .durability(model.durability())
                .itemType(model.itemType())
                .lore(localize(model.lore(), locale, replacements))
                .nbt(formatNbt(model.nbt(), replacements))
                .slots(model.slots());
    }

    private static CompoundTag formatNbt(
            @Nullable CompoundTag compoundTag,
            @NonNull String... replacements) {
        if (compoundTag == null) {
            return null;
        }
        try {
            String mojangson = SNBTUtil.toSNBT(compoundTag);
            return (CompoundTag) SNBTUtil.fromSNBT(Replacer.of(mojangson)
                    .replaceAll((Object[]) replacements).replacedMessageJoined());
        } catch (IOException ioException) {
            // Ignored
        }
        return compoundTag;
    }

}
