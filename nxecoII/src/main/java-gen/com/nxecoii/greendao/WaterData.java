package com.nxecoii.greendao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import android.net.Uri;
// KEEP INCLUDES END
/**
 * Entity mapped to table "WATER_DATA".
 */
public class WaterData {

    private Long id;
    private Integer zone;
    private Integer adjust;
    private Integer intervalOrigin;
    private Integer intervalActual;
    private Long startTimeTheory;
    private Long startTimeActually;
    private Long endTimeActually;
    private Boolean isUpload;
    private Integer sprayType;
    private Integer stopType;
    private java.util.Date add_time;

    // KEEP FIELDS - put your custom fields here
    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.nxecoii.waterrecordprovider";
    public static final Uri URI_CONTENT_WATER = Uri.parse(SCHEME + AUTHORITY );
    // KEEP FIELDS END

    public WaterData() {
    }

    public WaterData(Long id) {
        this.id = id;
    }

    public WaterData(Long id, Integer zone, Integer adjust, Integer intervalOrigin, Integer intervalActual, Long startTimeTheory, Long startTimeActually, Long endTimeActually, Boolean isUpload, Integer sprayType, Integer stopType, java.util.Date add_time) {
        this.id = id;
        this.zone = zone;
        this.adjust = adjust;
        this.intervalOrigin = intervalOrigin;
        this.intervalActual = intervalActual;
        this.startTimeTheory = startTimeTheory;
        this.startTimeActually = startTimeActually;
        this.endTimeActually = endTimeActually;
        this.isUpload = isUpload;
        this.sprayType = sprayType;
        this.stopType = stopType;
        this.add_time = add_time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getZone() {
        return zone;
    }

    public void setZone(Integer zone) {
        this.zone = zone;
    }

    public Integer getAdjust() {
        return adjust;
    }

    public void setAdjust(Integer adjust) {
        this.adjust = adjust;
    }

    public Integer getIntervalOrigin() {
        return intervalOrigin;
    }

    public void setIntervalOrigin(Integer intervalOrigin) {
        this.intervalOrigin = intervalOrigin;
    }

    public Integer getIntervalActual() {
        return intervalActual;
    }

    public void setIntervalActual(Integer intervalActual) {
        this.intervalActual = intervalActual;
    }

    public Long getStartTimeTheory() {
        return startTimeTheory;
    }

    public void setStartTimeTheory(Long startTimeTheory) {
        this.startTimeTheory = startTimeTheory;
    }

    public Long getStartTimeActually() {
        return startTimeActually;
    }

    public void setStartTimeActually(Long startTimeActually) {
        this.startTimeActually = startTimeActually;
    }

    public Long getEndTimeActually() {
        return endTimeActually;
    }

    public void setEndTimeActually(Long endTimeActually) {
        this.endTimeActually = endTimeActually;
    }

    public Boolean getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(Boolean isUpload) {
        this.isUpload = isUpload;
    }

    public Integer getSprayType() {
        return sprayType;
    }

    public void setSprayType(Integer sprayType) {
        this.sprayType = sprayType;
    }

    public Integer getStopType() {
        return stopType;
    }

    public void setStopType(Integer stopType) {
        this.stopType = stopType;
    }

    public java.util.Date getAdd_time() {
        return add_time;
    }

    public void setAdd_time(java.util.Date add_time) {
        this.add_time = add_time;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
