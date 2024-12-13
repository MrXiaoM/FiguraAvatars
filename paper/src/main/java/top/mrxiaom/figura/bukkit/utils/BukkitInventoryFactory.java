package top.mrxiaom.figura.bukkit.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class BukkitInventoryFactory implements InventoryFactory {
    @Override
    public Inventory create(InventoryHolder owner, int size, String title) {
        return Bukkit.createInventory(owner, size, MiniMessageConvert.miniMessageToLegacy(title.startsWith("&") ? title : ("&0" + title)));
    }
}
