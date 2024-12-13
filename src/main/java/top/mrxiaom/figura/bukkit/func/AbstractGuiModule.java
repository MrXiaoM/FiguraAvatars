package top.mrxiaom.figura.bukkit.func;

import org.jetbrains.annotations.Nullable;
import top.mrxiaom.figura.bukkit.FiguraAvatars;

import java.io.File;

public abstract class AbstractGuiModule extends top.mrxiaom.pluginbase.func.AbstractGuiModule<FiguraAvatars> {
    public AbstractGuiModule(FiguraAvatars plugin, File file) {
        super(plugin, file);
    }

    public AbstractGuiModule(FiguraAvatars plugin, File file, @Nullable String mainIconsKey, @Nullable String otherIconsKey) {
        super(plugin, file, mainIconsKey, otherIconsKey);
    }
}
