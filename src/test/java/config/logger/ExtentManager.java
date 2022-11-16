package test.java.config.logger;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Protocol;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Paths;

public class ExtentManager {

	private ExtentManager() {
		throw new IllegalStateException("Utility class");
	}

	private static ExtentReports extent;
	private static String reportFileName = "extent-report.html";
	private static String path = Paths.get(System.getProperty("user.dir"), "test-output", "extent").toString();
	private static String reportFileLoc = Paths.get(path, reportFileName).toString();

	public static ExtentReports getInstance() {
		if (extent == null)
			extent = createInstance("");
		return extent;
	}

	// Create an extent report instance
	public static ExtentReports createInstance(String suiteName) {
		String fileName = getReportFileLocation();
		var htmlReporter = new ExtentSparkReporter(fileName);
		htmlReporter.config().setTheme(Theme.STANDARD);
		htmlReporter.config().setDocumentTitle("Amazon UI Automation Report");
		htmlReporter.config().setEncoding("utf-8");
		if (StringUtils.isNotBlank(suiteName)) {
			htmlReporter.config().setReportName(suiteName);
		} else {
			htmlReporter.config().setReportName("Automation Report");
		}

		htmlReporter.config().enableOfflineMode(false);
		htmlReporter.config().enableTimeline(true);
		htmlReporter.config().setProtocol(Protocol.HTTPS);

		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		return extent;
	}

	// Select the extent report file location based on platform
	private static String getReportFileLocation() {
		String reportFileLocation = reportFileLoc;
		createReportPath(path);
		return reportFileLocation;
	}

	// Create the report path if it does not exist
	private static void createReportPath(String path) {
		var testDirectory = new File(path);
		if (!testDirectory.exists()) {
			if (testDirectory.mkdir()) {
				Logger.info("Directory: " + path + " is created!");
			} else {
				Logger.info("Failed to create directory: " + path);
			}
		} else {
			Logger.info("Directory already exists: " + path);
		}
	}

}