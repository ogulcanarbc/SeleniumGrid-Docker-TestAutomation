package page;

import base.BaseTest;
import helper.date.DateAndTimeHelper;
import helper.file.FileHelper;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import helper.RestAssuredHelper;
import wait.ExplicitWaitServiceServices;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePage extends BasePage {

    Logger logger = Logger.getLogger(HomePage.class);

    private static final By homePagePopUp = By.xpath("//a[@title='Close']");
    private static final By signInIcon = By.xpath("//i[@class='icon navigation-icon-user']");
    private static final By boutiqueLinkUrl = By.xpath("//*[@class='category-header']//following::li/a");
    private static final By img = By.cssSelector(".image-container>img");

    protected HashMap<String, String> urlAndRespCode = new HashMap<String, String>();
    protected HashMap<String, String> imgUrlAndResTime = new HashMap<String, String>();
    protected RestAssuredHelper restAssuredHelper;
    private static String href;
    private static String statusCode;
    private static String imgSrcUrl;
    private static String imgLoadTime;
    private static long startTime;
    private static long endTime;
    private static long estimatedTime;

    public HomePage closePopUp() {
        if (isExistElement(homePagePopUp)) {
            findElement(homePagePopUp).click();
        } else {
            logger.error("Home Page PopUp Not Found.");
        }
        logger.info("Home page popup closed.");
        return this;
    }

    public LoginPage clickSignInIconButton() {
        click(signInIcon);
        logger.info("Click Sign In Icon Button..");
        return new LoginPage();
    }

    /**
     * @return Scroll yapıldıktan sonra URL ve response kodlarını HashMap formatında döndürür. (STEP/2)
     */
    public HashMap<String, String> getBoutiqueImgUrlAndLoadResponseTimeList() {
        List<WebElement> webElements = findElements(img);
        logger.info("Time counter started.");
        for (int i = 0; i < webElements.size(); i++) {
            startTime = System.currentTimeMillis();
            scrollToWebElement(webElements.get(i));
            new ExplicitWaitServiceServices().attributeToBeNotEmptyBy(webElements.get(i), "src");
            endTime = System.currentTimeMillis();
            estimatedTime = endTime - startTime;
            imgLoadTime = String.valueOf((double) estimatedTime / 1000);
            imgSrcUrl = webElements.get(i).getAttribute("src");
            imgUrlAndResTime.put(String.valueOf(imgSrcUrl), imgLoadTime);
        }
        return imgUrlAndResTime;
    }


    /**
     * @return Butik url response time  ve response kodlarını HashMap formatında döndürür. (STEP/1)
     */
    public HashMap<String, String> getBoutiqueUrlAndResponseCodeAfterSet() {
        List<WebElement> elements = new ExplicitWaitServiceServices().waitPresenceOfAllElementLocatedBy(boutiqueLinkUrl);
        for (int i = 0; i < elements.size(); i++) {
            href = elements.get(i).getAttribute("href");
            statusCode = String.valueOf(RestAssuredHelper.getStatusCodeForGetRequest(href));
            urlAndRespCode.put(href, statusCode);
        }
        return urlAndRespCode;
    }

    /**
     * Trendyol ana sayfadaki tüm butik linklerinin yülklenme sürelerini ve response kodlarını
     * .csv dosyasına kaydeder! (STEP/2)
     */
    public void writeBoutiqueUrlAndResponseCodeToCsv() {
        try {
            logger.info("Writing to csv file. It may take some time. :)");
            FileUtils.write(getBoutiqueUrlAndResponseCodeCsvFilePath(), convertBoutiqeUrlAndResponseCodeCsvFormat());
        } catch (IOException e) {
            logger.error("Ohh noo... There was a mistake :(  -> " + e.getMessage());
            e.printStackTrace();
        }
        logger.info("Boutique url and response codes saved to csv file successfully.");
    }

    /**
     * Scroll yapıldıktan sonra Image url,response kod ve response timeları .csv dosyasına kaydeder! (STEP/2)
     */
    public void writeBoutiqueImageUrlAndResponseTimeToCsv() {
        try {
            logger.info("Writing to csv file. It may take some time. :)");
            FileUtils.write(getBoutiqueImgUrlAndResponseTimeCsvFilePath(), convertBoutiqueImgSrcUrlAndResponseTimeCsvFormat());
        } catch (IOException e) {
            logger.error("Ohh noo... There was a mistake :(  -> " + e.getMessage());
            e.printStackTrace();
        }
        logger.info("Boutique img src url and response times saved to csv file successfully.");
    }

    /**
     * Trendyol ana sayfasındaki butik linklerini ve bu linklere atılan requestlerden dönen response kodlarını tutacak
     * .csv uzantılı dosya döndürür. (STEP/1)
     */
    public File getBoutiqueUrlAndResponseCodeCsvFilePath() {
        return FileHelper.getInstance().getFile(BaseTest.localPath + "/reports/LinkAndResponseCodeResult/"
                + DateAndTimeHelper.getNowDateDayMonthYearFormatAsString() + "/"
                + DateAndTimeHelper.getNowDateDHoursMinuteSecondFormatAsString()
                + ".csv");
    }

    /**
     * Scroll edildiğinde butik linklerinin yüklenme süreleri ve bu linklere atılan requestlerden dönen response kodlarını tutacak
     * .csv uzantılı dosya döndürür. (STEP/2)
     */
    public File getBoutiqueImgUrlAndResponseTimeCsvFilePath() {
        return FileHelper.getInstance().getFile(BaseTest.localPath + "/reports/ImgUrlAndResponseTimeResult/"
                + DateAndTimeHelper.getNowDateDayMonthYearFormatAsString() + "/"
                + DateAndTimeHelper.getNowDateDHoursMinuteSecondFormatAsString()
                + ".csv");
    }

    /**
     * Trendyol ana sayfadaki tüm butik(sublist dahil) linklerini ve bu linklere atılan request sonucu dönen response
     * kodlarını csv formatına çevirir. (STEP/1)
     */
    public StringBuilder convertBoutiqeUrlAndResponseCodeCsvFormat() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("URL")
                .append(",")
                .append("STATUS CODE")
                .append("\n");
        for (Map.Entry<String, String> entry : getBoutiqueUrlAndResponseCodeAfterSet().entrySet()) {
            stringBuilder.append(entry.getKey())
                    .append(',')
                    .append(entry.getValue())
                    .append("\n");
        }
        return stringBuilder;
    }

    /**
     * Butik İmage linklerinin yüklenme sürelerini ve response kodlarının csv formatına çevirir.(STEP/2)
     */
    public StringBuilder convertBoutiqueImgSrcUrlAndResponseTimeCsvFormat() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("IMG URL")
                .append(",")
                .append("RESPONSE TIME")
                .append(",")
                .append("STATUS CODE")
                .append("\n");

        for (Map.Entry<String, String> entry : getBoutiqueImgUrlAndLoadResponseTimeList().entrySet()) {
            stringBuilder.append(entry.getKey())
                    .append(',')
                    .append(entry.getValue())
                    .append(",")
                    .append(RestAssuredHelper.getStatusCodeForGetRequest(imgSrcUrl))
                    .append("\n");
        }
        return stringBuilder;
    }
}