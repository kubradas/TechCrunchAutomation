package techcrunch.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.openqa.selenium.WebElement;
import techcrunch.PageObjects.ArticlePage;
import techcrunch.PageObjects.HomePage;
import techcrunch.TestComponents.BaseTest;

import java.util.List;
import java.util.Random;

public class TCNewsTests extends BaseTest {

    @Epic("Tech Crunch Web Automation")
    @Feature("Latest News Validation")
    @Story("Validate that each news article has an author and image")
    @Description("Check latest news for author and image attributes.")
    @Test
    public void verifyLatestNewsAttributesAndContent() {
        HomePage homePage = new HomePage(driver);
        List<WebElement> newsArticles = homePage.getLatestNewsArticles();

        Assert.assertFalse(newsArticles.isEmpty(), "Assertion Failed: The 'Latest News' list is empty. No articles found on the homepage.");
        System.out.println("Verified: The 'Latest News' section contains " + newsArticles.size() + " articles.");

        int articlesWithAuthor = 0;
        int articlesWithImage = 0;

        for (int i = 0; i < newsArticles.size(); i++) {
            WebElement article = newsArticles.get(i);
            String articleTitle = homePage.getNewsTitle(article);

            System.out.println("\n--- Processing Article " + (i + 1) + ": '" + (articleTitle != null ? articleTitle : "Title Not Found") + "' ---");

            if (homePage.hasAuthor(article)) {
                articlesWithAuthor++;
                System.out.println("  Verified: Article has author: " + homePage.getAuthorName(article));
            } else {
                Assert.fail("Assertion Failed: News article '" + (articleTitle != null ? articleTitle : "Unknown Title") + "' is missing an author.");
            }

            if (homePage.hasImage(article)) {
                articlesWithImage++;
                System.out.println("  Verified: Article has an image.");
            } else {
                Assert.fail("Assertion Failed: News article '" + (articleTitle != null ? articleTitle : "Unknown Title") + "' is missing an image.");
            }
        }

        Assert.assertEquals(articlesWithAuthor, newsArticles.size(), "Assertion Failed: Not all articles have an author. Found " + articlesWithAuthor + " out of " + newsArticles.size() + ".");
        Assert.assertEquals(articlesWithImage, newsArticles.size(), "Assertion Failed: Not all articles have an image. Found " + articlesWithImage + " out of " + newsArticles.size() + ".");
        System.out.println("\nSummary: All " + newsArticles.size() + " latest news articles have an author and an image.");
    }


    @Epic("Tech Crunch Web Automation")
    @Feature("Latest News Validation")
    @Story("Validate article title and links after opening a random article")
    @Description("Open a random latest news article and verify its title and internal links.")
    @Test
    public void verifyTitleAndLinks() {
        HomePage homePage = new HomePage(driver);
        List<WebElement> newsArticles = homePage.getLatestNewsArticles();

        if (newsArticles.isEmpty()) {
            Assert.fail("Test cannot proceed: No news articles found to click.");
        }

        Random random = new Random();
        int randomIndex = random.nextInt(newsArticles.size());
        WebElement randomNewsArticle = newsArticles.get(randomIndex);

        String expectedArticleTitle = homePage.getNewsTitle(randomNewsArticle);

        if (expectedArticleTitle == null) {
            System.err.println("Warning: Could not retrieve title for the randomly selected news article before clicking.");
            expectedArticleTitle = "Unknown Article Title";
        }

        System.out.println("\nNavigating: Clicking on a random news article with expected title: '" + expectedArticleTitle + "'");

        ArticlePage articlePage = homePage.clickNewsArticle(randomNewsArticle);

        String actualBrowserTitle = articlePage.getBrowserTitle();
        String actualArticleTitleOnPage = articlePage.getArticleTitleText();

        String normalizedExpectedArticleTitle = expectedArticleTitle
                .replace("’", "'")
                .replace("‘", "'")
                .replace("?", "'")
                .replace("“", "\"")
                .replace("”", "\"");

        String cleanedActualBrowserTitle = actualBrowserTitle.replace(" | TechCrunch", "").trim();

        String normalizedActualBrowserTitle = cleanedActualBrowserTitle
                .replace("’", "'")
                .replace("‘", "'")
                .replace("?", "'")
                .replace("“", "\"")
                .replace("”", "\"");


        Assert.assertTrue(normalizedActualBrowserTitle.contains(normalizedExpectedArticleTitle),
                "Assertion Failed: Browser title '" + actualBrowserTitle + "' (normalized: '" + normalizedActualBrowserTitle + "') does not contain the expected news title '" + normalizedExpectedArticleTitle + "'.");
        System.out.println("Verified Browser Title: Browser title '" + actualBrowserTitle + "' contains expected title fragment.");


        String normalizedActualArticleTitleOnPage = actualArticleTitleOnPage
                .replace("’", "'")
                .replace("‘", "'")
                .replace("?", "'")
                .replace("“", "\"")
                .replace("”", "\"");


        Assert.assertEquals(normalizedActualArticleTitleOnPage, normalizedExpectedArticleTitle,
                "Assertion Failed: Article title on the opened page ('" + actualArticleTitleOnPage + "') (normalized: '" + normalizedActualArticleTitleOnPage + "') does not match the expected original news title ('" + expectedArticleTitle + "') (normalized: '" + normalizedExpectedArticleTitle + "').");
        System.out.println("Verified Article Title: Article title on page '" + actualArticleTitleOnPage + "' matches the expected title.");

        Assert.assertTrue(articlePage.areLinksClickableAndValid(), "Assertion Failed: Some links within the news content are broken or invalid. Check ArticlePage.areLinksClickableAndValid() implementation.");
        System.out.println("Verified: All found links within the article content are clickable and valid.");
    }
}