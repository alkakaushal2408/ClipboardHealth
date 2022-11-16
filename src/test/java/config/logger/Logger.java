package test.java.config.logger;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import test.java.config.logger.LoggerEnricher.EnricherFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class Logger {

	private Logger() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * 
	 * @return org.apache.logging.log4j.Logger
	 */
	private static synchronized org.apache.logging.log4j.Logger getLogger() {
		return LogManager.getLogger();
	}

	/**
	 * If the log is related to a test and is either a string or throwable, logs it
	 * to extent reporting as well
	 */
	private static synchronized void extentLog(Status status, Object message) {
		extentLog(status, message, null);
	}

	/**
	 * If the log is related to a test and is either a string or throwable, logs it
	 * to extent reporting as well
	 */
	private static synchronized void extentLog(Status status, Object message, String screenshotPath) {
		if (ExtentTestManager.getTest() != null) {
			ExtentColor color = ExtentColor.GREY;
			switch (status) {
			case FAIL:
			case ERROR:
			case FATAL:
				color = ExtentColor.RED;
				break;
			case PASS:
				color = ExtentColor.GREEN;
				break;
			case WARNING:
				color = ExtentColor.YELLOW;
				break;
			case SKIP:
				color = ExtentColor.ORANGE;
				break;
			case INFO:
			case DEBUG:
			}

			try {
				if (message.getClass() == String.class) {
					if (StringUtils.isEmpty(screenshotPath)) {
						ExtentTestManager.getTest().log(status, MarkupHelper.createLabel(message.toString(), color));
					} else {

						ExtentTestManager.getTest().log(status, message.toString(),
								MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());

					}
				} else if (message.getClass() == Throwable.class) {
					ExtentTestManager.getTest().log(status, (Throwable) message,
							MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
				}
			} catch (IOException e) {
				Logger.error(message);
			}
		}
	}

	/**
	 * 
	 * @param message
	 */
	public static synchronized void debug(Object message) {
		getLogger().info(message);
		LoggerEnricher.enrichReporterLog(EnricherFactory.ORANGE, message);
		extentLog(Status.ERROR, message);
	}

	/**
	 * 
	 * @param message
	 * @param enricherType
	 */
	public static synchronized void debug(Object message, Object enricherType) {
		if (enricherType instanceof Throwable) {
			getLogger().info(message, (Throwable) enricherType);
			LoggerEnricher.enrichReporterLog(enricherType, message);
		} else {
			getLogger().info(message);
			LoggerEnricher.enrichReporterLog(enricherType, message);
		}
	}

	/**
	 * 
	 * @param message
	 */
	public static synchronized void warn(Object message) {
		getLogger().info(message);
		LoggerEnricher.enrichReporterLog(EnricherFactory.YELLOW, message);
		extentLog(Status.WARNING, message);
	}

	/**
	 * 
	 * @param message
	 * @param enricherType
	 */
	public static synchronized void warn(Object message, Object enricherType) {
		if (enricherType instanceof Throwable) {
			getLogger().info(message, (Throwable) enricherType);
			LoggerEnricher.enrichReporterLog(enricherType, message);
		} else {
			getLogger().info(message);
			LoggerEnricher.enrichReporterLog(enricherType, message);
		}
	}

	/**
	 * 
	 * @param message
	 */
	public static synchronized void error(Object message) {
		getLogger().info(message);
		LoggerEnricher.enrichReporterLog(EnricherFactory.RED, message);
		extentLog(Status.ERROR, message);
	}

	/**
	 * 
	 * @param message
	 * @param enricherType
	 */
	public static synchronized void error(Object message, Object enricherType) {
		if (enricherType instanceof Throwable) {
			getLogger().info(message, (Throwable) enricherType);
			LoggerEnricher.enrichReporterLog(enricherType, message);
		} else {
			getLogger().info(message);
			LoggerEnricher.enrichReporterLog(enricherType, message);
		}
	}

	/**
	 * 
	 * @param message
	 */
	public static synchronized void fatal(Object message) {
		getLogger().info(message);
		LoggerEnricher.enrichReporterLog(EnricherFactory.ITALIC_RED, message);
		extentLog(Status.FATAL, message);
	}

	/**
	 * 
	 * @param message
	 * @param enricherType
	 */
	public static synchronized void fatal(Object message, Object enricherType) {
		if (enricherType instanceof Throwable) {
			getLogger().info(message, (Throwable) enricherType);
			LoggerEnricher.enrichReporterLog(enricherType, message);
		} else {
			getLogger().info(message);
			LoggerEnricher.enrichReporterLog(enricherType, message);
		}
	}

	/**
	 * 
	 * @param message
	 */
	public static synchronized void info(Object message) {
		getLogger().info(message);
		LoggerEnricher.enrichReporterLog(EnricherFactory.DEFAULT, message);
		extentLog(Status.INFO, message);
	}

	/**
	 * 
	 * @param message
	 * @param enricherType
	 */
	public static synchronized void info(Object message, Object enricherType) {
		if (enricherType instanceof Throwable) {
			getLogger().info(message, (Throwable) enricherType);
			LoggerEnricher.enrichReporterLog(enricherType, message);
		} else {
			getLogger().info(message);
			LoggerEnricher.enrichReporterLog(enricherType, message);
		}
	}

	/**
	 * 
	 * @param message
	 */
	public static synchronized void pass(Object message) {
		getLogger().info(message);
		LoggerEnricher.enrichReporterLog(EnricherFactory.BOLD_GREEN, message);
		extentLog(Status.PASS, message);
	}

	/**
	 * 
	 * @param message
	 */
	public static synchronized void fail(Object message) {
		fail(message, null);
	}

	/**
	 * 
	 * @param message
	 * @param screenshotPath Full path to a screenshot for the failure if one exists
	 */
	public static synchronized void fail(Object message, String screenshotPath) {
		getLogger().info(message);
		LoggerEnricher.enrichReporterLog(EnricherFactory.BOLD_RED, message);
		extentLog(Status.FAIL, message, screenshotPath);
	}

	/**
	 * 
	 * @return
	 */
	public static synchronized boolean isDebugEnabled() {
		return getLogger().isDebugEnabled();
	}

	/**
	 * 
	 * @param level
	 * @return
	 */
	public static synchronized boolean isEnabledFor(Level level) {
		return getLogger().isEnabled(level);
	}

	/**
	 * 
	 * @return
	 */
	public static synchronized boolean isInfoEnabled() {
		return getLogger().isInfoEnabled();
	}

	/**
	 * 
	 * @param level
	 */
	public static synchronized void setLevel(Level level) {
		getLogger().atLevel(level);
	}

	public static PrintStream getPrintStream() {
		PrintStream myPrintStream = null;

		OutputStream output = new OutputStream() {
			private StringBuilder myStringBuilder = new StringBuilder();

			@Override
			public void write(int b) throws IOException {
				this.myStringBuilder.append((char) b);
			}

			/**
			 * @see OutputStream#flush()
			 */
			@Override
			public void flush() {
				var string = this.myStringBuilder.toString();
				if (StringUtils.isNotBlank(string)) {
					getLogger().info(string);
				}
				myStringBuilder = new StringBuilder();
			}
		};

		myPrintStream = new PrintStream(output, true); // true: autoflush must be set!

		return myPrintStream;

	}

	public static String getStackTrace(Throwable e) {
		var sb = new StringBuilder();
		for (StackTraceElement element : e.getStackTrace()) {
			sb.append(element.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	private static final String PRINT_PREFIX = "~~~~~~~~~~~~~~~~~~~~";
	private static final String PRINT_SUFFIX = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";

	public static void printCredentials(String url, String username, String password) {
		Logger.info(PRINT_PREFIX + " Credentials " + PRINT_PREFIX, EnricherFactory.BOLD);
		Logger.info("Portal URL: " + url);
		Logger.info("Credentials: " + username + "/" + password);
		Logger.info(PRINT_SUFFIX, EnricherFactory.BOLD);
	}

	public static void printTestDescription(String description) {
		Logger.info(PRINT_PREFIX + " Test Description " + PRINT_PREFIX, EnricherFactory.BOLD);
		Logger.info("Description: " + description);
		Logger.info(PRINT_SUFFIX, EnricherFactory.BOLD);
	}

	public static void printTestSteps(String[] steps) {
		Logger.info(PRINT_PREFIX + " Test Steps " + PRINT_PREFIX, EnricherFactory.BOLD);
		for (var i = 0; i < steps.length; i++) {
			Logger.info((i + 1) + ". " + steps[i]);
		}
		Logger.info(PRINT_SUFFIX, EnricherFactory.BOLD);
	}

	private static void printExpectedOutputString(String output) {
		Logger.info(PRINT_PREFIX + " Expected Output " + PRINT_PREFIX, EnricherFactory.BOLD);
		Logger.info("Expected output: " + output);
	}

	public static void printExpectedOutput(String output) {
		printExpectedOutputString(output);
		Logger.info(PRINT_SUFFIX, EnricherFactory.BOLD);
	}

	public static void printExpectedOutput(String output, String responseCode) {
		printExpectedOutputString(output);
		Logger.info("Expected Response Code: " + responseCode);
		Logger.info(PRINT_SUFFIX, EnricherFactory.BOLD);
	}

	public static void printExpectedOutput(String output, String responseCode, String responseBody) {
		printExpectedOutputString(output);
		Logger.info("Expected Response Code: " + responseCode);
		Logger.info("Expected Response Body: " + responseBody);
		Logger.info(PRINT_SUFFIX, EnricherFactory.BOLD);
	}
}
