import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;

public class ElPaisScraper {

    // -------- DOWNLOAD IMAGE --------
    public static void downloadImage(String imageUrl, String filePath) {
        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.copy(in, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Downloaded: " + filePath);
        } catch (Exception e) {
            System.out.println("Failed to download image: " + e.getMessage());
        }
    }

    // -------- COOKIE HANDLER --------
    public static void acceptCookiesIfPresent(WebDriver driver) {
        try {
            Thread.sleep(2500);
            List<WebElement> didomiButton = driver.findElements(By.id("didomi-notice-agree-button"));
            if (!didomiButton.isEmpty()) {
                System.out.println("Homepage cookie popup detected! Clicking...");
                didomiButton.get(0).click();
                Thread.sleep(2000);
                return;
            }

            List<WebElement> fallback = driver.findElements(
                    By.xpath("//button[contains(text(),'Accept') or contains(text(),'Aceptar')]")
            );

            if (!fallback.isEmpty()) {
                fallback.get(0).click();
                System.out.println("Clicked fallback cookie button");
                Thread.sleep(2000);
            } else {
                System.out.println("No cookie popup present");
            }

        } catch (Exception e) {
            System.out.println("Cookie error: " + e.getMessage());
        }
    }

    // -------- MAIN --------
    public static void main(String[] args) throws Exception {

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        driver.get("https://elpais.com/");
        System.out.println("Website opened!");

        acceptCookiesIfPresent(driver);
        Thread.sleep(2000);

        // Force Spanish
        try {
            WebElement langButton = driver.findElement(By.cssSelector("a[hreflang='es']"));
            langButton.click();
            System.out.println("Switched to Spanish!");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Already in Spanish.");
        }

        // Navigate to Opinión
        try {
            WebElement opinionLink = driver.findElement(By.linkText("Opinión"));
            opinionLink.click();
            System.out.println("Navigated to Opinión!");
        } catch (Exception e) {
            System.out.println("Opinión section not found.");
        }

        Thread.sleep(3000);

        // Collect article URLs
        List<WebElement> articleElements = driver.findElements(By.cssSelector("article a"));
        List<String> articleUrls = new ArrayList<>();
        for (WebElement el : articleElements) {
            String href = el.getAttribute("href");
            if (href != null &&
                    href.matches("https://elpais\\.com/opinion/\\d{4}-\\d{2}-\\d{2}/.*\\.html")) {
                articleUrls.add(href);
            }
        }

        System.out.println("Found article URLs: " + articleUrls.size());

        int limit = Math.min(5, articleUrls.size()); // limit number of articles

        String baseFolder = "elpais_articles";
        new File(baseFolder).mkdirs();

        // Prepare CSV (UTF-8)
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(baseFolder + "/index.csv"), StandardCharsets.UTF_8);
             PrintWriter csvWriter = new PrintWriter(writer)) {

            csvWriter.println("article_id,title,date,url,num_images,folder_path");

            for (int i = 0; i < limit; i++) {

                driver.get(articleUrls.get(i));
                Thread.sleep(2000);

                acceptCookiesIfPresent(driver);

                System.out.println("\nVisiting: " + articleUrls.get(i));

                String articleFolder = baseFolder + "/article_" + (i + 1);
                new File(articleFolder).mkdirs();
                File imagesFolder = new File(articleFolder + "/images");
                if (!imagesFolder.exists()) {
                    imagesFolder.mkdir();
                }

                // Extract title (decode HTML entities + UTF-8 save)
                String rawTitle = driver.findElement(By.tagName("h1")).getText();
                String title = StringEscapeUtils.unescapeHtml4(rawTitle);
                try (Writer fw = new OutputStreamWriter(new FileOutputStream(articleFolder + "/title.txt"), StandardCharsets.UTF_8)) {
                    fw.write(title);
                }

                // Extract content
                List<WebElement> paragraphs = driver.findElements(By.tagName("p"));
                StringBuilder content = new StringBuilder();
                for (WebElement p : paragraphs) {
                    content.append(p.getText()).append("\n");
                }
                try (Writer fw = new OutputStreamWriter(new FileOutputStream(articleFolder + "/content.txt"), StandardCharsets.UTF_8)) {
                    fw.write(content.toString());
                }
                System.out.println("Saved content (first 300 chars): " +
                        content.substring(0, Math.min(300, content.length())) + "...");

                // Extract date
                String date = "";
                try {
                    WebElement timeElem = driver.findElement(By.tagName("time"));
                    date = timeElem.getAttribute("datetime");
                } catch (Exception ignored) {}

                // -------- Only cover image --------
                int imgCount = 0;
                try {
                    WebElement coverImg = driver.findElement(By.cssSelector("figure img, picture img"));
                    String imgUrl = coverImg.getAttribute("src");
                    if (imgUrl == null || imgUrl.isEmpty()) {
                        imgUrl = coverImg.getAttribute("data-src");
                    }
                    if (imgUrl != null && !imgUrl.isEmpty()) {
                        String imgPath = imagesFolder + "/cover.jpg";
                        downloadImage(imgUrl, imgPath);
                        imgCount = 1;
                    }
                } catch (Exception e) {
                    System.out.println("No cover image found for article " + (i + 1));
                }

                // Save metadata
                try (Writer fw = new OutputStreamWriter(new FileOutputStream(articleFolder + "/meta.txt"), StandardCharsets.UTF_8)) {
                    fw.write("URL: " + articleUrls.get(i) + "\n");
                    fw.write("Date: " + date + "\n");
                    fw.write("Num Images: " + imgCount + "\n");
                }

                // Write to CSV
                csvWriter.printf("article_%d,\"%s\",%s,%s,%d,%s%n",
                        (i + 1), title.replace("\"", "'"), date, articleUrls.get(i), imgCount, articleFolder);
            }
        }

        driver.quit();
        System.out.println("\nAll articles saved in folder: " + baseFolder);
    }
}