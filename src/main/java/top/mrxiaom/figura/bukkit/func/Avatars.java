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
    Map<String, Avatar> avatars = new HashMap<>();
    public Avatars(FiguraAvatars plugin) {
        super(plugin);
    }

    @Override
    public void reloadConfig(MemoryConfiguration config) {
        avatars.clear();
        for (String path : config.getStringList("avatars-folders")) {
            if (path.startsWith("./")) {
                reloadAvatars(new File(plugin.getDataFolder(), path.substring(2)));
            } else {
                reloadAvatars(new File(path));
            }
        }
    }

    private void reloadAvatars(File avatarsFolder) {
        if (!avatarsFolder.exists()) {
            Util.mkdirs(avatarsFolder);
            // 导出默认配置
            File folder = new File(avatarsFolder, "hoshino");
            plugin.saveResource("avatars/hoshino/metadata.yml", new File(folder, "metadata.yml"));
            plugin.saveResource("avatars/hoshino/hoshino.yml", new File(folder, "hoshino.yml"));
            plugin.saveResource("avatars/hoshino/LICENSE", new File(folder, "LICENSE"));
        }
        File[] files = avatarsFolder.listFiles();
        if (files != null) for (File folder : files) {
            try {
                Avatar avatar = Avatar.loadFromFolder(folder);
                if (avatar != null) {
                    avatars.put(avatar.id, avatar);
                }
            } catch (Throwable t) {
                warn("加载外观配置 " + folder.getName() + " 时出现错误", t);
            }
        }
    }

    public Avatars inst() {
        return instanceOf(Avatars.class);
    }
}
