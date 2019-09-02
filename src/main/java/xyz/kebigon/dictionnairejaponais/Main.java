package xyz.kebigon.dictionnairejaponais;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import lombok.extern.slf4j.Slf4j;
import xyz.kebigon.dictionnairejaponais.data.Member;
import xyz.kebigon.dictionnairejaponais.data.WordEntry;
import xyz.kebigon.dictionnairejaponais.pageprocessor.MembersPageProcessor;
import xyz.kebigon.dictionnairejaponais.pageprocessor.SearchPageProcessor;
import xyz.kebigon.dictionnairejaponais.writer.DataReader;
import xyz.kebigon.dictionnairejaponais.writer.DataWriter;

@Slf4j
public class Main {
//	private static final long SHORT_WAIT = 2000;
//	private static final long LONG_WAIT = 5000;

	public static void main(String[] args) throws InterruptedException, ParseException, IOException {
		final Options options = new Options();

		final OptionGroup actions = new OptionGroup();
		actions.addOption(new Option("m", "list-members", false, "Get a list of members with at least 1 contribution"));
		actions.addOption(new Option("e", "list-entries", false, "Get a list of entries from the members"));
		options.addOptionGroup(actions);

		options.addOption("of", "output-file", true, "Use a file instead of the standard output");
		options.addOption("if", "input-file", true, "Use a file instead of the standard input");

		options.addOption(null, "chrome-driver", true, "Chrome driver's path");
		options.addOption(null, "chrome-path", true, "path to the Chrome executable");
		options.addOption(null, "headless", false, "use Chrome's headless mode");

		options.addOption("h", "help", false, "Display this screen");

		final CommandLineParser parser = new DefaultParser();
		final CommandLine cmd = parser.parse(options, args);

		if (cmd.hasOption('h')) {
			printHelp(options);
			return;
		}

		final String inputFile = cmd.hasOption("if") ? cmd.getOptionValue("if") : null;
		final String outputFile = cmd.hasOption("of") ? cmd.getOptionValue("of") : null;

		if (!cmd.hasOption("chrome-driver"))
			throw new RuntimeException("Chrome driver path is missing");

		System.setProperty("webdriver.chrome.driver", cmd.getOptionValue("chrome-driver"));

		// Initialize browser
		final ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("remote-debugging-port=12345");
		if (cmd.hasOption("chrome-path")) {
			final String chromePath = cmd.getOptionValue("chrome-path");
			log.info("Using Chromium executable {}", chromePath);
			chromeOptions.setBinary(chromePath);
		}

		if (cmd.hasOption("headless")) {
			log.info("Using Chromium in headless mode");
			chromeOptions.setHeadless(true);
			chromeOptions.addArguments("--headless");
			chromeOptions.addArguments("headless");
		}

		final WebDriver driver = new ChromeDriver(chromeOptions);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		if (cmd.hasOption("m")) {
			if (outputFile == null)
				throw new RuntimeException("Missing of parameter");

			final MembersPageProcessor processor = new MembersPageProcessor(driver);

			try (DataWriter<Member> dataWriter = new DataWriter<Member>(outputFile)) {
				processor.processMembersPages(dataWriter);
			}
		}

		else if (cmd.hasOption("e")) {
			if (inputFile == null)
				throw new RuntimeException("Missing if parameter");
			if (outputFile == null)
				throw new RuntimeException("Missing of parameter");

			final SearchPageProcessor processor = new SearchPageProcessor(driver);

			try (final DataWriter<WordEntry> dataWriter = new DataWriter<WordEntry>(outputFile, 400)) {
				for (final Member member : new DataReader(inputFile).readAll())
					processor.processSearchPages(member, dataWriter);
			}
		}

		// Close browser
		driver.close();
	}

	private static void printHelp(Options options) {
		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar dictionnaire-japonais-scraper", options);
	}
}
