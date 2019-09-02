package xyz.kebigon.dictionnairejaponais.pageprocessor;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import lombok.extern.slf4j.Slf4j;
import xyz.kebigon.dictionnairejaponais.data.Member;
import xyz.kebigon.dictionnairejaponais.data.WordEntry;
import xyz.kebigon.dictionnairejaponais.data.WordEntry.WordEntryBuilder;
import xyz.kebigon.dictionnairejaponais.writer.DataWriter;

@Slf4j
public class SearchPageProcessor extends PageProcessor {

	private static final String ENTRY_PATH = "http://www.dictionnaire-japonais.com/w/";
	private static final int ENTRY_PATH_LENGTH = ENTRY_PATH.length();

	private static final String RESULTS_LIST_XPATH = "//ul[@class='resultsList']/li/a";
	private static final String RESULT_XPATH = "./span[@class='jap']/span[@class='jp'|@class='kana'|@class='romaji']|./span[@class='fr'|@class='detail']";

	public SearchPageProcessor(WebDriver driver) {
		super(driver);
	}

	public void processSearchPages(Member member, DataWriter<WordEntry> dataWriter)
			throws IOException, InterruptedException {
		log.info("Processing words of user {} ({} words)", member.getId(), member.getWords());

		goToPage("http://www.dictionnaire-japonais.com/search.php?w=user:" + member.getId());

		do {
			processSearchPage(dataWriter);
		} while (goToNextPage());
	}

	private void processSearchPage(DataWriter<WordEntry> dataWriter) throws IOException {
		for (final WebElement word : getDriver().findElements(By.xpath(RESULTS_LIST_XPATH))) {
			final WordEntryBuilder builder = WordEntry.builder();

			final String link = word.getAttribute("href");
			builder.id(Integer.parseInt(link.substring(ENTRY_PATH_LENGTH, link.indexOf('/', ENTRY_PATH_LENGTH))));

			for (final WebElement element : word.findElements(By.xpath(RESULT_XPATH))) {
				switch (element.getAttribute("class")) {
				case "jp":
					builder.japanese(element.getText());
					break;
				case "kana":
					builder.furigana(element.getText());
					break;
				case "romaji":
					builder.romaji(element.getText());
					break;
				case "fr":
					builder.french(element.getText());
					break;
				case "detail":
					builder.details(element.getText());
					break;
				}
			}

			dataWriter.write(builder.build());
		}
	}
}
