<img src="https://cdn.modrinth.com/data/cached_images/01d4b3f0a8d469b8d7b36030f2039007500b00f4.png" align="right" height="64">

# 多世界模組 (Multiworld) ![](http://cf.way2muchnoise.eu/multiworld-mod.svg) ![](http://cf.way2muchnoise.eu/versions/multiworld-mod.svg)

多世界模組 (Multiworld Mod) - 提供建立與傳送至多個世界的功能。

<a href="https://modrinth.com/mod/multiworld/versions?l=fabric"><img src="https://cdn.modrinth.com/data/cached_images/1b54a3f3b03745c57beaa1ab11d9d86b9222a41a.png" width="160"></a>
<a href="https://modrinth.com/mod/multiworld/versions?l=neoforge"><img src="https://cdn.modrinth.com/data/cached_images/a073c4dc33587010c5b7f0386d3df9e1b0eee3ed.png" width="160"></a>
 
## 指令用法：
| 指令            | 說明                                      | 範例                                              |
|-----------------|-------------------------------------------|---------------------------------------------------|
| /mw             | 查看幫助資訊                              |                                                   |
| /mw list        | 列出所有世界                              |                                                   |
| /mw tp          | 傳送到指定世界                            | /mw tp minecraft:overworld                        |
| /mw spawn       | 傳送到目前世界的重生點                    |                                                   |
| /mw setspawn    | 設定目前世界的重生點                      |                                                   |
| /mw create      | 建立新世界                                | /mw create myLovelyWorld NORMAL -g=FLAT -s=1234   |
| /mw delete      | 刪除世界 (僅限控制台執行)                 | /mw delete myWorld                                |

#### 進階地形生成參數 (Create)
在使用 `/mw create` 指令時，您可以加入以下進階參數來高度客製化您的新世界（支援 Tab 自動補全）：
- `-g=<GENERATOR>`：設定生成器種類 (例如：`NORMAL`, `FLAT`, `VOID`)。
- `-s=<SEED>`：設定世界種子碼 (例如：`-s=1234` 或 `-s=RANDOM`)。
- `-m=<WorldSaveMode>`：設定儲存模式 (例如：`VANILLA`)。
- `-t=<TerrainType>`：設定特殊地形類型 (例如：`NORMAL`, `AMPLIFIED` [巨大化], `LARGE_BIOMES` [大型生態群系], `SINGLE_BIOME_SURFACE` [單一生物群系])。
- `-mod-terrain=<true|false>`：是否允許其他模組干涉並修改此世界地形。
- `-mod-biomes=<true|false>`：是否允許其他模組干涉並加入自訂生物群系。

**建立進階地形範例：**
`/mw create myWorld NORMAL -t=AMPLIFIED -mod-terrain=false -mod-biomes=true`

#### 遊戲規則與難度
| 指令            | 說明                                      | 範例                                |
|-----------------|-------------------------------------------|-------------------------------------|
| /mw gamerule    | 支援自訂世界的遊戲規則 (Gamerule) 設定    | /mw gamerule doDaylightCycle false  |
| /mw difficulty  | 設定目前世界的難度                        | /mw difficulty EASY                 |

## 傳送門 (Portals) <img src="https://static.wikia.nocookie.net/minecraft_gamepedia/images/0/03/Nether_portal_%28animated%29.png/revision/latest?cb=20191114182303" width="128" float="right" align="right">
最新版本的多世界模組引進了傳送門功能。
傳送門可以導向一個目標 (Destination)，目標可以是另一個世界 *(`myWorld`)*、另一個傳送門 *(`p:myOtherPortal`)*，或者是絕對座標 *(`w:myWorld:0,0,0`)*。

若要建立傳送門，請使用「傳送門法杖 (Portal Wand)」，可透過指令 *`/mw portal wand`* 取得。就像使用 WorldEdit 一樣，手持法杖時點擊滑鼠「左鍵」與「右鍵」來選取傳送門外框的兩個對角。選取完畢後，即可透過傳送門建立指令來生成傳送門。

### 傳送門指令
(待完善)
| 指令              | 說明                                      | 範例                                              |
|-------------------|-------------------------------------------|---------------------------------------------------|
| /mw portal        | 查看幫助資訊                              |                                                   |
| /mw portal create | 依據法杖選取的範圍建立新的傳送門          | /mw portal create myPortal myWorld                |
| /mw portal wand   | 取得傳送門法杖，用來選取傳送門範圍        | 選取傳送門黑曜石框架的兩個對角                  |

## 權限系統 (Permissions)

多世界模組支援 LuckPerms 或 CyberPerms。
擁有 `multiworld.admin` 權限或身為 `/op` 管理員可使用所有指令。

| 指令 | 權限節點 |
|------|----------|
| /mw  | multiworld.cmd |
| /mw tp | multiworld.tp |
| /mw spawn | multiworld.spawn |
| /mw setspawn | multiworld.setspawn |
| /mw create \<id> \<dim> [-g=GENERATOR -s=SEED] | multiworld.create |
| /mw gamerule | multiworld.gamerule |
以及其他以此類推...
 
## 即將推出

- 刪除世界的指令
- 支援自訂生成器
- 支援自訂傳送門

## 授權與致謝 (License & Credits)

多世界模組依據 [LGPL v3](LICENSE) 條款授權。

備註：多世界模組使用了 NucleoidMC 團隊的 Fantasy 函式庫來建立動態世界（同樣為 LGPLv3 授權）。

在 Forge 版本中，使用了一小段來自 [Fabric Dimensions v1](https://github.com/FabricMC/fabric/blob/1.18/fabric-dimensions-v1/src/main/java/net/fabricmc/fabric/impl/dimension/FabricDimensionInternals.java#L45) 的程式碼，該部分依據 Apache License v2.0 條款授權。
