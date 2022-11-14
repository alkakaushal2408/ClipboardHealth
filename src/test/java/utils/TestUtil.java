package test.java.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class TestUtil {

	private TestUtil() {
		throw new IllegalStateException("Utility class");
	}

	static Random randomGenerator = new Random();
	private static Logger logger = LogManager.getLogger();

	/**
	 * Gets a unique long number using the UTC Date Time Stamp so the number will
	 * never repeat, meaning no two tests will ever use the same number
	 */
	public static long getUniqueNumber() {
		// waits a random number of ms so that when this is used to generate a unique
		// string, it will never return the same string twice
		TestUtil.sleep(TestUtil.getRandomIntInRange(1, 50));
		var instant = Instant.now().truncatedTo(ChronoUnit.NANOS);
		OffsetDateTime odt = instant.atOffset(ZoneOffset.UTC);
		return Long.parseLong(odt.format(DateTimeFormatter.ofPattern("uuMMddHHmmssSSS")));
	}

	/** get random int between 0 and n */
	public static int getRandomInt(int n) {
		if (n != 0) {
			return randomGenerator.nextInt(n + 1);
		} else {
			return n;
		}
	}

	/**
	 * Gets a random special character from a predefined list. Useful for testing
	 * form inputs with unique special characters each time.
	 */
	public static String getRandomSpecialCharacter() {
		var specialCharacters = "صèը;♡O益̲\\.[]{}()<>*+-=!?^$|";
		return getRandomObjectFromList(Arrays.asList(specialCharacters.split("(?!^)")));
	}

	/** Gets a list of random unique Integers between 0 and n */
	public static List<Integer> getUpToNRandomUniqueIntegers(int resultsToGet, int n) {
		List<Integer> numbers = new ArrayList<>();

		/*
		 * if number of unique numbers requested is greater than numbers in the range,
		 * gets the maximum amount instead
		 */
		if (resultsToGet - 1 > n) {
			resultsToGet = n + 1;
		}

		while (numbers.size() < resultsToGet) {
			// gets a random number
			var num = 0;
			if (n != 0) {
				num = randomGenerator.nextInt(n + 1);
			}
			// if the random number isn't already in list, adds it
			if (!numbers.contains(num)) {
				numbers.add(num);
			}
		}
		return numbers;
	}

	/** get random int between x and y */
	public static int getRandomIntInRange(int min, int max) {
		return randomGenerator.nextInt((max - min) + 1) + min;
	}

	/**
	 * Sleeps for the given number of MS
	 */
	public static void sleep(int msToWait) {
		try {
			Thread.sleep(msToWait);
		} catch (InterruptedException e) {
			logger.log(Level.WARN, "Interrupted!", e);
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Returns a random number (up to n) of random objects from an ArrayList (if
	 * insufficient objects exist in list, returns all objects)
	 */
	public static <T> List<T> getUpToNRandomObjectsFromList(int n, List<T> list) {
		List<T> randomObjects = new ArrayList<>();
		List<Integer> randomIndexes = getUpToNRandomUniqueIntegers(n, list.size() - 1);
		for (Integer i : randomIndexes) {
			randomObjects.add(list.get(i));
		}
		return randomObjects;
	}

	/** Returns a random object from an ArrayList */
	public static <T> T getRandomObjectFromList(List<T> list) {
		return list.get(getRandomInt(list.size() - 1));
	}

	/**
	 * Returns n random objects from an ArrayList (if insufficient objects exist in
	 * list, returns all objects)
	 */
	public static <T> List<T> getNRandomObjectsFromList(int n, List<T> list) {
		if (n >= list.size()) {
			return list;
		}

		List<T> subList = new ArrayList<>();
		for (var i = 0; i < n; i++) {
			var object = list.get(getRandomInt(list.size() - 1));
			subList.add(object);
			list.remove(object);
		}

		return subList;
	}

	/** Returns a random number of objects from an ArrayList */
	public static <T> List<T> getRandomNumberOfObjectsFromList(List<T> list) {
		var numberOfObjects = TestUtil.getRandomIntInRange(1, list.size() - 1);
		return getNRandomObjectsFromList(numberOfObjects, list);
	}

	/** Removes all duplicates from an ArrayList */
	public static <T> List<T> removeDuplicatesFromList(List<T> al) {
		/*
		 * add objects to a hashset (which doesn't allow dupes) then recreates the list
		 * using the hashset
		 */
		Set<T> hs = new HashSet<>();
		hs.addAll(al);
		al.clear();
		al.addAll(hs);
		return al;
	}

	/** Sorts an ArrayList alphabetically */
	public static List<String> sortListAlphabetically(List<String> list) {
		Collections.sort(list);
		return list;
	}

	/** Removes all null values from an ArrayList */
	public static String[] removeNullValuesFromArray(String[] firstArray) {
		return Arrays.stream(firstArray).filter(s -> (s != null && s.length() > 0)).toArray(String[]::new);
	}

	/** Randomly returns true or false boolean */
	public static boolean randomTrueOrFalse() {
		var trueOrFalse = TestUtil.getRandomIntInRange(0, 1);
		return trueOrFalse == 0;
	}
}
