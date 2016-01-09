package org.bdlions.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author nazmul hasan
 */
public class DateUtils {

    public DateUtils() {

    }

    /**
     * This method will return current unix time in seconds
     *
     * @return int, current unix time
     */
    public static int getCurrentUnixTime() {
        return (int) (System.currentTimeMillis() / 1000L);
    }

    public static String getCurrentDate() {
        long unixTime = System.currentTimeMillis();
        Calendar mydate = Calendar.getInstance();
        mydate.setTimeInMillis(unixTime);
        return mydate.get(Calendar.YEAR) + "-" + (mydate.get(Calendar.MONTH) + 1) + "-" + mydate.get(Calendar.DAY_OF_MONTH);

    }

    public static String getUnixToHuman(long unixTime) {
        Calendar mydate = Calendar.getInstance();
        mydate.setTimeInMillis(unixTime * 1000);
        return mydate.get(Calendar.YEAR) + "-" + (mydate.get(Calendar.MONTH) + 1) + "-" + mydate.get(Calendar.DAY_OF_MONTH);
    }

    public static long getHumanToUnix(String humanDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(humanDate);
        long timeInMillisSinceEpoch = date.getTime();
        long timeInMinutesSinceEpoch = timeInMillisSinceEpoch / (1000L);
        return timeInMinutesSinceEpoch;
    }
}
