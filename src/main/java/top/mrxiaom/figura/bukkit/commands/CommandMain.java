package top.mrxiaom.figura.bukkit.commands;
        
import com.google.common.collect.Lists;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.figura.bukkit.Messages;
import top.mrxiaom.figura.bukkit.gui.GuiAvatars;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.figura.bukkit.FiguraAvatars;
import top.mrxiaom.figura.bukkit.func.AbstractModule;
import top.mrxiaom.pluginbase.utils.Util;

import java.util.*;

@AutoRegister
public class CommandMain extends AbstractModule implements CommandExecutor, TabCompleter, Listener {
    public CommandMain(FiguraAvatars plugin) {
        super(plugin);
        registerCommand("figuraavatars", this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length >= 1 && "open".equalsIgnoreCase(args[0])) {
            Player target;
            if (args.length >= 2) {
                if (!sender.hasPermission("figura.avatars.open.other")) {
                    return Messages.commands__no_permission.tm(sender);
                }
                target = Util.getOnlinePlayer(args[1]).orElse(null);
                if (target == null) {
                    return Messages.player__not_found.tm(sender);
                }
            } else {
                target = sender instanceof Player ? (Player) sender : null;
                if (target == null) {
                    return Messages.player__only.tm(sender);
                }
                if (!target.hasPermission("figura.avatars.open")) {
                    return Messages.commands__no_permission.tm(target);
                }
            }
            GuiAvatars.inst().createGui(target).open();
            return true;
        }
        if (args.length == 1 && "reload".equalsIgnoreCase(args[0]) && sender.isOp()) {
            plugin.reloadConfig();
            return Messages.commands__reload.tm(sender);
        }
        return (sender.isOp() ? Messages.commands__help__admin : Messages.commands__help__normal).tm(sender);
    }

    private static final List<String> emptyList = Lists.newArrayList();
    private static final List<String> listArg0 = Lists.newArrayList();
    private static final List<String> listOpArg0 = Lists.newArrayList(
            "reload");
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return startsWith(sender.isOp() ? listOpArg0 : listArg0, args[0]);
        }
        return emptyList;
    }

    public List<String> startsWith(Collection<String> list, String s) {
        return startsWith(null, list, s);
    }
    public List<String> startsWith(String[] addition, Collection<String> list, String s) {
        String s1 = s.toLowerCase();
        List<String> stringList = new ArrayList<>(list);
        if (addition != null) stringList.addAll(0, Lists.newArrayList(addition));
        stringList.removeIf(it -> !it.toLowerCase().startsWith(s1));
        return stringList;
    }
}
