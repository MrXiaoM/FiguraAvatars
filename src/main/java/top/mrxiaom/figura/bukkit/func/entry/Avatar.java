package top.mrxiaom.figura.bukkit.func.entry;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTFile;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.utils.Pair;
import top.mrxiaom.pluginbase.utils.Util;

import java.io.File;
import java.util.List;

public class Avatar {
    public final String id;
    public final File moon;
    public final Material material;
    public final @Nullable Integer customModelData;
    public final String name;
    public final String authors;
    public final String figuraVersion;
    public final List<String> description;
    public final @Nullable String viewPermission;
    public final @Nullable String equipPermission;

    Avatar(String id, File moon, Material material, @Nullable Integer customModelData, String name, String authors, String figuraVersion, List<String> description, @Nullable String viewPermission, @Nullable String equipPermission) {
        this.id = id;
        this.moon = moon;
        this.material = material;
        this.customModelData = customModelData;
        this.name = name;
        this.authors = authors;
        this.figuraVersion = figuraVersion;
        this.description = description;
        this.viewPermission = viewPermission;
        this.equipPermission = equipPermission;
    }

    @Nullable
    public static Avatar loadFromFolder(File folder) throws Exception {
        Pair<File, File> pair = find(folder);
        if (pair == null) {
            if (new File(folder, ".ignore").exists()) return null;
            throw illegalState("文件不全，需要一个 metadata.yml 和一个 .moon 或 .nbt 外观文件");
        }
        String id = folder.getName();
        File extraMeta = pair.getKey();
        File moon = pair.getValue();

        NBTCompound nbt = NBTFile.readFrom(moon);
        NBTCompound metadata = nbt.getCompound("metadata");
        if (metadata == null) throw illegalState("无效的模型，找不到 metadata");
        String name = metadata.getString("name");
        String authors = metadata.getString("authors");
        String figuraVersion = metadata.getString("version");

        YamlConfiguration config = YamlConfiguration.loadConfiguration(extraMeta);
        if (config.contains("display")) {
            name = config.getString("display");
        }
        Material material = Util.valueOr(Material.class, config.getString("material"), Material.PAPER);
        Integer customModelData = config.contains("custom-model-data")
                ? config.getInt("custom-model-data")
                : null;
        List<String> description = config.getStringList("description");
        String viewPermission = config.getString("permission.view", null);
        String equipPermission = config.getString("permission.equip", null);

        return new Avatar(id, moon, material, customModelData, name, authors, figuraVersion, description, viewPermission, equipPermission);
    }

    private static IllegalStateException illegalState(String message) {
        return new IllegalStateException(message);
    }

    private static Pair<File, File> find(File folder) {
        if (!folder.isDirectory()) return null;
        String[] list = folder.list();
        if (list == null) return null;
        File extraMeta = null;
        File moon = null;
        for (String s : list) {
            if (moon != null && extraMeta != null) break;
            if (extraMeta == null && s.equals("metadata.yml")) {
                extraMeta = new File(folder, s);
            }
            if (moon == null && s.endsWith(".moon") || s.endsWith(".nbt")) {
                moon = new File(folder, s);
            }
        }
        if (extraMeta == null || moon == null) {
            return null;
        } else {
            return Pair.of(extraMeta, moon);
        }
    }
}
