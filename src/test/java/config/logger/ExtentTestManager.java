package test.java.config.logger;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import java.util.HashMap;
import java.util.Map;

public class ExtentTestManager {

	private ExtentTestManager() {
		throw new IllegalStateException("Utility class");
	}

	static Map<Integer, ExtentTest> extentTestMap = new HashMap<>();
	static ExtentReports extent = ExtentManager.getInstance();

	public static synchronized ExtentTest getTest() {
		return extentTestMap.get((int) (Thread.currentThread().getId()));
	}

	public static synchronized void endTest() {
		extent.flush();
	}

	public static synchronized ExtentTest startTest(String testName) {
		ExtentTest test = extent.createTest(testName);
		extentTestMap.put((int) (Thread.currentThread().getId()), test);
		return test;
	}

	public static synchronized ExtentTest startTest(String testName, String testDescription) {
		ExtentTest test = extent.createTest(testName, testDescription);
		extentTestMap.put((int) (Thread.currentThread().getId()), test);
		return test;
	}
}