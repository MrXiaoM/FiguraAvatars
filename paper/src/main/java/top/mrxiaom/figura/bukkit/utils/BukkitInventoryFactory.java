package top.mrxiaom.figura.bukkit.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class BukkitInventoryFactory implements InventoryFactory {
    @Override
    @SuppressWarnings({"deprecation"})
    public Inventory create(InventoryHolder owner, int size, String title) {
        String guiTitle = MiniMessageConvert.miniMessageToLegacy(title);
        return Bukkit.createInventory(owner, size, guiTitle);
    }
}
