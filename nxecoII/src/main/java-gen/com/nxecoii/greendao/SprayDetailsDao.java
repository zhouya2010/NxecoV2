package com.nxecoii.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.nxecoii.greendao.SprayDetails;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SPRAY_DETAILS".
*/
public class SprayDetailsDao extends AbstractDao<SprayDetails, Long> {

    public static final String TABLENAME = "SPRAY_DETAILS";

    /**
     * Properties of entity SprayDetails.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Zone = new Property(1, Integer.class, "zone", false, "ZONE");
        public final static Property Adjust = new Property(2, Integer.class, "adjust", false, "ADJUST");
        public final static Property IntervalOrigin = new Property(3, Integer.class, "intervalOrigin", false, "INTERVAL_ORIGIN");
        public final static Property IntervalActual = new Property(4, Integer.class, "intervalActual", false, "INTERVAL_ACTUAL");
        public final static Property SprayType = new Property(5, Integer.class, "sprayType", false, "SPRAY_TYPE");
        public final static Property StopType = new Property(6, Integer.class, "stopType", false, "STOP_TYPE");
        public final static Property StartTimeTheory = new Property(7, Long.class, "startTimeTheory", false, "START_TIME_THEORY");
        public final static Property StartTimeActually = new Property(8, Long.class, "startTimeActually", false, "START_TIME_ACTUALLY");
        public final static Property EndTimeActually = new Property(9, Long.class, "endTimeActually", false, "END_TIME_ACTUALLY");
        public final static Property IsUpload = new Property(10, Boolean.class, "isUpload", false, "IS_UPLOAD");
        public final static Property IsSpraying = new Property(11, Boolean.class, "isSpraying", false, "IS_SPRAYING");
        public final static Property Add_time = new Property(12, java.util.Date.class, "add_time", false, "ADD_TIME");
    };


    public SprayDetailsDao(DaoConfig config) {
        super(config);
    }
    
    public SprayDetailsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SPRAY_DETAILS\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"ZONE\" INTEGER," + // 1: zone
                "\"ADJUST\" INTEGER," + // 2: adjust
                "\"INTERVAL_ORIGIN\" INTEGER," + // 3: intervalOrigin
                "\"INTERVAL_ACTUAL\" INTEGER," + // 4: intervalActual
                "\"SPRAY_TYPE\" INTEGER," + // 5: sprayType
                "\"STOP_TYPE\" INTEGER," + // 6: stopType
                "\"START_TIME_THEORY\" INTEGER," + // 7: startTimeTheory
                "\"START_TIME_ACTUALLY\" INTEGER," + // 8: startTimeActually
                "\"END_TIME_ACTUALLY\" INTEGER," + // 9: endTimeActually
                "\"IS_UPLOAD\" INTEGER," + // 10: isUpload
                "\"IS_SPRAYING\" INTEGER," + // 11: isSpraying
                "\"ADD_TIME\" INTEGER);"); // 12: add_time
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SPRAY_DETAILS\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, SprayDetails entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Integer zone = entity.getZone();
        if (zone != null) {
            stmt.bindLong(2, zone);
        }
 
        Integer adjust = entity.getAdjust();
        if (adjust != null) {
            stmt.bindLong(3, adjust);
        }
 
        Integer intervalOrigin = entity.getIntervalOrigin();
        if (intervalOrigin != null) {
            stmt.bindLong(4, intervalOrigin);
        }
 
        Integer intervalActual = entity.getIntervalActual();
        if (intervalActual != null) {
            stmt.bindLong(5, intervalActual);
        }
 
        Integer sprayType = entity.getSprayType();
        if (sprayType != null) {
            stmt.bindLong(6, sprayType);
        }
 
        Integer stopType = entity.getStopType();
        if (stopType != null) {
            stmt.bindLong(7, stopType);
        }
 
        Long startTimeTheory = entity.getStartTimeTheory();
        if (startTimeTheory != null) {
            stmt.bindLong(8, startTimeTheory);
        }
 
        Long startTimeActually = entity.getStartTimeActually();
        if (startTimeActually != null) {
            stmt.bindLong(9, startTimeActually);
        }
 
        Long endTimeActually = entity.getEndTimeActually();
        if (endTimeActually != null) {
            stmt.bindLong(10, endTimeActually);
        }
 
        Boolean isUpload = entity.getIsUpload();
        if (isUpload != null) {
            stmt.bindLong(11, isUpload ? 1L: 0L);
        }
 
        Boolean isSpraying = entity.getIsSpraying();
        if (isSpraying != null) {
            stmt.bindLong(12, isSpraying ? 1L: 0L);
        }
 
        java.util.Date add_time = entity.getAdd_time();
        if (add_time != null) {
            stmt.bindLong(13, add_time.getTime());
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public SprayDetails readEntity(Cursor cursor, int offset) {
        SprayDetails entity = new SprayDetails( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // zone
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // adjust
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // intervalOrigin
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // intervalActual
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // sprayType
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // stopType
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7), // startTimeTheory
            cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8), // startTimeActually
            cursor.isNull(offset + 9) ? null : cursor.getLong(offset + 9), // endTimeActually
            cursor.isNull(offset + 10) ? null : cursor.getShort(offset + 10) != 0, // isUpload
            cursor.isNull(offset + 11) ? null : cursor.getShort(offset + 11) != 0, // isSpraying
            cursor.isNull(offset + 12) ? null : new java.util.Date(cursor.getLong(offset + 12)) // add_time
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, SprayDetails entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setZone(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setAdjust(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setIntervalOrigin(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setIntervalActual(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setSprayType(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setStopType(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setStartTimeTheory(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
        entity.setStartTimeActually(cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8));
        entity.setEndTimeActually(cursor.isNull(offset + 9) ? null : cursor.getLong(offset + 9));
        entity.setIsUpload(cursor.isNull(offset + 10) ? null : cursor.getShort(offset + 10) != 0);
        entity.setIsSpraying(cursor.isNull(offset + 11) ? null : cursor.getShort(offset + 11) != 0);
        entity.setAdd_time(cursor.isNull(offset + 12) ? null : new java.util.Date(cursor.getLong(offset + 12)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(SprayDetails entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(SprayDetails entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}