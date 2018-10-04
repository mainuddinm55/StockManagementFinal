package com.kcirque.stockmanagementfinal.Common;

import android.util.Log;

import java.io.CharArrayReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateConverter {
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    private Date date = new Date();

    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    public String getDateInString(long unixDate) {
        date = new Date(unixDate * 1000L);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+6:00"));
        return dateFormat.format(date);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public long getDateInUnix(String date) {
        long unixTime = 0;
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+6:00"));
        try {
            unixTime = dateFormat.parse(date).getTime();
            unixTime = unixTime / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return unixTime;
    }

    public long getCurrentDate() {
        return getDateInUnix(day + "/" + (month + 1) + "/" + year);
    }

    public String unixToDay(long timeStamp) {
        Date dateTime = new Date((long) timeStamp * 1000);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);
        int dayInt = cal.get(Calendar.DAY_OF_WEEK);

        String dayStr = "Saturday";

        switch (dayInt) {
            case Calendar.SATURDAY:
                dayStr = "Saturday";
                break;
            case Calendar.SUNDAY:
                dayStr = "Sunday";
                break;
            case Calendar.MONDAY:
                dayStr = "Monday";
                break;
            case Calendar.TUESDAY:
                dayStr = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                dayStr = "Wednesday";
                break;
            case Calendar.THURSDAY:
                dayStr = "Thursday";
                break;
            case Calendar.FRIDAY:
                dayStr = "Friday";
                break;
        }

        return dayStr;
    }

    /*  private String unixToDate(long timestamp) {
          // convert seconds to milliseconds
          Date date = new java.util.Date(timestamp*1000L);
          // the format of your date
          SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
          String formattedDate = sdf.format(date);
          return formattedDate;
      }
  */
    public String getTimeFromUnix(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp * 1000L);

        Date d = calendar.getTime();
        String timeStr = new SimpleDateFormat("hh:mm a").format(d);

        return timeStr;
    }

    public boolean isToday(long unixDate) {
        Calendar today = Calendar.getInstance();
        Calendar specifiedDay = Calendar.getInstance();
        specifiedDay.setTimeInMillis(unixDate * 1000);
        specifiedDay.setTimeZone(TimeZone.getTimeZone("GMT+6:00"));
        if (today.get(Calendar.YEAR) == specifiedDay.get(Calendar.YEAR)
                && today.get(Calendar.MONTH) == specifiedDay.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) == specifiedDay.get(Calendar.DAY_OF_MONTH)) {
            return true;
        } else {
            return false;
        }
    }

    public int getDayCount(long unixDate) {
        long currentDate = getCurrentDate();
        return (int) ((currentDate - unixDate) / 86400);
    }

    public boolean isLastWeek(long unixDate) {

        Calendar currentCalendar = Calendar.getInstance();
        Calendar specifiedCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(getCurrentDate() * 1000);
        specifiedCalendar.setTimeInMillis(unixDate * 1000);
        currentCalendar.setFirstDayOfWeek(Calendar.SATURDAY);
        specifiedCalendar.setFirstDayOfWeek(Calendar.SATURDAY);
        if (specifiedCalendar.get(Calendar.WEEK_OF_MONTH) ==
                (currentCalendar.get(Calendar.WEEK_OF_MONTH) - 1)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLastMonth(long unixDate) {
        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(getCurrentDate() * 1000);
        Calendar specifiedDay = Calendar.getInstance();
        specifiedDay.setTimeInMillis(unixDate * 1000);
        today.setTimeZone(TimeZone.getTimeZone("GMT+6:00"));
        specifiedDay.setTimeZone(TimeZone.getTimeZone("GMT+6:00"));
        int month = today.get(Calendar.MONTH);
        int mon = specifiedDay.get(Calendar.MONTH);
        if (today.get(Calendar.YEAR) == specifiedDay.get(Calendar.YEAR)
                && (today.get(Calendar.MONTH) - 1) == (specifiedDay.get(Calendar.MONTH))) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLastYear(long unixDate) {
        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(getCurrentDate() * 1000);
        Calendar specifiedDay = Calendar.getInstance();
        specifiedDay.setTimeInMillis(unixDate * 1000);
        today.setTimeZone(TimeZone.getTimeZone("GMT+6:00"));
        specifiedDay.setTimeZone(TimeZone.getTimeZone("GMT+6:00"));
        int month = today.get(Calendar.MONTH);
        int mon = specifiedDay.get(Calendar.MONTH);
        if ((today.get(Calendar.YEAR) - 1) == specifiedDay.get(Calendar.YEAR)) {
            return true;
        } else {
            return false;
        }
    }
}
