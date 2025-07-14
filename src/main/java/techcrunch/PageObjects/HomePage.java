package techcrunch.PageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class HomePage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By latestNewsArticles = By.cssSelector(".wp-block-block.wp-block-query > ul > li");
    private By newsAuthor = By.cssSelector(".loop-card__author-list");
    private By newsImage = By.cssSelector(".loop-card__figure > img");
    private By newsTitleLink = By.cssSelector(".loop-card__title a");

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public List<WebElement> getLatestNewsArticles() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(latestNewsArticles));
        return driver.findElements(latestNewsArticles);
    }

    public boolean hasAuthor(WebElement newsArticle) {
        List<WebElement> authors = newsArticle.findElements(newsAuthor);
        return !authors.isEmpty() && authors.get(0).isDisplayed();
    }

    public String getAuthorName(WebElement newsArticle) {
        List<WebElement> authors = newsArticle.findElements(newsAuthor);
        if (!authors.isEmpty()) {
            return authors.get(0).getText();
        }
        return null;
    }

    public boolean hasImage(WebElement newsArticle) {
        List<WebElement> images = newsArticle.findElements(newsImage);
        if (!images.isEmpty()) {
            WebElement image = images.get(0);
            String src = image.getAttribute("src");
            return image.isDisplayed() && src != null && !src.isEmpty();
        }
        return false;
    }

    public String getNewsTitle(WebElement newsArticle) {
        List<WebElement> titleLinks = newsArticle.findElements(newsTitleLink);
        if (!titleLinks.isEmpty()) {
            return titleLinks.get(0).getText();
        }
        return null;
    }

    public ArticlePage clickNewsArticle(WebElement newsArticle) {
        WebElement titleElement = wait.until(ExpectedConditions.elementToBeClickable(newsArticle.findElement(newsTitleLink)));
        titleElement.click();
        return new ArticlePage(driver);
    }
}