# FiguraAvatars

Figura λ 外观管理插件

## 简介

插件开发中

## 命令

根命令 `/figuraavatars`，别名 `/fa` 或 `/avatars`。  
`<>`包裹的是必选参数，`[]`包裹的是可选参数

| 命令                      | 描述           | 权限                          |
|-------------------------|--------------|-----------------------------|
| `/avatars open`         | 打开外观选择界面     | `figura.avatars.open`       |
| `/avatars open <玩家>`    | 为某玩家打开外观选择界面 | `figura.avatars.open.other` |
| `/avatars refresh <玩家>` | 刷新玩家的上传权限状态  | OP/控制台                      |
| `/avatars reload`       | 重载插件配置文件     | OP/控制台                      |

## 权限

+ `figura.upload` 在 Figura 衣柜自行上传模型的权限。重进游戏，或使用 `/avatars refresh` 刷新状态。

## Figura Lambda 生态软件

+ [FiguraLambda](https://github.com/MrXiaoM/FiguraLambda): 客户端Mod
+ [sculptor](https://github.com/MrXiaoM/sculptor): 第三方后端(fork)
+ [FiguraAuthProvider](https://github.com/MrXiaoM/FiguraAuthProvider): 服务端/代理端 玩家验证插件
+ [FiguraAvatars](https://github.com/MrXiaoM/FiguraAvatars): 服务端 模型管理插件 `<-- 你在这里`
