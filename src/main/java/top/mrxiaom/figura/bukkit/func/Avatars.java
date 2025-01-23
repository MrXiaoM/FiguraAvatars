package top.mrxiaom.figura.bukkit.func;

import com.google.common.collect.Lists;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import org.apache.http.client.methods.*;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.figura.bukkit.FiguraAvatars;
import top.mrxiaom.figura.bukkit.func.entry.Avatar;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.Util;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

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
        UUID uuid = e.getPlayer().getUniqueId();
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeLong(uuid.getMostSignificantBits());
        buffer.writeLong(uuid.getLeastSignificantBits());
        customPayload(e.getPlayer(), "figura:uuid", ByteBufUtil.getBytes(buffer));
        sendUploadState(e.getPlayer().getUniqueId(), e.getPlayer().hasPermission("figura.upload"));
    }

    public HttpRequestBase createConnection(String method, String path) throws IOException {
        String url = apiUrl + path;
        HttpRequestBase request = null;
        if (method.equals("GET")) {
            request = new HttpGet(url);
        }
        if (method.equals("POST")) {
            request = new HttpPost(url);
        }
        if (method.equals("PUT")) {
            request = new HttpPut(url);
        }
        if (method.equals("DELETE")) {
            request = new HttpDelete(url);
        }
        if (request == null) throw new IllegalArgumentException("Method '" + method + "' is not supported");
        request.setHeader("Accept", "*/*");
        request.setHeader("Host", "lambda");
        request.setHeader("User-Agent", userAgent);
        return request;
    }

    @Override
    public void reloadConfig(MemoryConfiguration config) {
        avatars.clear();
        String host = config.getString("internal-api-url", "127.0.0.1:6665");
        apiUrl = "http://" + (host.endsWith("/") ? host : (host + "/")) + "internal/";
        for (String path : config.getStringList("avatars-folders")) {
            boolean exportDefault = path.equals("./avatars");
            if (path.startsWith("./")) {
                reloadAvatars(new File(plugin.getDataFolder(), path.substring(2)), exportDefault);
            } else {
                reloadAvatars(new File(path), false);
            }
        }
        info("[avatars] 共加载 " + avatars.size() + " 个外观配置");
        checkHealth();
    }

    private void reloadAvatars(File avatarsFolder, boolean exportDefault) {
        if (!avatarsFolder.exists()) {
            Util.mkdirs(avatarsFolder);
            // 导出默认配置
            if (exportDefault) {
                File folder = new File(avatarsFolder, "hoshino");
                plugin.saveResource("avatars/hoshino/metadata.yml", new File(folder, "metadata.yml"));
                plugin.saveResource("avatars/hoshino/hoshino.moon", new File(folder, "hoshino.moon"));
                plugin.saveResource("avatars/hoshino/LICENSE", new File(folder, "LICENSE"));
            }
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

    public void customPayload(Player player, String id, byte[] bytes) {
        // 发送 CustomPayLoad 包
        if (!player.getListeningPluginChannels().contains(id)) {
            Class<? extends Player> clazz = player.getClass();
            try {
                Method method = clazz.getDeclaredMethod("addChannel", String.class);
                method.invoke(player, id);
            } catch (ReflectiveOperationException e) {
                warn(e);
                return;
            }
        }
        player.sendPluginMessage(plugin, id, bytes);
    }

    public void openWardrobe(Player player) {
        customPayload(player, "figura:wardrobe", new ByteArrayOutputStream().toByteArray());
    }

    public void requestReconnect(Player player) {
        customPayload(player, "figura:reconnect", new ByteArrayOutputStream().toByteArray());
    }

    public Set<String> keys() {
        return avatars.keySet();
    }

    @Nullable
    public Avatar get(String id) {
        return avatars.get(id);
    }

    public List<Avatar> all() {
        return Lists.newArrayList(avatars.values());
    }

    public void deleteAvatar(UUID uuid) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpRequestBase request = createConnection("DELETE", uuid + "/avatar");
            try (CloseableHttpResponse response = client.execute(request)) {
                int responseCode = response.getStatusLine().getStatusCode();
                if (responseCode != 200) {
                    warn("执行失败 DELETE /internal/" + uuid + "/avatar: " + responseCode);
                }
            }
        } catch (IOException e) {
            warn(e);
        }
    }

    public void checkHealth() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpRequestBase request = createConnection("GET", "health");
            try (CloseableHttpResponse response = client.execute(request)) {
                int responseCode = response.getStatusLine().getStatusCode();
                if (responseCode != 200) {
                    warn("执行失败 GET /internal/health: " + responseCode);
                } else {
                    info("可正常连接到后端");
                }
            }
        } catch (IOException e) {
            warn(e);
        }
    }

    public void putAvatar(UUID uuid, File file, boolean temp) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpRequestBase request = createConnection("PUT", uuid + (temp ? "/temp" : "/avatar"));
            try (FileInputStream input = new FileInputStream(file)) {
                ((HttpPut) request).setEntity(new InputStreamEntity(input));
                try (CloseableHttpResponse response = client.execute(request)) {
                    int responseCode = response.getStatusLine().getStatusCode();
                    if (responseCode != 200) {
                        warn("执行失败 PUT /internal/" + uuid + (temp ? "/temp: " : "/avatar: ") + responseCode);
                    }
                }
            }
        } catch (IOException e) {
            warn(e);
        }
    }

    public void sendUpdateAvatar(UUID uuid) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpRequestBase request = createConnection("GET", uuid + "/event");
            try (CloseableHttpResponse response = client.execute(request)) {
                int responseCode = response.getStatusLine().getStatusCode();
                if (responseCode != 200) {
                    warn("执行失败 GET /internal/" + uuid + "/event: " + responseCode);
                }
            }
        } catch (IOException e) {
            warn(e);
        }
    }

    public void sendUploadState(UUID uuid, boolean newState) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpRequestBase request = createConnection("GET", uuid + "/upload_state/" + newState);
            try (CloseableHttpResponse response = client.execute(request)) {
                int responseCode = response.getStatusLine().getStatusCode();
                if (responseCode != 200) {
                    warn("执行失败 GET /internal/" + uuid + "/upload_state/" + newState + ": " + responseCode);
                }
            }
        } catch (IOException e) {
            warn(e);
        }
    }

    public static Avatars inst() {
        return instanceOf(Avatars.class);
    }
}
