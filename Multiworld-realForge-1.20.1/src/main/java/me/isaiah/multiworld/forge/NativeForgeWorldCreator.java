package me.isaiah.multiworld.forge;

import java.util.Collections;
import java.util.Optional;
import java.lang.reflect.Field;

import me.isaiah.multiworld.ICreator;
import me.isaiah.multiworld.MultiworldMod;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.world.biome.source.BiomeAccess;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Util;
import com.mojang.serialization.Lifecycle;

public class NativeForgeWorldCreator implements ICreator {

    public static void init() {
        MultiworldMod.setICreator(new NativeForgeWorldCreator());
    }

    @Override
    public ServerWorld create_world(String id, Identifier dim, ChunkGenerator gen, Difficulty dif, long seed) {
        MinecraftServer server = MultiworldMod.mc;
        RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, new Identifier(id));
        
        // Return existing world if it exists
        ServerWorld existing = server.getWorld(worldKey);
        if (existing != null) {
            return existing;
        }

        RegistryKey<DimensionType> dimTypeKey = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, dim);
        DimensionType dimType = server.getRegistryManager().get(RegistryKeys.DIMENSION_TYPE).get(dimTypeKey);
        
        DimensionOptions options = new DimensionOptions(
            server.getRegistryManager().get(RegistryKeys.DIMENSION_TYPE).getEntry(dimTypeKey).get(),
            gen
        );

        // Natively unfreeze the registry using reflection is no longer needed
        // because we don't want to save it to level.dat! If we save it to level.dat,
        // Vanilla will load it with the overworld seed on next startup!
        // dimensionsRegistry.add(RegistryKey.of(RegistryKeys.DIMENSION, worldKey.getValue()), options, Lifecycle.stable());

        // Get LevelStorage.Session via reflection
        LevelStorage.Session session = null;
        try {
            for (Field f : MinecraftServer.class.getDeclaredFields()) {
                if (f.getType() == LevelStorage.Session.class) {
                    f.setAccessible(true);
                    session = (LevelStorage.Session) f.get(server);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ServerWorldProperties properties = new UnmodifiableLevelProperties(server.getSaveProperties(), server.getSaveProperties().getMainWorldProperties());
        
        ServerWorld world = new ServerWorld(
            server,
            Util.getMainWorkerExecutor(),
            session,
            properties,
            worldKey,
            options,
            new WorldGenerationProgressListener() {
                @Override public void start(net.minecraft.util.math.ChunkPos spawnPos) {}
                @Override public void setChunkStatus(net.minecraft.util.math.ChunkPos pos, net.minecraft.world.chunk.ChunkStatus status) {}
                @Override public void start() {}
                @Override public void stop() {}
            },
            false,
            seed,
            ImmutableList.of(),
            true,
            null
        ) {
            @Override
            public long getSeed() {
                return seed;
            }
        };

        // Inject into forge world map and server worlds map
        server.forgeGetWorldMap().put(worldKey, world);
        // Force ticking immediately
        world.tick(() -> true);
        
        return world;
    }

    @Override
    public void set_difficulty(String id, Difficulty dif) {
        // Not easily supported in basic native implementation, requires full dimension data wrapping
    }

    @Override
    public void delete_world(String id) {
        MinecraftServer server = MultiworldMod.mc;
        RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, new Identifier(id));
        ServerWorld world = server.getWorld(worldKey);
        if (world != null) {
            server.forgeGetWorldMap().remove(worldKey);
        }
    }

    @Override
    public boolean is_the_end(ServerWorld world) {
        return world.getDimensionKey() == DimensionTypes.THE_END;
    }

    @Override
    public BlockPos get_pos(double x, double y, double z) {
        return BlockPos.ofFloored(x, y, z);
    }

    @Override
    public BlockPos get_spawn(ServerWorld world) {
        net.minecraft.world.WorldProperties prop = world.getLevelProperties();
        return new BlockPos(prop.getSpawnX(), prop.getSpawnY(), prop.getSpawnZ());
    }

    @Override
    public void teleleport(ServerPlayerEntity player, ServerWorld world, double x, double y, double z) {
        player.teleport(world, x, y, z, player.getYaw(), player.getPitch());
    }

    @Override
    public ChunkGenerator get_flat_chunk_gen(MinecraftServer mc) {
        var biome = mc.getRegistryManager().get(RegistryKeys.BIOME).getEntry(mc.getRegistryManager().get(RegistryKeys.BIOME).getOrThrow(BiomeKeys.PLAINS));
        FlatChunkGeneratorConfig flat = new FlatChunkGeneratorConfig(Optional.empty(), biome, Collections.emptyList());
        return new FlatChunkGenerator(flat);
    }

    @Override
    public ChunkGenerator get_void_chunk_gen(MinecraftServer mc) {
        return this.get_flat_chunk_gen(mc);
    }

    @Override
    public boolean permissionLevel(ServerCommandSource source, int level) {
        return source.hasPermissionLevel(level);
    }

    @Override
    public boolean permissionLevel(ServerPlayerEntity plr, int level) {
        return plr.hasPermissionLevel(level);
    }
}
