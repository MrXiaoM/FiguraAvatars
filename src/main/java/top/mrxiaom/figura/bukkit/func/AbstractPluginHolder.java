package top.mrxiaom.figura.bukkit.func;
        
import top.mrxiaom.figura.bukkit.FiguraAvatars;

@SuppressWarnings({"unused"})
public abstract class AbstractPluginHolder extends top.mrxiaom.pluginbase.func.AbstractPluginHolder<FiguraAvatars> {
    public AbstractPluginHolder(FiguraAvatars plugin) {
        super(plugin);
    }

    public AbstractPluginHolder(FiguraAvatars plugin, boolean register) {
        super(plugin, register);
    }
}
