import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.*;


import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class SiteForPracticeTest {

    WebDriver driver;
    Actions actions;


    @DataProvider(name = "searchQueryAndResult")
    public Object[][] dataForTests(){
        Object[][] searchData = new Object[][]{
                {"Summer","\"SUMMER\""}
        };
        return searchData;
    }

    @BeforeSuite
    void openChromeDriver(){

        System.setProperty("webdriver.chrome.driver","C:\\Users\\bielskyi\\IdeaProjects\\automation\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://automationpractice.com/");

    }


    @Test(priority = 0,dataProvider = "searchQueryAndResult")
    public void searchResultTest(String searchQuery, String searchResult){

            WebElement searchField = driver.findElement(By.name("search_query"));
            searchField.sendKeys(searchQuery);
            WebElement searchButton = driver.findElement(By.name("submit_search"));
            searchButton.click();
            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
            String actualSearchResult = driver.findElement(By.xpath("//h1[@class='page-heading  product-listing']//span[@class='lighter']")).getText();
            Assert.assertEquals(actualSearchResult,searchResult);

    }

    @Test(priority = 1)
    public void sortByPriceTest(){

        WebElement sortByPicklist = driver.findElement(By.id("uniform-selectProductSort"));
        sortByPicklist.click();
        WebElement sortValue = driver.findElement(By.xpath("//div[@id='uniform-selectProductSort']//option[@value='price:desc']"));
        sortValue.click();
        WebElement selectedSortValue = driver.findElement(By.xpath("//div[@id='uniform-selectProductSort']//option[@selected='selected']"));
        Assert.assertEquals(selectedSortValue.getText(),"Price: Highest first");
        //WebElement itemPrice = driver.findElement(By.xpath("(//div[@class='right-block']/div[@class='content_price']/span[@class='old-price product-price' or @class='price product-price'])[2]"));
        //Assert.assertTrue(itemPrice.getText().contains("30.51"));
        int itemPriceCount = driver.findElements(By.xpath("(//div[@class='right-block']/div[@class='content_price']/span[@class='price product-price'])")).size();

        ArrayList<Float> priceList = new ArrayList<>();
        for (int i  = 1; i<=itemPriceCount; i++){
            if(driver.findElements(By.xpath("(((//div[@class='right-block']/div[@class='content_price'])["+i+"])//span[@class = 'old-price product-price'])")).size()>0){
                priceList.add(Float.valueOf(driver.findElement(By.xpath("(((//div[@class='right-block']/div[@class='content_price'])["+i+"])//span[@class = 'old-price product-price'])")).getText().substring(1)));
            } else {
                priceList.add(Float.valueOf(driver.findElement(By.xpath("(//div[@class='right-block']/div[@class='content_price']/span[@class='price product-price'])"+"["+i+"]")).getText().substring(1)));
            }
        }
        ArrayList<Float> priceListSorted = new ArrayList<>(priceList);
        Collections.sort(priceListSorted,Collections.reverseOrder());
        Assert.assertTrue(priceList.equals(priceListSorted));



    }

    @Test(priority = 2)
    void checkItemPriceAndNameInCart(){

        actions = new Actions(driver);
        String firstItemPrice = driver.findElement(By.xpath("(//div[@class='right-block']/div[@class='content_price']/span[@class = 'price product-price'])")).getText().substring(1);
        String firstItemName = driver.findElement(By.xpath("(//div[@class='right-block']//a[@class='product-name'])")).getText();
        WebElement buttonAddToCart = driver.findElement(By.xpath("(//div[@class = 'product-container']//a[@title='Add to cart'])"));
        actions.moveToElement(driver.findElement(By.xpath("(//div[@class='product-container'])"))).perform();
        buttonAddToCart.click();
        WebElement buttonProceedToCheckout = driver.findElement(By.id("layer_cart")).findElement(By.xpath("//*[@title = 'Proceed to checkout']"));
        buttonProceedToCheckout.click();
        String totalAmountAtCart = driver.findElement(By.xpath("//td[@class='cart_total']//span[@class='price']")).getText().substring(1);
        String productNameInCart = driver.findElement(By.linkText(firstItemName)).getText();
        Assert.assertEquals(totalAmountAtCart,firstItemPrice);
        Assert.assertEquals(productNameInCart,firstItemName);

    }


    @AfterSuite
    void closeDriver(){

        driver.quit();

    }

}
