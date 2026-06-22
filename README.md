<img src="https://cdn.modrinth.com/data/cached_images/01d4b3f0a8d469b8d7b36030f2039007500b00f4.png" align="right" height="64">

# Multiworld Unofficial Forge Port ![](http://cf.way2muchnoise.eu/multiworld-mod.svg) ![](http://cf.way2muchnoise.eu/versions/multiworld-mod.svg)

Multiworld Unofficial Forge Port 提供了一個簡單強大的方式，讓您能在同一個 Minecraft 伺服器中建立、管理及傳送至多個自訂世界（維度）。

<a href="https://modrinth.com/mod/multiworld/versions?l=fabric"><img src="https://cdn.modrinth.com/data/cached_images/1b54a3f3b03745c57beaa1ab11d9d86b9222a41a.png" width="160"></a>
<a href="https://modrinth.com/mod/multiworld/versions?l=neoforge"><img src="https://cdn.modrinth.com/data/cached_images/a073c4dc33587010c5b7f0386d3df9e1b0eee3ed.png" width="160"></a>
 
## ✨ 主要功能 (Features)

- **無縫多世界管理**：隨時建立與刪除新世界，支援自訂種子碼與地形。
- **支援進階參數補全 (🔥 本分支強化)**：建立指令完整支援 Tab 自動補全（Auto-Completion），不用死記參數。
- **自訂規則**：每個世界都可以有獨立的遊戲規則 (Gamerule) 與遊戲難度。
- **傳送門系統**：內建傳送門魔杖，讓您能選取兩點並創建跨越世界的傳送門。
- **WorldEdit 深度整合 (🔥 本分支新增)**：修復了 WorldEdit `//regen` 在模組自訂世界中容易卡死的問題，並原生支援 `//undo` 復原功能。

---

## 📜 基礎指令用法

| 指令            | 說明                                      | 範例                                              |
|-----------------|-------------------------------------------|---------------------------------------------------|
| `/mw`             | 查看所有可用指令與幫助資訊                  |                                                   |
| `/mw list`        | 列出伺服器中存在的所有世界                  |                                                   |
| `/mw tp`          | 傳送到指定的世界                            | `/mw tp minecraft:overworld`                      |
| `/mw spawn`       | 傳送到您目前所在世界的重生點                |                                                   |
| `/mw setspawn`    | 設定目前所在世界的預設重生點                |                                                   |
| `/mw create`      | 建立新世界 (支援多種參數設定)               | `/mw create myWorld NORMAL -g=FLAT -s=1234`       |
| `/mw delete`      | 刪除指定的世界 (為安全起見，僅限控制台執行) | `/mw delete myWorld`                              |

### 遊戲規則與難度管理
您可以在不同的世界設定不同的遊戲規則，互不干擾：
| 指令            | 說明                                      | 範例                                |
|-----------------|-------------------------------------------|-------------------------------------|
| `/mw gamerule`  | 修改目前所在世界的遊戲規則 (Gamerule)       | `/mw gamerule doDaylightCycle false`|
| `/mw difficulty`| 修改目前所在世界的難度                      | `/mw difficulty EASY`               |

---

## 🌍 進階地形生成參數 (Create) 🌟 *(本分支強化)*

在使用 `/mw create` 指令時，您可以加上各種進階參數來客製化地形。（所有參數均支援 **Tab 鍵自動補全**）：

- `-g=<GENERATOR>`：設定生成器種類。預設支援：`NORMAL`, `FLAT`, `VOID`。
- `-s=<SEED>`：設定該世界的種子碼。可以指定數字（如 `-s=1234`）或隨機（`-s=RANDOM`）。
- `-m=<WorldSaveMode>`：設定儲存模式（如 `VANILLA`）。
- `-t=<TerrainType>`：設定特殊地形類型。支援：`NORMAL`、`AMPLIFIED` (巨大化)、`LARGE_BIOMES` (大型生態群系)、`SINGLE_BIOME_SURFACE` (單一生態群系)。
- `-mod-terrain=<true|false>`：是否允許其他模組干涉並修改此世界的地形。
- `-mod-biomes=<true|false>`：是否允許其他模組干涉並加入自訂的生物群系。

**建立進階地形範例：**
```
/mw create myWorld NORMAL -t=AMPLIFIED -mod-terrain=false -mod-biomes=true
```

