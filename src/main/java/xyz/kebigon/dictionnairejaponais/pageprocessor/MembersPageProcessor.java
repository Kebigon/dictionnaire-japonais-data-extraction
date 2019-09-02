package xyz.kebigon.dictionnairejaponais.pageprocessor;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import xyz.kebigon.dictionnairejaponais.data.Member;
import xyz.kebigon.dictionnairejaponais.data.Member.MemberBuilder;
import xyz.kebigon.dictionnairejaponais.writer.DataWriter;

public class MembersPageProcessor extends PageProcessor {

	public MembersPageProcessor(WebDriver driver) {
		super(driver);
	}

	public void processMembersPages(DataWriter<Member> dataWriter) throws IOException, InterruptedException {
		goToPage("http://www.dictionnaire-japonais.com/membres.php");
		do {
			processMembersPage(dataWriter);
		} while (goToNextPage());
	}

	private void processMembersPage(DataWriter<Member> dataWriter) throws IOException {
		final WebElement resultsList = getDriver().findElement(By.className("membres"));
		final List<WebElement> members = resultsList.findElements(By.xpath("./*"));

		for (final WebElement member : members) {
			final String nbWords = member.findElement(By.className("nbWords")).getText();
			if (nbWords.equals("0　mots"))
				continue;
			final String profilLink = member.findElement(By.tagName("a")).getAttribute("href");
			final int index = profilLink.lastIndexOf('=');

			final MemberBuilder builder = Member.builder();
			builder.id(Integer.parseInt(profilLink.substring(index + 1)));
			builder.words(Integer.parseInt(nbWords.substring(0, nbWords.indexOf('　'))));
			dataWriter.write(builder.build());
		}
	}
}
