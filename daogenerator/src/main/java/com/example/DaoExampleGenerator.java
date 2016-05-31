package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DaoExampleGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.nxecoii.greendao");
        addNote(schema);
        new DaoGenerator().generateAll(schema, "../NxecoV2/nxecoii/src/main/java-gen");
    }

    /**
     * @param schema
     */
    private static void addNote(Schema schema) {
        schema.enableKeepSectionsByDefault();
        Entity note = schema.addEntity("SprayDetails");
        note.addIdProperty();
        note.addIntProperty("zone");
        note.addIntProperty("adjust");
        note.addIntProperty("intervalOrigin");
        note.addIntProperty("intervalActual");
        note.addIntProperty("sprayType");
        note.addIntProperty("stopType");
        note.addLongProperty("startTimeTheory");
        note.addLongProperty("startTimeActually");
        note.addLongProperty("endTimeActually");
        note.addBooleanProperty("isUpload");
        note.addBooleanProperty("isSpraying");
        note.addDateProperty("add_time");

        Entity sch = schema.addEntity("ScheduleData");
        sch.addIdProperty();
        sch.addIntProperty("zone");
        sch.addIntProperty("repeat");
        sch.addIntProperty("interval");
        sch.addIntProperty("timeId");
        sch.addIntProperty("groupId");
        sch.addStringProperty("lable");
        sch.addStringProperty("start_time");
        sch.addBooleanProperty("isUpload");
        sch.addBooleanProperty("enable");
        sch.addDateProperty("add_time");

        Entity water = schema.addEntity("WaterData");
        water.addIdProperty();
        water.addIntProperty("zone");
        water.addIntProperty("adjust");
        water.addIntProperty("intervalOrigin");
        water.addIntProperty("intervalActual");
        water.addLongProperty("startTimeTheory");
        water.addLongProperty("startTimeActually");
        water.addLongProperty("endTimeActually");
        water.addBooleanProperty("isUpload");
        water.addIntProperty("sprayType");
        water.addIntProperty("stopType");
        water.addDateProperty("add_time");

        Entity entity = schema.addEntity("DownloadDBEntity");
        entity.setClassNameDao("DownloadDao");
        entity.setTableName("download");
        entity.addStringProperty("downloadId").primaryKey();
        entity.addLongProperty("toolSize");
        entity.addLongProperty("completedSize");
        entity.addStringProperty("url");
        entity.addStringProperty("saveDirPath");
        entity.addStringProperty("fileName");
        entity.addIntProperty("downloadStatus");
    }
}