---

## 🚪 傳送門系統 (Portals)

多世界模組內建傳送門功能，可以讓玩家透過實體傳送門跨越維度：
1. 目標可以是另一個世界（例如：`myWorld`）
2. 或是另一個已建立的傳送門（例如：`p:myOtherPortal`）
3. 或者是絕對座標（例如：`w:myWorld:0,0,0`）

### 如何建立傳送門？
1. 使用 `/mw portal wand` 取得**傳送門法杖** (Portal Wand)。
2. 拿著法杖，對著傳送門外框的左上角點擊**左鍵**，右下角點擊**右鍵**，以選取範圍（就像使用 WorldEdit 一樣）。
3. 範圍選取完畢後，輸入 `/mw portal create <傳送門名稱> <目標>` 即可生成！

| 指令              | 說明                                      | 範例                                              |
|-------------------|-------------------------------------------|---------------------------------------------------|
| `/mw portal`        | 查看傳送門幫助資訊                        |                                                   |
| `/mw portal wand`   | 取得傳送門法杖，用來選取傳送門範圍        |                                                   |
| `/mw portal create` | 依據法杖選取的範圍建立新的傳送門          | `/mw portal create myPortal myWorld`              |

---

## 🛠️ WorldEdit 深度整合與注意事項 (WorldEdit Support) 🔥 *(本分支獨家)*

我們對伺服器中常用的 **WorldEdit** 模組進行了深度整合，解決了過去常見的相容性問題：

### 非同步地形重生 (`//regen`)
過去在自訂維度中使用 `//regen` 容易導致主執行緒死結（伺服器卡死、跳出 Can't keep up 警告）。
現在，當您執行 `//regen` 時：
- 模組會自動攔截請求，並在背景以**非同步 (Async)** 的方式重新計算該區塊的原始地形、噪音圖與植被。
- 計算完畢後，會安全地回到主執行緒進行區塊替換，徹底解決了伺服器卡頓崩潰與其他模組建築亂入（地形污染）的問題。
- **請注意**：背景生成可能需要幾秒鐘的時間。請靜候聊天室出現 `§a重建完成！ (已支援 Undo)` 的綠字提示，在此之前請勿離開伺服器或修改該區域。

### 原生復原支援 (`//undo`)
我們將非同步的區塊重建完美整合進了 WorldEdit 原生的 `EditSession` 歷史紀錄系統。
如果重建出來的地形不符預期，您隨時可以直接輸入 `//undo` 將地形一秒還原，完全不留痕跡！

### 客戶端指令攔截
如果您在客戶端安裝了 WorldEdit（例如 Forge 用戶端版），客戶端可能會偷偷將 `//regen` 攔截並轉化為 `worldedit:regen` 發送給伺服器。別擔心，我們的模組已經監聽了所有可能被 WorldEdit 轉換的別名，不管您用什麼方式觸發，都能享有安全、非同步的重建體驗。

---

## 🛡️ 權限系統 (Permissions)

Multiworld Unofficial Forge Port支援 LuckPerms 或 CyberPerms。
擁有 `multiworld.admin` 權限或身為 OP 的管理員可使用所有指令。

| 指令 | 權限節點 |
|------|----------|
| `/mw`  | `multiworld.cmd` |
| `/mw tp` | `multiworld.tp` |
| `/mw spawn` | `multiworld.spawn` |
| `/mw setspawn` | `multiworld.setspawn` |
| `/mw create` | `multiworld.create` |
| `/mw gamerule` | `multiworld.gamerule` |
*(其餘指令均以此類推...)*

---

## 📌 即將推出

- 支援自訂生成器註冊 API
- 支援更豐富的自訂傳送門特效

---

## 📄 授權與致謝 (License & Credits)

Multiworld Unofficial Forge Port依據 [LGPL v3](LICENSE) 條款開源授權。

- 模組底層使用了 NucleoidMC 團隊的 Fantasy 函式庫來建立動態世界（LGPLv3）。
- Forge 版本中，使用了一小段來自 [Fabric Dimensions v1](https://github.com/FabricMC/fabric/blob/1.18/fabric-dimensions-v1/src/main/java/net/fabricmc/fabric/impl/dimension/FabricDimensionInternals.java#L45) 的程式碼，該部分依據 Apache License v2.0 條款授權。
