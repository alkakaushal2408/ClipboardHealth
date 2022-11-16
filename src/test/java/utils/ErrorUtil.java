package test.java.utils;

import com.aventstack.extentreports.ExtentTest;
import org.testng.ITestResult;
import org.testng.Reporter;
import test.java.config.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorUtil {

	private ErrorUtil() {
		throw new IllegalStateException("Utility class");
	}

	private static Map<ITestResult, List<Throwable>> verificationFailuresMap = new HashMap<>();

	/** Adds a screenshot to the report if one was taken */
	public static void addVerificationFailure(Throwable e, String screenshotPath) {
		Logger.fail(e.getMessage().split(" expected")[0], screenshotPath);
		List<Throwable> verificationFailures = getVerificationFailures();
		verificationFailuresMap.put(Reporter.getCurrentTestResult(), verificationFailures);
		verificationFailures.add(e);
	}

	public static List<Throwable> getVerificationFailures() {
		List<Throwable> verificationFailures = verificationFailuresMap.get(Reporter.getCurrentTestResult());
		return verificationFailures == null ? new ArrayList<>() : verificationFailures;
	}

}
