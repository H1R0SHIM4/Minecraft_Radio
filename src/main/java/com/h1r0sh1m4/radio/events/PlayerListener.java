package com.h1r0sh1m4.radio.events;

import com.h1r0sh1m4.radio.database.DatabaseManager;
import com.h1r0sh1m4.radio.database.model.ReceiverModel;
import com.h1r0sh1m4.radio.database.model.StationModel;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {
    private final Map<UUID, BossBar> playerBossBars = new HashMap<>();
    private final DatabaseManager dbManager;
    private final FileConfiguration fileConfiguration;

    public PlayerListener(DatabaseManager dbManager, FileConfiguration fileConfiguration) {
        this.dbManager = dbManager;
        this.fileConfiguration = fileConfiguration;
    }

    @EventHandler
    public void onLeverUsing(PlayerInteractEvent event){

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        assert block != null;

        if(block.getLocation().subtract(0,1,0).getBlock().getType() != Material.JUKEBOX || block.getType() != Material.LEVER){
            return;
        }
        event.setCancelled(true);
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block targetBlock = player.getTargetBlock(null, 5);

        if (targetBlock.getType() != Material.JUKEBOX) {
            removeBossBar(event.getPlayer());
        }

    }


    @EventHandler
    public void onSneakStop(PlayerToggleSneakEvent event) {
        if (!event.isSneaking() || !event.getPlayer().getTargetBlock(null, 5).getType().equals(Material.JUKEBOX)) {
            removeBossBar(event.getPlayer());
        }
    }


    @EventHandler
    public void onMouseWheelScrolling(PlayerItemHeldEvent event) {
        double frequency;
        Player player = event.getPlayer();
        Block targetBlock = player.getTargetBlock(null,5);
        StationModel stationModel = null;
        ReceiverModel receiverModel = null;
        if(targetBlock.getType() != Material.JUKEBOX || !player.isSneaking()) return;
        player.getInventory().setHeldItemSlot(4);
        event.setCancelled(true);
        if(targetBlock.getLocation().add(0,1,0).getBlock().getType().equals(Material.LEVER)) {
            stationModel = dbManager.getStation(targetBlock.getLocation());
            if(stationModel == null){
                addBlockToDatabase(targetBlock);
                stationModel = dbManager.getStation(targetBlock.getLocation());
            }
            frequency = stationModel.getFrequency();
        }
        else {
            receiverModel = dbManager.getReceiver(targetBlock.getLocation());
            if(receiverModel == null){
                addBlockToDatabase(targetBlock);
                receiverModel = dbManager.getReceiver(targetBlock.getLocation());
            }
            frequency = receiverModel.getFrequency();
        }
        int previousSlot = event.getPreviousSlot();
        int newSlot = event.getNewSlot();
        if (newSlot > previousSlot || (newSlot == 0 && previousSlot == 8)) {
            frequency += 0.1;
        }
        else if (newSlot < previousSlot || (newSlot == 8 && previousSlot == 0)) {
            frequency -= 0.1;
        }
        frequency = Math.max(87.5, frequency);
        frequency = Math.min(108.0, frequency);
        frequency = Math.round(frequency * 10.0) / 10.0;

        BossBar bossBar = playerBossBars.get(player.getUniqueId());
        if (bossBar == null) {
            bossBar = Bukkit.createBossBar("Волна: " + frequency, BarColor.valueOf(fileConfiguration.getString("bar-color")), BarStyle.SOLID);
            bossBar.addPlayer(player);
            playerBossBars.put(player.getUniqueId(), bossBar);
        }
        else bossBar.setTitle("Волна: " + frequency);

        bossBar.setProgress((frequency - 87.5) / 20.5);

        if(targetBlock.getLocation().add(0,1,0).getBlock().getType().equals(Material.LEVER)) {
            stationModel.setFrequency(frequency);
            dbManager.saveStation(stationModel);
        }
        else {
            receiverModel.setFrequency(frequency);
            dbManager.saveReceiver(receiverModel);
        }

    }


    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent event){
        addBlockToDatabase(event.getBlock());
    }


    public void removeBossBar(Player player){
        BossBar bossbar = playerBossBars.get(player.getUniqueId());
        if(bossbar != null){
            bossbar.removePlayer(player);
            playerBossBars.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(!event.getBlock().getType().equals(Material.JUKEBOX) && !event.getBlock().getType().equals(Material.LEVER)) return;

        if(event.getBlock().getType().equals(Material.LEVER) && event.getBlock().getLocation().subtract(0,1,0).getBlock().getType().equals(Material.JUKEBOX)) {
            StationModel stationModel = dbManager.getStation(event.getBlock().getLocation().subtract(0,1,0));
            ReceiverModel receiverModel = new ReceiverModel(stationModel.getFrequency(), stationModel.getLocation());
            dbManager.removeStation(stationModel);
            dbManager.saveReceiver(receiverModel);
            return;
        }

        if(dbManager.getReceiver(event.getBlock().getLocation()) != null) {
            dbManager.removeReceiver(dbManager.getReceiver(event.getBlock().getLocation()));
            return;
        }
        if(dbManager.getStation(event.getBlock().getLocation()) != null) {
            dbManager.removeStation(dbManager.getStation(event.getBlock().getLocation()));
        }

    }
    public void addBlockToDatabase(Block block) {
        if(!block.getType().equals(Material.JUKEBOX) && !block.getType().equals(Material.LEVER)) return;

        if(block.getLocation().subtract(0,1,0).getBlock().getType().equals(Material.JUKEBOX) && block.getType().equals(Material.LEVER)) {
                ReceiverModel receiverModel = dbManager.getReceiver(block.getLocation().subtract(0, 1, 0));
                StationModel stationModel = new StationModel(receiverModel.getFrequency(), block.getLocation().subtract(0, 1, 0).getBlock().getLocation());
                dbManager.saveStation(stationModel);
                dbManager.removeReceiver(receiverModel);
                return;
        }
        if(block.getLocation().add(0,1,0 ).getBlock().getType().equals(Material.LEVER) && block.getType().equals(Material.JUKEBOX)) {
            StationModel stationModel = new StationModel(87.5, block.getLocation().getBlock().getLocation());
            dbManager.saveStation(stationModel);
        }
        else{
            ReceiverModel receiverModel = new ReceiverModel(87.5, block.getLocation());
            dbManager.saveReceiver(receiverModel);
        }
    }


}

