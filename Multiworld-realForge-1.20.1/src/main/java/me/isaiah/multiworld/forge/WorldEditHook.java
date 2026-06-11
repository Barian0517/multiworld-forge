package me.isaiah.multiworld.forge;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.forge.ForgeAdapter;
import com.sk89q.worldedit.regions.Region;
import me.isaiah.multiworld.Utils;
import me.isaiah.multiworld.config.FileConfiguration;
import multiworld.api.WorldFolderMode;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = "multiworld")
public class WorldEditHook {

    @SubscribeEvent
    public static void onCommand(CommandEvent event) {
        String cmd = event.getParseResults().getReader().getString();
        
        if (cmd.startsWith("/regen") || cmd.startsWith("//regen") || cmd.startsWith("/worldedit:regen") || cmd.startsWith("worldedit:regen") || cmd.startsWith("regen") || cmd.startsWith("/we:regen")) {
            if (event.getParseResults().getContext().getSource().getPlayer() != null) {
                ServerPlayerEntity player = event.getParseResults().getContext().getSource().getPlayer();
                ServerWorld world = player.getServerWorld();
                
                String dim = world.getRegistryKey().getValue().toString();
                
                if (dim.startsWith("multiworld:")) {
                    event.setCanceled(true);
                    
                    Path pDir = Utils.getWorldDirectory(world.getRegistryKey().getValue(), WorldFolderMode.VANILLA);
                    File wc = pDir.resolve(Utils.WORLD_YML_NAME).toFile();
                    boolean modTerrain = false;
                    
                    if (wc.exists()) {
                        try {
                            FileConfiguration config = new FileConfiguration(wc);
                            if (config.is_set("mod_terrain")) {
                                modTerrain = config.getBoolean("mod_terrain");
                            }
                        } catch (Exception e) {}
                    }
                    
                    try {
                        LocalSession session = WorldEdit.getInstance().getSessionManager().get(ForgeAdapter.adaptPlayer(player));
                        Region region = session.getSelection(ForgeAdapter.adapt(world));
                        
                        CustomRegen.regenerate(player, world, region, session);
                    } catch (IncompleteRegionException e) {
                        player.sendMessage(Text.literal("§c請先選取一個區域！"), false);
                    } catch (Exception e) {
                        player.sendMessage(Text.literal("§c無法讀取 WorldEdit 選取區域: " + e.getMessage()), false);
                    }
                }
            }
        }
    }
}

