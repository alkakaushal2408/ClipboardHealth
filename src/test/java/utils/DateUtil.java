package test.java.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtil {

	private DateUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static String getCurrentUTCDateTimeStamp() {
		// waits a random number of ms so that when this is used to generate a unique
		// string, it will never return the same string twice
		TestUtil.sleep(TestUtil.getRandomIntInRange(1, 50));
		var instant = Instant.now().truncatedTo(ChronoUnit.NANOS);
		OffsetDateTime odt = instant.atOffset(ZoneOffset.UTC);
		return odt.format(DateTimeFormatter.ofPattern("uuuuMMdd'T'HHmmssSSS"));
	}

	/**
	 * Gets the current date in the system's timezone, and returns it in the passed
	 * in format
	 * 
	 * @param format Date format: eg. "MM/dd/yyyy"
	 * @return The current date in the given format
	 */
	public static String getCurrentDate(String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		return getCurrentDate(dateFormat);
	}

	/**
	 * Gets the current date in the given timezone, and returns it in the passed in
	 * format
	 * 
	 * @param tz     Timezone to get the date in
	 * @param format Date format: eg. "MM/dd/yyyy"
	 * @return The current date in the given format
	 */
	public static String getCurrentDate(TimeZone tz, String dateFormatStr) {
		DateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
		dateFormat.setTimeZone(tz);
		return getCurrentDate(dateFormat);
	}

	private static String getCurrentDate(DateFormat dateFormat) {
		var date = new GregorianCalendar();
		return dateFormat.format(date.getTime());
	}
}