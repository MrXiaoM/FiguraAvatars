package top.mrxiaom.figura.bukkit.actions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import top.mrxiaom.figura.bukkit.func.Avatars;
import top.mrxiaom.pluginbase.func.gui.actions.IAction;
import top.mrxiaom.pluginbase.func.gui.actions.IActionProvider;
import top.mrxiaom.pluginbase.utils.Pair;

public class ActionWardrobe implements IAction {
    public static final IActionProvider PROVIDER;
    public static final ActionWardrobe INSTANCE;
    static {
        INSTANCE = new ActionWardrobe();
        PROVIDER = (s) -> s.equals("[wardrobe]") || s.equals("wardrobe") ? INSTANCE : null;
    }
    private ActionWardrobe() {}
    @Override
    public void run(Player player, Pair<String, Object>[] pairs) {
        player.closeInventory();
        Avatars manager = Avatars.inst();
        Bukkit.getScheduler().runTask(manager.plugin, () -> manager.openWardrobe(player));
    }
}
