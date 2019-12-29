package ru.kvrachu;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.chrome.ChromeDriver;

public class WebDriverSetting {
    public ChromeDriver myDriver;

    @Before
    public void setUp(){
        System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\home-pc\\Desktop\\chromedriver_win32\\chromedriver.exe"); //проверяй путь chromedriver
        myDriver = new ChromeDriver();
    }

    @After
    public void close(){
        myDriver.close();
    }
}
