package top.mrxiaom.figura.bukkit.func;

import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import top.mrxiaom.figura.bukkit.FiguraAvatars;
import top.mrxiaom.figura.bukkit.func.entry.Avatar;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AutoRegister
public class Avatars extends AbstractModule implements Listener {
    Map<String, Avatar> avatars = new HashMap<>();
    final String userAgent;
    String apiUrl;
    public Avatars(FiguraAvatars plugin) {
        super(plugin);
        userAgent = "Minecraft/" + MinecraftVersion.getVersion().name() + " FiguraAvatars/" + plugin.getDescription().getVersion();
        registerEvents();
    }

    @EventHandler
    public void on(PlayerJoinEvent e) {
        sendUploadState(e.getPlayer().getUniqueId(), e.getPlayer().hasPermission("figura.upload"));
    }

    public HttpURLConnection createConnection(String path) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl + path).openConnection();
        conn.addRequestProperty("Accept", "*/*");
        conn.addRequestProperty("Host", "lambda");
        conn.addRequestProperty("User-Agent", userAgent);
        return conn;
    }

    @Override
    public void reloadConfig(MemoryConfiguration config) {
        avatars.clear();
        String host = config.getString("internal-api-url", "127.0.0.1:6665");
        apiUrl = "http://" + (host.endsWith("/") ? host : (host + "/")) + "internal/";
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
            plugin.saveResource("avatars/hoshino/hoshino.moon", new File(folder, "hoshino.moon"));
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

    public void deleteAvatar(UUID uuid) {
        try {
            HttpURLConnection conn = createConnection(uuid + "/avatar");
            conn.setRequestMethod("DELETE");
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                warn("执行失败 DELETE /internal/" + uuid + "/avatar: " + responseCode);
            }
        } catch (IOException e) {
            warn(e);
        }
    }

    public void putAvatar(UUID uuid, File file, boolean temp) {
        try {
            HttpURLConnection conn = createConnection(uuid + (temp ? "/temp" : "/avatar"));
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.connect();
            try (OutputStream output = conn.getOutputStream()) {
                try (FileInputStream input = new FileInputStream(file)) {
                    byte[] buffer = new byte[1024 * 10];
                    int len;
                    while ((len = input.read(buffer)) != -1) {
                        output.write(buffer, 0, len);
                    }
                }
            }
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                warn("执行失败 PUT /internal/" + uuid + (temp ? "/temp: " : "/avatar: ") + responseCode);
            }
        } catch (IOException e) {
            warn(e);
        }
    }

    public void sendUpdateAvatar(UUID uuid) {
        try {
            HttpURLConnection conn = createConnection(uuid + "/event");
            conn.setRequestMethod("GET");
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                warn("执行失败 GET /internal/" + uuid + "/event: " + responseCode);
            }
        } catch (IOException e) {
            warn(e);
        }
    }

    public void sendUploadState(UUID uuid, boolean newState) {
        try {
            HttpURLConnection conn = createConnection(uuid + "/upload_state/" + newState);
            conn.setRequestMethod("GET");
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                warn("执行失败 GET /internal/" + uuid + "/upload_state/" + newState + ": " + responseCode);
            }
        } catch (IOException e) {
            warn(e);
        }
    }

    public Avatars inst() {
        return instanceOf(Avatars.class);
    }
}
