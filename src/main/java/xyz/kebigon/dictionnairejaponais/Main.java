package xyz.kebigon.dictionnairejaponais;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import xyz.kebigon.dictionnairejaponais.data.Member;
import xyz.kebigon.dictionnairejaponais.data.WordEntry;
import xyz.kebigon.dictionnairejaponais.data.WordEntry.WordEntryBuilder;
import xyz.kebigon.dictionnairejaponais.writer.CSVDataWriter;
import xyz.kebigon.dictionnairejaponais.writer.DataWriter;
import xyz.kebigon.dictionnairejaponais.writer.JSONDataWriter;

public class Main {
	private static final long SHORT_WAIT = 2000;
	private static final long LONG_WAIT = 5000;

	public static void main(String[] args) throws InterruptedException, ParseException, IOException {
		final Options options = new Options();

		final OptionGroup actions = new OptionGroup();
		actions.addOption(new Option("m", "list-members", false, "Get a list of members with at least 1 contribution"));
		actions.addOption(new Option("e", "list-entries", false, "Get a list of entries from the members"));
		options.addOptionGroup(actions);

		options.addOption(new Option("of", "output-file", true, "Use a file instead of the standard output"));
		options.addOption(new Option("if", "input-file", true, "Use a file instead of the standard input"));

		final OptionGroup formats = new OptionGroup();
		formats.addOption(new Option("json", "json", false, "Data will be outputed as JSON"));
		formats.addOption(new Option("csv", "csv", false, "Data will be outputed as CSV. Default behaviour"));
		options.addOptionGroup(formats);

		options.addOption("h", "help", false, "Display this screen");

		final CommandLineParser parser = new DefaultParser();
		final CommandLine cmd = parser.parse(options, args);

		if (cmd.hasOption('h')) {
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar dictionnaire-japonais-scraper", options);
			return;
		}

		final Writer writer = cmd.hasOption("of") ? //
				new FileWriter(cmd.getOptionValue("of")) : //
				new OutputStreamWriter(System.out);

		final DataWriter dataWriter = cmd.hasOption("json") ? //
				new JSONDataWriter(writer) : //
				new CSVDataWriter(writer);

		final BufferedReader reader;
		if (cmd.hasOption("if")) {
			reader = new BufferedReader(new FileReader(cmd.getOptionValue("if")));
		} else {
			reader = new BufferedReader(new InputStreamReader(System.in));
		}

		System.setProperty("webdriver.chrome.driver", "/home/kebigon/Applications/chromedriver");

		// Initialize browser
		final WebDriver driver = new ChromeDriver();

		if (cmd.hasOption("m")) {
			processMembersPages(driver, dataWriter);
		} else if (cmd.hasOption("e")) {
			String member;
			while ((member = reader.readLine()) != null) {
				goToPage(driver, "http://www.dictionnaire-japonais.com/search.php?w=user:" + member);
				processSearchPages(driver, dataWriter);
			}
		}

		// Close browser
		driver.close();
		writer.close();
	}

	private static void processMembersPages(WebDriver driver, DataWriter dataWriter)
			throws IOException, InterruptedException {
		goToPage(driver, "http://www.dictionnaire-japonais.com/membres.php");
		do {
			processMembersPage(driver, dataWriter);
		} while (goToNextPage(driver));
	}

	private static void processMembersPage(WebDriver driver, DataWriter dataWriter) throws IOException {
		final WebElement resultsList = driver.findElement(By.className("membres"));
		final List<WebElement> members = resultsList.findElements(By.xpath("./*"));

		for (final WebElement member : members) {
			final WebElement nbWords = member.findElement(By.className("nbWords"));
			if (nbWords.getText().equals("0　mots"))
				continue;
			final String profilLink = member.findElement(By.tagName("a")).getAttribute("href");
			final int index = profilLink.lastIndexOf('=');

			dataWriter.write(Member.builder().id(profilLink.substring(index + 1)).build());
		}
	}

	private static void processSearchPages(WebDriver driver, DataWriter dataWriter)
			throws IOException, InterruptedException {
		do {
			processSearchPage(driver, dataWriter);
		} while (goToNextPage(driver));
	}

	private static final String ENTRY_LINK_BASE_PATH = "http://www.dictionnaire-japonais.com/w/";

	private static void processSearchPage(WebDriver driver, DataWriter dataWriter) throws IOException {

		final WebElement resultsList = driver.findElement(By.className("resultsList"));

		final List<WebElement> words = resultsList.findElements(By.xpath("./*"));

		for (final WebElement word : words) {
			final String link = word.findElement(By.tagName("a")).getAttribute("href");

			final WordEntryBuilder builder = WordEntry.builder();
			builder.id(link.substring(ENTRY_LINK_BASE_PATH.length(), link.indexOf("/", ENTRY_LINK_BASE_PATH.length())));
			builder.japanese(word.findElement(By.className("jp")).getText());
			builder.furigana(word.findElement(By.className("kana")).getText());
			builder.romaji(word.findElement(By.className("romaji")).getText());
			builder.french(word.findElement(By.className("fr")).getText());
			builder.details(word.findElement(By.className("detail")).getText());
			dataWriter.write(builder.build());
		}
	}

	private static void goToPage(WebDriver driver, String url) throws InterruptedException {
		driver.navigate().to(url);
		Thread.sleep(LONG_WAIT);

		while (!isValidPage(driver)) {
			Thread.sleep(SHORT_WAIT);
			driver.navigate().refresh();
		}
	}

	private static boolean goToNextPage(WebDriver driver) throws InterruptedException {
		try {
			driver.findElement(By.className("next")).click();
			Thread.sleep(SHORT_WAIT);
		} catch (final NoSuchElementException ex) {
			return false; // No next page
		}

		while (!isValidPage(driver)) {
			Thread.sleep(SHORT_WAIT);
			driver.navigate().refresh();
		}

		return true;
	}

	private static boolean isValidPage(WebDriver driver) {
		try {
			driver.findElement(By.id("bg1"));
			return true; // Page is valid;
		} catch (final NoSuchElementException ex) {
			return false; // Page is not valid
		}
	}
}
