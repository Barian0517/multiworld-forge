<img src="https://cdn.modrinth.com/data/cached_images/01d4b3f0a8d469b8d7b36030f2039007500b00f4.png" align="right" height="64">

# Multiworld Unofficial Forge Port ![](http://cf.way2muchnoise.eu/multiworld-mod.svg) ![](http://cf.way2muchnoise.eu/versions/multiworld-mod.svg)

非官方的 Multiworld (多世界模組) Forge 移植版。 為伺服器加入建立多個自訂世界與傳送功能，並搭載高度自訂生成參數與深度 WorldEdit 整合等專屬強化功能！

### 🔥 本分支專屬強化功能 (Exclusive Enhancements)
本移植版移除了對fabric的依賴(原先可能與信雅互聯相關內容衝突)，我們還大幅強化了創建世界的自由度，並解決了worldedit重新生成時破壞子世界地形的問題：

#### 🔥全新的地形自訂參數：完全掌握新世界的生成細節！包含：
* **模組隔離設定**：提供 `-mod-terrain`（是否允許模組修改地形）與 `-mod-biomes`（是否允許模組加入生態系）參數，讓您能強制隔離其他模組的干擾，打造不受模組干擾的專屬世界。
* **多樣化特殊地形**：內建支援多種原版特殊地形，包含：超平坦 (`FLAT`)、虛空 (`VOID`)、巨大化 (`AMPLIFIED`)、大型生物群系 (`LARGE_BIOMES`) 及單一生態群系 (`SINGLE_BIOME_SURFACE`) 等。

#### 🔥WorldEdit 支持:
* **重寫regen**：當與worldedit同時使用時，如果服務器安裝了修改地形的模組，即使隔絕了模組地形干擾，在使用 `//regen` 時也會導致"忽略模組地形/生態系干擾"失效，為此我們重寫了一套regen。在自訂世界中使用 WorldEdit 重新生成區塊時，會攔截命令並使用自身重寫regen來確保地形生成符合子世界生成設定。
* **銜接原生 Undo 支援**：所有的地形重建都已完美整合進 WorldEdit 的歷史紀錄系統中。如果不滿意重建結果，仍可使用 `//undo` 還原地貌。

---

### ✨ 原版核心功能 (Core Features)
* **建立多重世界**：支援各種不同的生成器、地形種類與獨立的種子碼。
* **便利的傳送系統**：透過簡單的指令在各個世界間快速移動。
* **自訂實體傳送門**：親手打造能連向其他世界、其他傳送門或絕對座標的傳送門。
* **獨立世界設定**：每個自訂維度皆可設定獨立的遊戲規則 (Gamerule) 與難度。

---

### 📜 指令用法 (🔥含此分支新增功能)
* **列出所有世界**：`/mw list`
* **建立新世界**：`/mw create <名稱> <維度類型> [-g=生成器 -s=種子碼 -t=地形類型 -mod-terrain=布林值 -mod-biomes=布林值]`
    * 範例：`/mw create testworld NORMAL`
    * 範例：`/mw create flatworld NORMAL -g=FLAT -s=1234`
    * 範例：`/mw create bigworld NORMAL -t=AMPLIFIED -mod-terrain=false` (巨大化地形，且禁止其他模組干涉地形)
* **傳送至世界**：`/mw tp <名稱>`
    * 範例：`/mw tp minecraft:overworld`
    * 範例（後台執行）：`/mw tp overworld Player52`
* **設定遊戲規則**：`/mw gamerule doDaylightCycle false`
* **設定世界難度**：`/mw difficulty EASY`
* **刪除世界（僅限後台執行）**：`/mw delete <名稱>`

---

### 🚪 自訂傳送門
使用 `/mw portal wand` 指令取得傳送門法杖 (Portal Wand)。就像使用 WorldEdit 一樣，對著傳送門外框的左上角點擊左鍵、右下角點擊右鍵以選取範圍，最後輸入 `/mw portal create <傳送門名稱> <目標>` 即可將它連結到另一個世界或傳送門！

---

### 🛡️ 權限系統 (Permissions)
本模組支援如 LuckPerms 或 CyberPerms 等權限管理插件。擁有 `multiworld.admin` 權限或身為 OP 的管理員可使用所有指令。

* `/mw` - `multiworld.cmd` (所有子指令的基礎權限)
* `/mw tp` - `multiworld.tp`
* `/mw spawn` - `multiworld.spawn`
* `/mw setspawn` - `multiworld.setspawn`
* `/mw create` - `multiworld.create`
* `/mw gamerule` - `multiworld.gamerule`
* `/mw difficulty` - `multiworld.difficulty`
* `/mw delete` - 僅限伺服器後台執行的指令

---

### 📂 世界存檔位置
Multiworld 會將自訂世界存放在原版存檔的結構中。您新增的世界將會被儲存於主世界的維度資料夾內：`world\dimensions\<命名空間>\<世界名稱>`。

---

### 📄 授權與致謝 (Credits & License)
此為非官方的 Forge 移植版。本專案依據 [LGPL v3](LICENSE) 條款開源授權。
