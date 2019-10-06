package com.curryspcworld.qa;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.core.Every.everyItem;

public class AppTest
{
    public static WebDriver driver;

    @Before
    public void launchApp() {
        //setup the Chrome browser
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        //get the url
        driver.get("https://www.currys.co.uk/");
        //maximize the window
        driver.manage().window().maximize();
        //implicit wait for web elements
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        //page load time out for loading the page
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        //delete all cookies
        driver.manage().deleteAllCookies();
    }

    @After
    public void closeApp() {
        //close app
        driver.quit();
    }
    @Test
    public void getUrlTest()
    {
        //get the current url
        String actualUrl=getCurrentUrl();
        assertThat("different Home page "+actualUrl, Matchers.startsWith("https://www.currys.co.uk/"));
    }
    @Test
    public void logInTest(){
        logIn("reshmareddyinjeti@gmail.com","sandipani555");
        String actual=logInsuccess();
        assertThat("user not able to see user name :",actual, Matchers.startsWith("Hello"));


    }
    @Test
    public void searchTest()throws InterruptedException {
        doSearchProductTest("Laptops");
        String currentUrl = driver.getCurrentUrl();
        System.out.println(currentUrl);
        assertThat("different result " + currentUrl, Matchers.containsString("laptops"));
    }
    @Test
    public void FilterPriceTest() throws InterruptedException {
        doSearchProductTest("Laptops");
        selectCustomerPrice(" £299.00 - £499.00 ");
        List<Double> actualList=getAllPricesOnFilterProduct();
        assertThat("List is sorting wrong value .",actualList,everyItem(greaterThanOrEqualTo( 299.00)));
        assertThat("List is sorting wrong value.",actualList,everyItem(lessThanOrEqualTo( 499.00 )));
    }


    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
    public void logIn(String email,String password){
        driver.findElement(By.linkText("Sign in")).click();
        driver.findElement(By.id("input-sEmail")).sendKeys(email);
        driver.findElement(By.id("input-sPassword")).sendKeys(password);
        driver.findElement(By.cssSelector(".dc-background-primary-hover")).click();

    }
    public String logInsuccess(){

        return driver.findElement(By.cssSelector(".dc-menu-trigger")).getText();
    }


    public void doSearchProductTest(String customerSelectedProduct) throws InterruptedException {
        driver.findElement(By.id("search")).sendKeys(Keys.BACK_SPACE);
        driver.findElement(By.id("search")).sendKeys(customerSelectedProduct);
        Thread.sleep(3000);
        driver.findElement(By.cssSelector(".dc-search-fieldset__submit-button")).click();
    }
    //price filter test
    public void selectCustomerPrice( String customerSelectedPrice){
        //finding the list wedElements for price
        List<WebElement> priceWebElements=driver.findElements(By.cssSelector(".dc-filter__option-list"));
        for (WebElement priceWebElement:priceWebElements){
            if (priceWebElement.getText().equalsIgnoreCase(customerSelectedPrice)){
                priceWebElement.click();
                break;
            }
        }
    }
    //assertion for price test
    public List<Double> getAllPricesOnFilterProduct() {
        List<Double> collectedPrice = new ArrayList<>();
        List<WebElement> priceWebelements = driver.findElements(By.cssSelector(".price"));

        for (WebElement priceWebelement : priceWebelements) {
            //get the text of price webelement
            String priceInString = priceWebelement.getText().replace("£", "");
            System.out.println(priceInString);
            //converting the variable string to double
            double priceInDouble=Double.parseDouble(priceInString);
            collectedPrice.add(priceInDouble);
        }
        return collectedPrice;
    }

    //Rating TEST
    @Test
    public void filterRatingTest() throws InterruptedException {
        doSearchProductTest("Laptops");
        selectARating("4 or more");
        List<Double> actualList = getAllRatingsOnFilteredProduct();
        assertThat("List is storing wrong value or filter broken. ", actualList, everyItem(greaterThanOrEqualTo(4.0)));
    }

    public void selectARating(String customerSelectedRating) {
        List<WebElement> customerRatings=driver.findElements(By.cssSelector(".dc-filter__option .dc-rating"));
        for (WebElement ratingWebElement :customerRatings) {
            if (ratingWebElement.getText().equalsIgnoreCase(customerSelectedRating)) {
                new WebDriverWait(driver, 20)
                        .until(ExpectedConditions.elementToBeClickable(ratingWebElement));
                ratingWebElement.click();
                break;

            }
        }
    }

    public List<Double> getAllRatingsOnFilteredProduct() {

        List<Double> collectedRating = new ArrayList<>();

        List <WebElement > ratingWebElements=driver.findElements(By.cssSelector(".dc-icon-star"));
        for (WebElement ratingWedelement : ratingWebElements) {
            String ratingInSting = ratingWedelement.getAttribute("data-star-rating");
            double ratingInDouble = Double.parseDouble(ratingInSting);
            System.out.println("Collected rating :"+collectedRating);
            collectedRating.add(ratingInDouble);

        }
        return collectedRating;
    }
}


