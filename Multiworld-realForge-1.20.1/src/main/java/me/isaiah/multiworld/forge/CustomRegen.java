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

    public static void regenerate(ServerPlayerEntity player, ServerWorld world, Region region) {
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

        for (ChunkPos pos : chunks) {
            try {
                ProtoChunk newChunk = new ProtoChunk(pos, UpgradeData.NO_UPGRADE_DATA, world, world.getRegistryManager().get(RegistryKeys.BIOME), null);
                
                int radius = 1;
                List<Chunk> neighbors = new ArrayList<>();
                for (int cz = pos.z - radius; cz <= pos.z + radius; cz++) {
                    for (int cx = pos.x - radius; cx <= pos.x + radius; cx++) {
                        if (cx == pos.x && cz == pos.z) {
                            neighbors.add(newChunk);
                        } else {
                            neighbors.add(world.getChunk(cx, cz, ChunkStatus.EMPTY, true));
                        }
                    }
                }
                
                Class<?> chunkRegionClass = Class.forName("net.minecraft.world.ChunkRegion");
                Constructor<?> constructor = chunkRegionClass.getConstructors()[0];
                Object chunkRegion = constructor.newInstance(world, neighbors, ChunkStatus.FEATURES, radius);
                
                // Reflection to invoke buildSurface and carve
                for (Method m : ChunkGenerator.class.getDeclaredMethods()) {
                    if (m.getName().equals("buildSurface") || m.getName().equals("method_12102")) {
                        m.invoke(generator, chunkRegion, world.getStructureAccessor(), world.getChunkManager().getNoiseConfig(), newChunk);
                    } else if ((m.getName().equals("carve") || m.getName().equals("method_12108")) && m.getParameterCount() == 7) {
                        m.invoke(generator, chunkRegion, world.getSeed(), world.getChunkManager().getNoiseConfig(), world.getBiomeAccess(), world.getStructureAccessor(), newChunk, GenerationStep.Carver.AIR);
                        m.invoke(generator, chunkRegion, world.getSeed(), world.getChunkManager().getNoiseConfig(), world.getBiomeAccess(), world.getStructureAccessor(), newChunk, GenerationStep.Carver.LIQUID);
                    }
                }
                
                generator.generateFeatures(world, newChunk, world.getStructureAccessor());
                
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
                                    world.setBlockState(bp, newState, 2 | 16); 
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to regen chunk " + pos);
                e.printStackTrace();
            }
        }
        
        player.sendMessage(Text.literal("§a重建完成！"), false);
    }
}
