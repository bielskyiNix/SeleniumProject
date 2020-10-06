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

    //Открываем вебдрайвер перед тест сьютом
    @BeforeSuite
    void openChromeDriver(){

        System.setProperty("webdriver.chrome.driver","C:\\Users\\bielskyi\\IdeaProjects\\automation\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();

    }


    @Test(priority = 0,dataProvider = "searchQueryAndResult")
    public void searchResultTest(String searchQuery, String searchResult){

        driver.get("http://automationpractice.com/");//перейти на сайт
        WebElement searchField = driver.findElement(By.name("search_query"));
        WebElement searchButton = driver.findElement(By.name("submit_search"));

        searchField.sendKeys(searchQuery);
        searchButton.click();

        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS); //ожидание загрузки страницы
        String actualSearchResult = driver.findElement(By.xpath("//h1[@class='page-heading  product-listing']//span[@class='lighter']")).getText();

        Assert.assertEquals(actualSearchResult,searchResult); //Проверка что актуальный результат поиска совпадает с ожидаемым результатом поиска

    }

    @Test(priority = 1)
    public void sortByPriceTest(){

        //Сортировка результатов поиска по цене
        WebElement sortByPicklist = driver.findElement(By.id("uniform-selectProductSort"));
        WebElement sortValue = driver.findElement(By.xpath("//div[@id='uniform-selectProductSort']//option[@value='price:desc']"));

        sortByPicklist.click();
        sortValue.click();

        WebElement selectedSortValue = driver.findElement(By.xpath("//div[@id='uniform-selectProductSort']//option[@selected='selected']"));
        Assert.assertEquals(selectedSortValue.getText(),"Price: Highest first"); //проверка что выбрано значение "Price: Highest first"

        int itemPriceCount = driver.findElements(By.xpath("(//div[@class='right-block']/div[@class='content_price']/span[@class='price product-price'])")).size();  //колличество найденных товаров на странице
        ArrayList<Float> priceList = new ArrayList<>(); // создаем список и далее

        //заполняем список priceList, ценами товаров, если товар со скидкой то записываем его старую цену (так как сортирует по ней)

        for (int i  = 1; i<=itemPriceCount; i++){
            if(driver.findElements(By.xpath("(((//div[@class='right-block']/div[@class='content_price'])["+i+"])//span[@class = 'old-price product-price'])")).size()>0){
                priceList.add(Float.valueOf(driver.findElement(By.xpath("(((//div[@class='right-block']/div[@class='content_price'])["+i+"])//span[@class = 'old-price product-price'])")).getText().substring(1)));
            } else {
                priceList.add(Float.valueOf(driver.findElement(By.xpath("(//div[@class='right-block']/div[@class='content_price']/span[@class='price product-price'])"+"["+i+"]")).getText().substring(1)));
            }
        }

        ArrayList<Float> priceListSorted = new ArrayList<>(priceList); //копируем список priceList в новый
        Collections.sort(priceListSorted,Collections.reverseOrder()); //сортируем новый список

        Assert.assertTrue(priceList.equals(priceListSorted)); // Проверка того что цены всех элементов на странице стоят в том же порядке что и в отсортированном списке

    }

    @Test(priority = 2)
    void checkItemPriceAndNameInCart(){

        //Добавление элемента в корзину и проверка цены в корзине

        actions = new Actions(driver);

        String firstItemPrice = driver.findElement(By.xpath("(//div[@class='right-block']/div[@class='content_price']/span[@class = 'price product-price'])")).getText().substring(1); //сохраняем цену первого объекта
        String firstItemName = driver.findElement(By.xpath("(//div[@class='right-block']//a[@class='product-name'])")).getText();  //сохраняем название первого объекта

        WebElement buttonAddToCart = driver.findElement(By.xpath("(//div[@class = 'product-container']//a[@title='Add to cart'])"));
        actions.moveToElement(driver.findElement(By.xpath("(//div[@class='product-container'])"))).perform();//наводим мышкой на поле с товаром чтобы появилась кнопка "Add to cart"
        buttonAddToCart.click();

        WebElement buttonProceedToCheckout = driver.findElement(By.id("layer_cart")).findElement(By.xpath("//*[@title = 'Proceed to checkout']"));
        buttonProceedToCheckout.click(); //переходим в корзину нажав кнопку 'Proceed to checkout' во всплывающем окне

        String totalAmountAtCart = driver.findElement(By.xpath("//td[@class='cart_total']//span[@class='price']")).getText().substring(1); // Находим и сохраняем цену товара в корзине
        String productNameInCart = driver.findElement(By.linkText(firstItemName)).getText(); // Находим и сохраняем название товара в корзине

        Assert.assertEquals(totalAmountAtCart,firstItemPrice); // Проверяем соответствие цены со страницы поиска с ценой в корзине
        Assert.assertEquals(productNameInCart,firstItemName); // Проверяем соответствие названия товара со страницы поиска с названиием товара в корзине
    }

    //Закрываем вебдравйвер после всех тестов

    @AfterSuite
    void closeDriver(){

        driver.quit();

    }

}
