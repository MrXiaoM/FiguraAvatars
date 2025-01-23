package top.mrxiaom.figura.bukkit;

import top.mrxiaom.pluginbase.func.language.IHolderAccessor;
import top.mrxiaom.pluginbase.func.language.Language;
import top.mrxiaom.pluginbase.func.language.LanguageEnumAutoHolder;

import java.util.List;

import static top.mrxiaom.pluginbase.func.language.LanguageEnumAutoHolder.wrap;

@Language(prefix = "messages.")
public enum Messages implements IHolderAccessor {
    commands__reload("&a配置文件已重载"),
    commands__no_permission("&c你没有执行该命令的权限"),
    player__not_found("&e玩家不在线 (或不存在)"),
    player__only("&e该操作只能由玩家执行"),
    commands__help__normal("",
            "&c&lFiguraAvatars&r &b帮助命令",
            "&f/avatars open &7打开菜单",
            ""),
    commands__help__admin("",
            "&c&lFiguraAvatars&r &b帮助命令",
            "&f/avatars open &7打开菜单",
            "&f/avatars open <玩家> &7为某人打开菜单",
            "&f/avatars reload &7重载配置文件",
            ""),


    ;
    Messages(String defaultValue) {
        holder = wrap(this, defaultValue);
    }
    Messages(String... defaultValue) {
        holder = wrap(this, defaultValue);
    }
    Messages(List<String> defaultValue) {
        holder = wrap(this, defaultValue);
    }
    private final LanguageEnumAutoHolder<Messages> holder;
    public LanguageEnumAutoHolder<Messages> holder() {
        return holder;
    }
}
