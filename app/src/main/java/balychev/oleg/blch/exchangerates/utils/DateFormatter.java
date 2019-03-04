package balychev.oleg.blch.exchangerates.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateFormatter {

    private static SimpleDateFormat format = new SimpleDateFormat();

    public static String getCalendarIn_yyyyMMdd_Format(Calendar calendar) {
        format.applyPattern("yyyyMMdd");
        return format.format(calendar.getTime());
    }

    public static String getCalendarIn_ddMMyyyy_Format(Calendar calendar) {
        format.applyPattern("dd.MM.yyyy");
        return format.format(calendar.getTime());
    }

}
