package me.isaiah.multiworld.forge;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomRegen {

    public static void regenerate(ServerPlayerEntity player, ServerWorld world, Region region, com.sk89q.worldedit.LocalSession session) {
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();

        Set<ChunkPos> chunks = new HashSet<>();
        for (int x = min.getBlockX() >> 4; x <= max.getBlockX() >> 4; x++) {
            for (int z = min.getBlockZ() >> 4; z <= max.getBlockZ() >> 4; z++) {
                chunks.add(new ChunkPos(x, z));
            }
        }

        player.sendMessage(Text.literal("§a正在使用自定義生成器重建 " + chunks.size() + " 個區塊..."), false);
        
        ChunkGenerator generator = world.getChunkManager().getChunkGenerator();

        java.util.concurrent.CompletableFuture.runAsync(() -> {
            me.isaiah.multiworld.MultiworldMod.LOGGER.info("[CustomRegen] Starting background regen task for " + chunks.size() + " chunks.");
            try {
                int count = 0;
                List<ProtoChunk> generatedChunks = new ArrayList<>();
                for (ChunkPos pos : chunks) {
                    count++;
                    me.isaiah.multiworld.MultiworldMod.LOGGER.info("[CustomRegen] Regenerating chunk: " + pos + " (" + count + "/" + chunks.size() + ")");
                    try {
                        ProtoChunk newChunk = new ProtoChunk(pos, UpgradeData.NO_UPGRADE_DATA, world, world.getRegistryManager().get(RegistryKeys.BIOME), null);
                        
                        int radius = 8;
                        List<Chunk> neighbors = new ArrayList<>();
                        me.isaiah.multiworld.MultiworldMod.LOGGER.info("[CustomRegen] Gathering neighbors...");
                        for (int cz = pos.z - radius; cz <= pos.z + radius; cz++) {
                            for (int cx = pos.x - radius; cx <= pos.x + radius; cx++) {
                                if (cx == pos.x && cz == pos.z) {
                                    neighbors.add(newChunk);
                                } else {
                                    Chunk c = world.getChunk(cx, cz, ChunkStatus.EMPTY, false);
                                    if (c == null) {
                                        c = new ProtoChunk(new ChunkPos(cx, cz), UpgradeData.NO_UPGRADE_DATA, world, world.getRegistryManager().get(RegistryKeys.BIOME), null);
                                    }
                                    neighbors.add(c);
                                }
                            }
                        }
                        
                        Class<?> chunkRegionClass = net.minecraft.world.ChunkRegion.class;
                        Constructor<?> constructor = chunkRegionClass.getConstructors()[0];
                        Object chunkRegion = constructor.newInstance(world, neighbors, ChunkStatus.FEATURES, radius);
                        
                        me.isaiah.multiworld.MultiworldMod.LOGGER.info("[CustomRegen] populateBiomes...");
                        // 1. populateBiomes
                        for (Method m : ChunkGenerator.class.getDeclaredMethods()) {
                            if (m.getParameterCount() == 4 && m.getParameterTypes()[1] == Blender.class) {
                                m.invoke(generator, world.getChunkManager().getNoiseConfig(), Blender.getNoBlending(), world.getStructureAccessor(), newChunk);
                                break;
                            }
                        }
                        
                        me.isaiah.multiworld.MultiworldMod.LOGGER.info("[CustomRegen] populateNoise...");
                        // 2. populateNoise
                        for (Method m : ChunkGenerator.class.getDeclaredMethods()) {
                            if (m.getParameterCount() == 5 && m.getParameterTypes()[0] == java.util.concurrent.Executor.class && m.getParameterTypes()[1] == Blender.class) {
                                java.util.concurrent.Executor executor = Runnable::run;
                                java.util.concurrent.CompletableFuture<?> future = (java.util.concurrent.CompletableFuture<?>) m.invoke(generator, executor, Blender.getNoBlending(), world.getChunkManager().getNoiseConfig(), world.getStructureAccessor(), newChunk);
                                future.join();
                                break;
                            }
                        }

                        me.isaiah.multiworld.MultiworldMod.LOGGER.info("[CustomRegen] buildSurface and carve...");
                        // Reflection to invoke buildSurface and carve
                        for (Method m : ChunkGenerator.class.getDeclaredMethods()) {
                            if (m.getParameterCount() == 4 && m.getParameterTypes()[0].isAssignableFrom(chunkRegionClass) && m.getParameterTypes()[3] == net.minecraft.world.chunk.Chunk.class) {
                                m.invoke(generator, chunkRegion, world.getStructureAccessor(), world.getChunkManager().getNoiseConfig(), newChunk);
                            } else if (m.getParameterCount() == 7 && m.getParameterTypes()[0].isAssignableFrom(chunkRegionClass) && m.getParameterTypes()[6] == GenerationStep.Carver.class) {
                                m.invoke(generator, chunkRegion, world.getSeed(), world.getChunkManager().getNoiseConfig(), world.getBiomeAccess(), world.getStructureAccessor(), newChunk, GenerationStep.Carver.AIR);
                                m.invoke(generator, chunkRegion, world.getSeed(), world.getChunkManager().getNoiseConfig(), world.getBiomeAccess(), world.getStructureAccessor(), newChunk, GenerationStep.Carver.LIQUID);
                            }
                        }
                        
                        me.isaiah.multiworld.MultiworldMod.LOGGER.info("[CustomRegen] generateFeatures...");
                        generator.generateFeatures((net.minecraft.world.StructureWorldAccess) chunkRegion, newChunk, world.getStructureAccessor());
                        
                        generatedChunks.add(newChunk);
                        me.isaiah.multiworld.MultiworldMod.LOGGER.info("[CustomRegen] Finished chunk " + pos + " in background.");
                    } catch (Exception e) {
                        me.isaiah.multiworld.MultiworldMod.LOGGER.error("[CustomRegen] Failed to regen chunk " + pos, e);
                    }
                }
                
                me.isaiah.multiworld.MultiworldMod.LOGGER.info("[CustomRegen] Applying to EditSession on main thread...");
                world.getServer().execute(() -> {
                    try (com.sk89q.worldedit.EditSession editSession = session.createEditSession(com.sk89q.worldedit.forge.ForgeAdapter.adaptPlayer(player))) {
                        int blockCount = 0;
                        for (ProtoChunk newChunk : generatedChunks) {
                            ChunkPos pos = newChunk.getPos();
                            for (int x = 0; x < 16; x++) {
                                for (int z = 0; z < 16; z++) {
                                    int realX = pos.getStartX() + x;
                                    int realZ = pos.getStartZ() + z;
                                    
                                    if (realX >= min.getBlockX() && realX <= max.getBlockX() && realZ >= min.getBlockZ() && realZ <= max.getBlockZ()) {
                                        for (int y = world.getBottomY(); y < world.getTopY(); y++) {
                                            if (y >= min.getBlockY() && y <= max.getBlockY()) {
                                                BlockPos bp = new BlockPos(realX, y, realZ);
                                                BlockState newState = newChunk.getBlockState(bp);
                                                if (newState == null) newState = Blocks.AIR.getDefaultState();
                                                
                                                editSession.setBlock(
                                                    com.sk89q.worldedit.math.BlockVector3.at(realX, y, realZ),
                                                    com.sk89q.worldedit.forge.ForgeAdapter.adapt(newState)
                                                );
                                                blockCount++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Add to Undo History
                        session.remember(editSession);
                        me.isaiah.multiworld.MultiworldMod.LOGGER.info("[CustomRegen] Successfully applied " + blockCount + " blocks and stored EditSession to undo history.");
                        player.sendMessage(Text.literal("§a重建完成！ (已支援 Undo)"), false);
                    } catch (Exception e) {
                        me.isaiah.multiworld.MultiworldMod.LOGGER.error("[CustomRegen] Fatal error during async regen apply!", e);
                        player.sendMessage(Text.literal("§c套用重建時發生嚴重錯誤，請查看控制台！"), false);
                    }
                });
            } catch (Exception e) {
                me.isaiah.multiworld.MultiworldMod.LOGGER.error("[CustomRegen] Fatal error during async regen calculation!", e);
                world.getServer().execute(() -> {
                    player.sendMessage(Text.literal("§c計算重建時發生嚴重錯誤，請查看控制台！"), false);
                });
            }
        }, net.minecraft.util.Util.getMainWorkerExecutor()).exceptionally(e -> {
            me.isaiah.multiworld.MultiworldMod.LOGGER.error("[CustomRegen] Async execution failed completely!", e);
            return null;
        });
    }
}
