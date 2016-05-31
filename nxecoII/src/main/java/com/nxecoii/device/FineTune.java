package com.nxecoii.device;

public class FineTune {

    private int adjust;
    /*
    0 by auto;
    1 by manual;
     */
    private int autoOrManual;

    public int getAdjust() {
        return adjust;
    }

    public void setAdjust(int adjust) {
        this.adjust = adjust;
    }

    public int getAutoOrManual() {
        return autoOrManual;
    }

    public void setAutoOrManual(int autoOrManual) {
        this.autoOrManual = autoOrManual;
    }
}
