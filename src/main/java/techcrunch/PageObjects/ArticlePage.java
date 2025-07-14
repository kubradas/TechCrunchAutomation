package techcrunch.PageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArticlePage {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final Logger LOGGER = Logger.getLogger(ArticlePage.class.getName());


    public ArticlePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }


    private By newsTitle = By.cssSelector(".wp-block-post-title");
    private By articleContent = By.cssSelector(".entry-content");
    private By linksInContent = By.cssSelector(".entry-content > p > a");


    public String getBrowserTitle() {
        wait.until(ExpectedConditions.titleIs(driver.getTitle()));
        return driver.getTitle();
    }

    public String getArticleTitleText() {
        try {
            WebElement titleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(newsTitle));
            return titleElement.getText();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not find or retrieve text from article title element on the page. " + e.getMessage());
            return null;
        }
    }

    public boolean areLinksClickableAndValid() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(articleContent));

        List<WebElement> links = driver.findElements(linksInContent);
        boolean allLinksValid = true;
        int checkedLinks = 0;

        LOGGER.log(Level.INFO, "Found " + links.size() + " links in the article content. Verifying...");

        for (WebElement link : links) {
            String url = link.getAttribute("href");

            if (url == null || url.isEmpty() || url.startsWith("javascript:") || url.startsWith("#")) {
                LOGGER.log(Level.INFO, "Skipping invalid, JavaScript, or internal anchor link: " + url);
                continue;
            }
            checkedLinks++;

            if (link.isDisplayed() && link.isEnabled()) {
                if (!verifyLink(url)) {
                    LOGGER.log(Level.WARNING, "Broken or invalid link found: " + url);
                    allLinksValid = false;
                } else {
                    LOGGER.log(Level.INFO, "Link valid: " + url);
                }
            } else {
                LOGGER.log(Level.WARNING, "Link not displayed or enabled, skipping URL check: " + url);
            }
        }

        if (checkedLinks == 0 && links.size() > 0) {
            LOGGER.log(Level.WARNING, "No external/valid links were found to check (or all were skipped), but links were present in the content.");
        } else if (checkedLinks == 0 && links.size() == 0) {
            LOGGER.log(Level.INFO, "No links found in the article content to verify.");
        }

        LOGGER.log(Level.INFO, "Link verification complete. All links valid: " + allLinksValid);
        return allLinksValid;
    }

    private boolean verifyLink(String linkUrl) {
        try {
            URL url = new URL(linkUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setRequestMethod("HEAD");

            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");


            httpURLConnection.connect();

            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode >= 200 && responseCode < 400) {
                return true;
            } else if (responseCode == 403 || responseCode == 401) {
                LOGGER.log(Level.INFO, "Link: " + linkUrl + " returned status code: " + responseCode + " (Access Denied/Forbidden - considered reachable, not a broken link for this test).");
                return true;
            } else {
                LOGGER.log(Level.WARNING, "Link: " + linkUrl + " returned unexpected (broken) status code: " + responseCode);
                return false;
            }
        } catch (java.net.SocketTimeoutException e) {
            LOGGER.log(Level.WARNING, "Link verification timed out for: " + linkUrl + " - " + e.getMessage());
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error verifying link: " + linkUrl + " - " + e.getMessage(), e);
            return false;
        }
    }
}