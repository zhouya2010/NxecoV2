package com.nxecoii.schedule;

import android.content.Context;

import com.example.nxecoii.R;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;


public class DaysOfWeek {

    private static int[] DAY_MAP = new int[] {
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
            Calendar.SUNDAY,
    };

    // Bitmask of all repeating days
    private int mDays;

    public DaysOfWeek(int days) {
        mDays = days;
    }

    public String toString(Context context, boolean showNever) {
        StringBuilder ret = new StringBuilder();

        // no days
        if (mDays == 0) {
            return showNever ?
                    context.getText(R.string.never).toString() : "";
        }

        // every day
        if (mDays == 0x7f) {
            return context.getText(R.string.every_day).toString();
        }

        // count selected days
        int dayCount = 0, days = mDays;
        while (days > 0) {
            if ((days & 1) == 1) dayCount++;
            days >>= 1;
        }

        // short or long form?
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.ENGLISH);
        String[] dayList = (dayCount > 1) ?
                dfs.getShortWeekdays() :
                dfs.getWeekdays();

        // selected days
        for (int i = 0; i < 7; i++) {
            if ((mDays & (1 << i)) != 0) {
                ret.append(dayList[DAY_MAP[i]]);
                dayCount -= 1;
                if (dayCount > 0) ret.append(
                        context.getText(R.string.day_concat));
            }
        }
        return ret.toString();
    }

    private boolean isSet(int day) {
        return ((mDays & (1 << day)) > 0);
    }

    public void set(int day, boolean set) {
        if (set) {
            mDays |= (1 << day);
        } else {
            mDays &= ~(1 << day);
        }
    }

    public void set(DaysOfWeek dow) {
        mDays = dow.mDays;
    }

    public void set(int days) {
        mDays = days;
    }
    // Returns if today is set.
    public boolean isToday(int calendarDayOfWeek){

        for(int i = 0; i < 7; i++){
            if(calendarDayOfWeek == DAY_MAP[i]){
                return isSet(i);
            }
        }

        return false;
    }

    public int getCoded() {
        return mDays;
    }

    public String toNumberString() {
        StringBuilder ret = new StringBuilder();

        int dayCount = 0, days = mDays;
        while (days > 0) {
            if ((days & 1) == 1) dayCount++;
            days >>= 1;
        }
        // selected days
        for (int i = 0; i < 7; i++) {
            if ((mDays & (1 << i)) != 0) {
                if( i != 6) {
                    ret.append(""+ (i+1));
                }
                else {
                    ret.append(""+ 0);
                }
                dayCount -= 1;
                if (dayCount > 0) ret.append(",");
            }
        }

        return ret.toString();
    }

    // Returns days of week encoded in an array of booleans.
    public boolean[] getBooleanArray() {
        boolean[] ret = new boolean[7];
        for (int i = 0; i < 7; i++) {
            ret[i] = isSet(i);
        }
        return ret;
    }

    public boolean isRepeatSet() {
        return mDays != 0;
    }

}
