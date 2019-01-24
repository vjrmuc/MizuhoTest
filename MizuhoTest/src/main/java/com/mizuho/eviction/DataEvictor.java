package com.mizuho.eviction;

import com.mizuho.dal.DataAccess;

import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.sql.Timestamp;

public class DataEvictor {

    public static void main(String[] args) {
        DataEvictor evictor = new DataEvictor();
        //TO-DO: Move eviction days to configuration file
        int evctionDays = 30;
        evictor.evictData(evctionDays);
    }


    /**
     * @param evictionDays evicts instruments older than preconfigured number of days
     */
    protected void evictData(int evictionDays){
        try {
            DataAccess dao = new DataAccess();
            dao.evictDataAfterPeriod(evictionDays);
        } catch (SQLException e) {
            System.out.println("Exception has occured in the database, currently not able to evict data");
            e.printStackTrace();
        }
    }
}