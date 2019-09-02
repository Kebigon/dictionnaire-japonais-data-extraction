package xyz.kebigon.dictionnairejaponais.pageprocessor;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

public abstract class PageProcessor {
	private final WebDriver driver;

	protected PageProcessor(WebDriver driver) {
		this.driver = driver;
	}

	protected WebDriver getDriver() {
		return driver;
	}

	protected void goToPage(String url) throws InterruptedException {
		driver.navigate().to(url);

		while (!isValidPage()) {
			driver.navigate().refresh();
		}
	}

	protected boolean goToNextPage() throws InterruptedException {
		try {
			driver.findElement(By.className("next")).click();
		} catch (final NoSuchElementException ex) {
			return false; // No next page
		}

		while (!isValidPage()) {
			driver.navigate().refresh();
		}

		return true;
	}

	private boolean isValidPage() {
		try {
			driver.findElement(By.xpath("/html/body/div[@id='bg1']"));
			return true; // Page is valid;
		} catch (final NoSuchElementException ex) {
			return false; // Page is not valid
		}
	}
}
