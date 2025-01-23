package top.mrxiaom.figura.bukkit.gui;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import top.mrxiaom.figura.bukkit.FiguraAvatars;
import top.mrxiaom.figura.bukkit.func.AbstractGuiModule;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.func.gui.LoadedIcon;
import top.mrxiaom.pluginbase.gui.IGui;

import java.io.File;
import java.util.List;

@AutoRegister
public class GuiAvatars extends AbstractGuiModule {
    LoadedIcon itemIcon;
    char itemRedirect;
    List<String> itemOpPreview, itemOpEquip, itemOpEquipNoPerm;
    LoadedIcon upIcon, downIcon;
    char upRedirect, downRedirect;
    public GuiAvatars(FiguraAvatars plugin) {
        super(plugin, new File(plugin.getDataFolder(), "gui/avatars.yml"));
    }

    @Override
    protected String warningPrefix() {
        return "[gui/avatars.yml]";
    }

    @Override
    public void reloadConfig(MemoryConfiguration cfg) {
        if (!file.exists()) {
            plugin.saveResource("gui/avatars.yml", file);
        }
        super.reloadConfig(cfg);
    }

    @Override
    protected void loadMainIcon(ConfigurationSection section, String id, LoadedIcon icon) {
        switch (id) {
            case "项": {
                itemIcon = icon;
                itemRedirect = section.getString(id + ".redirect", " ").charAt(0);
                itemOpPreview = section.getStringList(id + ".operations.preview");
                itemOpEquip = section.getStringList(id + ".operations.equip");
                itemOpEquipNoPerm = section.getStringList(id + ".operations.equip-no-permission");
                break;
            }
            case "上": {
                upIcon = icon;
                upRedirect = section.getString(id + ".redirect", " ").charAt(0);
                break;
            }
            case "下": {
                downIcon = icon;
                downRedirect = section.getString(id + ".redirect", " ").charAt(0);
                break;
            }
        }
    }

    @Override
    protected ItemStack applyMainIcon(IGui instance, Player player, char id, int index, int appearTimes) {
        switch (id) {
            case '项': {

            }
            case '上': {

            }
            case '下': {

            }
        }
        return null;
    }

    public Impl createGui(Player player) {
        return new Impl(player);
    }

    public class Impl extends Gui {
        protected Impl(Player player) {
            super(player, guiTitle, guiInventory);
        }

        @Override
        public void onClick(InventoryAction action, ClickType click,
                            InventoryType.SlotType slotType, int slot,
                            ItemStack currentItem, ItemStack cursor,
                            InventoryView view, InventoryClickEvent event
        ) {
            event.setCancelled(true);
        }
    }

    public static GuiAvatars inst() {
        return instanceOf(GuiAvatars.class);
    }
}
