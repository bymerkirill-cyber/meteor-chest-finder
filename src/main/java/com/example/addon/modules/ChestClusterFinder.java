package com.example.addon.modules;

import org.meteorclient.systems.modules.Module;
import org.meteorclient.systems.modules.Categories;
import org.meteorclient.utils.player.ChatUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChestClusterFinder extends Module {
    private final int CLUSTER_THRESHOLD = 5; 
    private final int RADIUS = 4;            
    private final Set<BlockPos> reportedClusters = new HashSet<>();

    public ChestClusterFinder() {
        super(Categories.Render, "chest-cluster-finder", "Ищет большие скопления сундуков.");
    }

    @Override
    public void onActivate() {
        reportedClusters.clear();
    }

    @Override
    public void onTick() {
        if (mc.world == null || mc.player == null) return;

        List<BlockPos> chests = new ArrayList<>();

        for (BlockEntity blockEntity : mc.world.blockEntities) {
            if (blockEntity instanceof ChestBlockEntity) {
                chests.add(blockEntity.getPos());
            }
        }

        for (BlockPos chest : chests) {
            if (isAlreadyReported(chest)) continue;

            int count = 0;
            for (BlockPos otherChest : chests) {
                if (chest.isWithinDistance(otherChest, RADIUS)) {
                    count++;
                }
            }

            if (count >= CLUSTER_THRESHOLD) {
                ChatUtils.info(" Найдено скопление сундуков (" + count + " шт.) в координатах: X: " 
                        + chest.getX() + " Y: " + chest.getY() + " Z: " + chest.getZ());
                markClusterAsReported(chest, RADIUS);
            }
        }
    }

    private boolean isAlreadyReported(BlockPos pos) {
        for (BlockPos reported : reportedClusters) {
            if (pos.isWithinDistance(reported, RADIUS)) return true;
        }
        return false;
    }

    private void markClusterAsReported(BlockPos pos, int radius) {
        reportedClusters.add(pos);
    }
}
