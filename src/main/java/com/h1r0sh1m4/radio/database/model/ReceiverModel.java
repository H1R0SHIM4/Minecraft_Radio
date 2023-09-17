package com.h1r0sh1m4.radio.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import de.maxhenkel.voicechat.api.Position;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

@DatabaseTable(tableName = "receiver")
public class ReceiverModel {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private double frequency;

    @DatabaseField
    private int x;

    @DatabaseField
    private int y;

    @DatabaseField
    private int z;

    @DatabaseField
    private String world;


    public ReceiverModel(double frequency, Location location) {
        this.frequency = frequency;
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.world = location.getWorld().getName();
    }


    public ReceiverModel() {

    }

    public int getId() {
        return id;
    }

    public double getFrequency() {
        return frequency;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public World getWorld() {
        return Bukkit.getWorld(world);
    }

    public Block getBlock() {
        return getWorld().getBlockAt(getX(),getY(),getZ());
    }
    public Position getPosition(VoicechatServerApi api){
        return api.createPosition(getX(),getY(),getZ());
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

}
