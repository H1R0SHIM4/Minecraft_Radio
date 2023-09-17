package com.h1r0sh1m4.radio;

import com.h1r0sh1m4.radio.channels.VCPacketListener;
import com.h1r0sh1m4.radio.database.DatabaseManager;
import com.h1r0sh1m4.radio.database.SQLiteDriver;
import com.h1r0sh1m4.radio.database.model.ReceiverModel;
import com.h1r0sh1m4.radio.database.model.StationModel;
import com.h1r0sh1m4.radio.events.*;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import de.maxhenkel.voicechat.api.*;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class Plugin extends JavaPlugin implements VoicechatPlugin {
    SQLiteDriver sqliteDriver;
    ConnectionSource connectionSource;
    DatabaseManager dbManager;

    @Override
    public String getPluginId() {
        return "h1r0shim4_radio";
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        BukkitVoicechatService service = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (service != null) {
            service.registerPlugin(this);
        }
        else {
            getLogger().warning("Plugin \"Simple Voice Chat\" doesn't found! Goodbye!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        sqliteDriver = new SQLiteDriver();
        connectionSource  = sqliteDriver.getConnection(this);

        try {
            TableUtils.createTableIfNotExists(connectionSource, ReceiverModel.class);
            TableUtils.createTableIfNotExists(connectionSource, StationModel.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        dbManager = new DatabaseManager(connectionSource);

        getServer().getPluginManager().registerEvents(new PlayerListener(dbManager, this.getConfig()), this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public void initialize(VoicechatApi api) {
        VoicechatPlugin.super.initialize(api);

    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, new VCPacketListener(dbManager,this.getConfig())::onMicrophonePacket);
    }
}
