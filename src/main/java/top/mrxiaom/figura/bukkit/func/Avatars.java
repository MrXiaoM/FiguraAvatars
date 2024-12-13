package top.mrxiaom.figura.bukkit.func;

import org.bukkit.configuration.MemoryConfiguration;
import top.mrxiaom.figura.bukkit.FiguraAvatars;
import top.mrxiaom.figura.bukkit.func.entry.Avatar;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.Util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@AutoRegister
public class Avatars extends AbstractModule {
    File avatarsFolder;
    Map<String, Avatar> avatars = new HashMap<>();
    public Avatars(FiguraAvatars plugin) {
        super(plugin);
    }

    @Override
    public void reloadConfig(MemoryConfiguration config) {
        String path = config.getString("avatars-folder", "./avatars");
        if (path.startsWith("./")) {
            avatarsFolder = new File(plugin.getDataFolder(), path.substring(2));
        } else {
            avatarsFolder = new File(path);
        }
        reloadAvatars();
    }

    private void reloadAvatars() {
        avatars.clear();
        if (!avatarsFolder.exists()) {
            Util.mkdirs(avatarsFolder);
            return;
        }
        File[] files = avatarsFolder.listFiles();
        if (files != null) for (File folder : files) {
            try {
                Avatar avatar = Avatar.loadFromFolder(folder);
                avatars.put(avatar.id, avatar);
            } catch (Throwable t) {
                warn("加载外观配置 " + folder.getName() + " 时出现错误", t);
            }
        }
    }

    public Avatars inst() {
        return instanceOf(Avatars.class);
    }
}
