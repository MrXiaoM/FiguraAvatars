package top.mrxiaom.figura.bukkit.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.figura.bukkit.FiguraAvatars;
import top.mrxiaom.figura.bukkit.Messages;
import top.mrxiaom.figura.bukkit.func.AbstractGuiModule;
import top.mrxiaom.figura.bukkit.func.Avatars;
import top.mrxiaom.figura.bukkit.func.entry.Avatar;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.func.gui.IModifier;
import top.mrxiaom.pluginbase.func.gui.LoadedIcon;
import top.mrxiaom.pluginbase.gui.IGui;
import top.mrxiaom.pluginbase.utils.AdventureItemStack;
import top.mrxiaom.pluginbase.utils.Pair;

import java.io.File;
import java.util.ArrayList;
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

    private ItemStack otherIcon(Player player, char id) {
        LoadedIcon icon = otherIcons.get(id);
        if (icon != null) {
            return icon.generateIcon(player);
        } else {
            return new ItemStack(Material.AIR);
        }
    }

    @Override
    protected ItemStack applyMainIcon(IGui instance, Player player, char id, int index, int appearTimes) {
        Impl gui = (Impl) instance;
        switch (id) {
            case '项': {
                int i = gui.pageOffset() + appearTimes - 1;
                if (i >= gui.avatars.size()) {
                    return otherIcon(player, itemRedirect);
                }
                Avatar avatar = gui.avatars.get(i);
                Pair<String, Object>[] pairs = Pair.array(3);
                pairs[0] = Pair.of("%name%", avatar.name);
                pairs[1] = Pair.of("%author%", avatar.authors);
                pairs[2] = Pair.of("%version%", avatar.figuraVersion);
                IModifier<String> mDisplayName = displayName -> Pair.replace(displayName, pairs);
                IModifier<List<String>> mLore = oldLore -> {
                    List<String> lore = new ArrayList<>();
                    for (String line : oldLore) {
                        if (line.equals("description")) {
                            for (String s : avatar.description) {
                                lore.add(Pair.replace(s, pairs));
                            }
                            continue;
                        }
                        if (line.equals("operation")) {
                            if (avatar.canPreview(player)) {
                                lore.addAll(itemOpPreview);
                            }
                            if (avatar.canEquip(player)) {
                                lore.addAll(itemOpEquip);
                            } else {
                                lore.addAll(itemOpEquipNoPerm);
                            }
                            continue;
                        }
                        lore.add(Pair.replace(line, pairs));
                    }
                    return lore;
                };
                ItemStack item = itemIcon.generateIcon(player, mDisplayName, mLore);
                item.setType(avatar.material);
                if (avatar.customModelData != null) {
                    AdventureItemStack.setCustomModelData(item, avatar.customModelData);
                }
                return item;
            }
            case '上': {
                if (!gui.canPrevPage()) {
                    return otherIcon(player, upRedirect);
                }
                return upIcon.generateIcon(player);
            }
            case '下': {
                if (!gui.canNextPage()) {
                    return otherIcon(player, downRedirect);
                }
                return downIcon.generateIcon(player);
            }
        }
        return null;
    }

    public Impl createGui(Player player) {
        return new Impl(player);
    }

    public class Impl extends Gui implements InventoryHolder {
        private final List<Avatar> avatars;
        private final int pageSize;
        private final int maxPage;
        private Inventory created;
        private boolean legacy = false;
        private int page = 1;
        protected Impl(Player player) {
            super(player, guiTitle, guiInventory);
            avatars = Avatars.inst().all();
            int pageSize = 0;
            for (char c : inventory) {
                if (c == '项') pageSize++;
            }
            this.pageSize = pageSize;
            maxPage = (int) Math.ceil((double) avatars.size() / pageSize);
        }

        public void markLegacy() {
            this.legacy = true;
        }

        public int pageOffset() {
            return (page - 1) * pageSize;
        }

        public boolean canPrevPage() {
            return page > 1;
        }

        public boolean canNextPage() {
            return page < maxPage;
        }

        public void prevPage() {
            if (canPrevPage()) {
                page--;
                Bukkit.getScheduler().runTask(plugin, () -> {
                    updateInventory(created);
                    submitUpdate();
                });
            }
        }

        public void nextPage() {
            if (canNextPage()) {
                page++;
                Bukkit.getScheduler().runTask(plugin, () -> {
                    updateInventory(created);
                    submitUpdate();
                });
            }
        }

        @NotNull
        @Override
        public Inventory getInventory() {
            return created;
        }

        @Override
        protected Inventory create(InventoryHolder holder, int size, String title) {
            return created = plugin.getInventoryFactory().create(this, size, title);
        }

        public Avatar getAvatar(int slot) {
            int i = pageOffset() + getAppearTimes('项', slot) - 1;
            if (i < 0 || i >= avatars.size()) return null;
            return avatars.get(i);
        }

        @Override
        public void onClick(InventoryAction action, ClickType click,
                            InventoryType.SlotType slotType, int slot,
                            ItemStack currentItem, ItemStack cursor,
                            InventoryView view, InventoryClickEvent event
        ) {
            event.setCancelled(true);
            if (legacy) {
                Messages.gui__legacy.tm(player);
                player.closeInventory();
                return;
            }
            Character clickId = getClickedId(slot);
            if (clickId == null) return;
            switch (clickId) {
                case '项': {
                    if (!click.isShiftClick()) {
                        Avatar avatar = getAvatar(slot);
                        if (avatar == null) break;
                        if (click.isLeftClick() && avatar.canPreview(player)) {
                            player.closeInventory();
                            avatar.preview(player);
                            break;
                        }
                        if (click.isRightClick()) {
                            if (!avatar.canEquip(player)) {
                                Messages.commands__no_permission.tm(player);
                                break;
                            }
                            player.closeInventory();
                            avatar.equip(player);
                            break;
                        }
                    }
                    break;
                }
                case '上': {
                    prevPage();
                    break;
                }
                case '下': {
                    nextPage();
                    break;
                }
                default: {
                    LoadedIcon icon = otherIcons.get(clickId);
                    if (icon != null) {
                        icon.click(player, click);
                    }
                    break;
                }
            }
        }

        @SuppressWarnings({"UnstableApiUsage"})
        public void submitUpdate() {
            this.player.updateInventory();
        }
    }

    public static GuiAvatars inst() {
        return instanceOf(GuiAvatars.class);
    }
}
