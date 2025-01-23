package top.mrxiaom.figura.bukkit;
        
import top.mrxiaom.figura.bukkit.utils.BukkitInventoryFactory;
import top.mrxiaom.figura.bukkit.utils.InventoryFactory;
import top.mrxiaom.figura.bukkit.utils.MiniMessageConvert;
import top.mrxiaom.figura.bukkit.utils.PaperInventoryFactory;
import top.mrxiaom.pluginbase.BukkitPlugin;
import top.mrxiaom.pluginbase.utils.Util;

public class FiguraAvatars extends BukkitPlugin {
    public static FiguraAvatars getInstance() {
        return (FiguraAvatars) BukkitPlugin.getInstance();
    }

    public FiguraAvatars() {
        super(options()
                .bungee(false)
                .adventure(true)
                .database(false)
                .reconnectDatabaseWhenReloadConfig(false)
                .vaultEconomy(false)
                .scanIgnore("top.mrxiaom.figura.bukkit.libs")
        );
    }

    InventoryFactory inventoryFactory;

    public InventoryFactory getInventoryFactory() {
        return inventoryFactory;
    }

    @Override
    protected void beforeEnable() {
        MiniMessageConvert.init();
        if (Util.isPresent("com.destroystokyo.paper.utils.PaperPluginLogger")) {
            inventoryFactory = new PaperInventoryFactory();
        } else {
            inventoryFactory = new BukkitInventoryFactory();
        }
    }

    @Override
    protected void afterEnable() {
        getLogger().info("FiguraAvatars 加载完毕");
    }
}
