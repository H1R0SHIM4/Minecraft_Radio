package com.h1r0sh1m4.radio.channels;

import com.h1r0sh1m4.radio.database.DatabaseManager;
import com.h1r0sh1m4.radio.database.model.ReceiverModel;
import com.h1r0sh1m4.radio.database.model.StationModel;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Collection;


public class VCPacketListener{
    DatabaseManager dbManager;
    VoicechatServerApi api;

    FileConfiguration fileConfiguration;

    public VCPacketListener(DatabaseManager dbManager, FileConfiguration fileConfiguration) {
        this.dbManager = dbManager;
        this.fileConfiguration = fileConfiguration;
    }


    public void onMicrophonePacket(MicrophonePacketEvent event){
        for (StationModel stationModel :dbManager.getStations()) {
            if(!Bukkit.getPlayer(event.getSenderConnection().getPlayer().getUuid()).getWorld().equals(stationModel.getWorld())){
                continue;
            }
            if(Bukkit.getPlayer(event.getSenderConnection().getPlayer().getUuid()).getLocation().distance(stationModel.getLocation()) < fileConfiguration.getInt("station-distance")) {
                for(ReceiverModel receiverModel : dbManager.getReceivers(stationModel.getFrequency())) {
                    Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                    for (Player player: players) {
                        if(!player.getWorld().equals(receiverModel.getWorld())) {
                            continue;
                        }
                        if (player.getLocation().distance(receiverModel.getBlock().getLocation()) < fileConfiguration.getInt("receiver-distance")) {
                            api = event.getVoicechat();
                            api.sendLocationalSoundPacketTo(api.getConnectionOf(player.getUniqueId()), event.getPacket().toLocationalSoundPacket(receiverModel.getPosition(api)));
                        }
                    }
                }
            }
        }
    }
}


