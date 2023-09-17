package com.h1r0sh1m4.radio.database;

import com.h1r0sh1m4.radio.database.model.ReceiverModel;
import com.h1r0sh1m4.radio.database.model.StationModel;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import org.bukkit.Location;

import java.sql.SQLException;
import java.util.List;

public class DatabaseManager{

    private final Dao<ReceiverModel, Integer> receiverDao;
    private final Dao<StationModel, Integer> stationDao;

    public DatabaseManager(ConnectionSource connection) {
        try {
            this.receiverDao = DaoManager.createDao(connection, ReceiverModel.class);
            this.stationDao = DaoManager.createDao(connection, StationModel.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //################################################################################
    //-------------------------------------Stations-----------------------------------
    //################################################################################

    public void saveStation(StationModel stationModel) {
        try {
            stationDao.createOrUpdate(stationModel);;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeStation(StationModel stationModel) {
        try {
            stationDao.delete(stationModel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public StationModel getStation(Location location){
        try {
            return stationDao.queryBuilder().where()
                    .eq("x", location.getBlockX()).and()
                    .eq("y",location.getBlockY()).and()
                    .eq("z", location.getBlockZ()).and()
                    .eq("world", location.getWorld().getName())
                    .queryForFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<StationModel> getStations() {
        try {
            return stationDao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //################################################################################
    //-------------------------------------Receivers----------------------------------
    //################################################################################

    public List<ReceiverModel> getReceivers(double frequency){
        try {
            return receiverDao.queryBuilder().where()
                    .eq("frequency", frequency).query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public ReceiverModel getReceiver(Location location){
        try {
            return receiverDao.queryBuilder().where()
                    .eq("x", location.getBlockX()).and()
                    .eq("y",location.getBlockY()).and()
                    .eq("z", location.getBlockZ()).and()
                    .eq("world", location.getWorld().getName())
                    .queryForFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void saveReceiver(ReceiverModel receiverModel) {
        try {
            receiverDao.createOrUpdate(receiverModel);;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void removeReceiver(ReceiverModel receiverModel) {
        try {
            receiverDao.delete(receiverModel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
