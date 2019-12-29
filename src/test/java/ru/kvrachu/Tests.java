package ru.kvrachu;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tests extends WebDriverSetting {
    Random random = new Random();

    @Test
    public void Test(){
        setConnect();
        //активируем демо-режим, неудача -> меняем регион и снова пытаемся войти (пока не войдет)
        do {
            activateTestMode();
            if(checkLogin())
                System.out.println("Error activate test mode in "+myDriver.findElement(By.xpath("//div[@class='region']/ul/li[@class='selected']/a")).getText());
            else
                break;
            changeRegion();
        }while(checkLogin());

        //выбираем врача с открытой датой и записываемся к нему
        writeToDoctor();
        registerFreeDate();

        //проверка записи
        checkWrite();

        //выходим из демо-режима и проверяемм по ранее сохраненому тайтлу
        deactivateTestMode();
    }

    public void setConnect(){
        try {
            myDriver.get("https://k-vrachu.ru/");
            myDriver.manage().window().maximize();
            myDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        }
        catch (Exception e){
            System.out.println("Error set connection: "+e.getMessage());
        }
    }

    public void activateTestMode(){
        try{
            myDriver.findElement(By.xpath("//div[@class = 'person']/p/a[@class='demo']")).click();
            myDriver.findElement(By.linkText("Включить демо-режим")).click();
        }
        catch (Exception e){
            System.out.println("Error activate TM: "+e.getMessage());
        }
    }

    public void deactivateTestMode(){
        try{
            myDriver.findElement(By.linkText("Выход")).click();
        }
        catch (Exception e){
            System.out.println("Error deactivate TM: "+e.getMessage());
        }
    }

    public boolean checkLogin(){
        try{
            return !myDriver.findElement(By.linkText("Выход")).isDisplayed(); //проверяем на наличие кнопки "Выход" на странице
        }
        catch (Exception e){
            return true;
        }
    }

    public void changeRegion(){
        try{
            WebElement elem;
            myDriver.findElement(By.xpath("//div[@class='region']/ul/li[@class='selected']/a")).click(); //открываем список регионов
            List<WebElement> elements = myDriver.findElements(By.xpath("//div[@class='region']/ul/li/a")); //получаем список всех регионов в List
            if(elements.size()>0) { //обработка в случае нулевого массива
                elem = elements.get(7); //выбираем случайный регион и активируем random.nextInt(elements.size())
                elem.click();
                if (!myDriver.getTitle().equals("Региональный портал медицинских услуг")) { //дополнительная проверка на загрузку страницы
                    System.out.println("Page do`t open!" + elem.getText());
                    Assert.fail();
                }
            }
            else System.out.println("list of regions is empty");
            }
        catch (Exception e){
            System.out.println("!!!Error change region: "+e.getMessage());
            e.printStackTrace();
            Assert.fail();
        }
    }

    public void writeToDoctor(){
        try{
            myDriver.findElement(By.linkText("Записать к врачу")).click();
            changeDoctor();
            while(!takeFreeDate()) {
                myDriver.findElement(By.xpath("//div[@class='content']/ul/li/a")).click();
                changeDoctor();
            }
        }
        catch (Exception e){
            System.out.println("Error write a doctor: "+e.getMessage());
        }
    }

    public void changeDoctor(){
        try {
            List<WebElement> doctors = myDriver.findElements(By.xpath("//dd/ul/li[not(@class='disabledSpec')]/a"));
            if(doctors.size()>0)
                doctors.get(random.nextInt(doctors.size())).click();
            else{
                System.out.println("No Free doctors. Test is block");
                Assert.fail();
            }
        }
        catch (Exception e){
            System.out.println("Error change doctor: "+e.getMessage());
        }
    }

    public boolean takeFreeDate(){
        boolean ok = false;
        try {
            List<WebElement> doctors = myDriver.findElements(By.xpath("//div[@class='Lpuunit']/a"));
            for (WebElement w:doctors) {
                String str = w.getText();
                String pattern="\\d{2}\\.\\d{2}\\.\\d{4}";
                Matcher matcher= Pattern.compile(pattern).matcher(str);
                if(matcher.find()) {
                    w.click();
                    ok = true;
                    break;
                }
            }
            }
        catch (Exception e){
            System.out.println("Error take free date: "+e.getMessage());
        }
        return ok;
    }

    public void registerFreeDate(){
        try{
            WebElement element = myDriver.findElement(By.xpath("//td[@class=' free ']"));
            String temp = element.getText();
            if(!temp.equals("")) {
                element.click();
            }
            else{
                myDriver.findElement(By.xpath("//div[@class='timeTableWeekArrowsRight']")).click();
                registerFreeDate();
            }
            myDriver.findElement(By.linkText("Подтвердить")).click();
        }
        catch (Exception e){
            System.out.println("Error register in free date: "+e.getMessage());
            Assert.fail();
        }
    }

    public void checkWrite(){
        try{
            myDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            String string = myDriver.findElement(By.xpath("//div[@class='content']")).getText();
            Assert.assertTrue(string.contains("Запись в базу данных невозможна"));
        }
        catch (Exception e){
            System.out.println("Error add write: "+e.getMessage());
        }
    }
}
