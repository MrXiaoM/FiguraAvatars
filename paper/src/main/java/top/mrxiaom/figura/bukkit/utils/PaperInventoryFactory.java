package top.mrxiaom.figura.bukkit.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class PaperInventoryFactory implements InventoryFactory {
    private final MiniMessage miniMessage;
    public PaperInventoryFactory() {
        miniMessage = MiniMessage.builder()
                .postProcessor(it -> it.decoration(TextDecoration.ITALIC, false))
                .build();
    }

    public Component miniMessage(String text) {
        if (text == null) {
            return Component.empty();
        }
        return miniMessage.deserialize(MiniMessageConvert.legacyToMiniMessage(text));
    }
    @Override
    @SuppressWarnings({"deprecation"})
    public Inventory create(InventoryHolder owner, int size, String title) {
        try {
            Component guiTitle = miniMessage(title);
            return Bukkit.createInventory(owner, size, guiTitle);
        } catch (LinkageError e) {
            String guiTitle = MiniMessageConvert.miniMessageToLegacy(title);
            return Bukkit.createInventory(owner, size, guiTitle);
        }
    }
}
