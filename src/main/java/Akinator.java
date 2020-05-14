
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class Akinator {
    public static WebDriver driver;

    public static void main(String[] args) throws InterruptedException, NoSuchElementException {
        initiateGame();
        answerQuestions();
        confirmYourHero();

        endGame();
    }

    //    Різниця між implicitlyWait & sleep. Розібратися що коли краще використовувати для економії часу на проходження
    public static void initiateGame() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        secWait(10);
        driver.get("https://ru.akinator.com");
        clickCss("a[href='/game']");
    }

    private static void secWait(int sec) {
        driver.manage().timeouts().implicitlyWait(sec, TimeUnit.SECONDS);
    }

    public static void endGame() {
        driver.quit();
    }

    public static void showFiveOptions() {

        String question = driver.findElement(By.xpath("//p[@class='question-text']")).getText();
        System.out.println("Question: " + question);
        System.out.println("Варианты ответа:");
        String first = textCss("a[id='a_yes']");
        System.out.println(first + " - 1");

        System.out.println(textCss("a[id='a_no']") + " - 2");
        System.out.println(textCss("a[id='a_dont_know']") + " - 3");
        System.out.println(textCss("a[id='a_probably']") + " - 4");
        System.out.println(textCss("a[id='a_probaly_not']") + " - 5");
        try {
            System.out.println(textCss("span[class='back-button-text']") + " - 6");
        } catch (Exception e){
            System.out.print("");
        }
    }

    public static int showTwoOptions() {
        String yes = textCss("a[id='a_propose_yes']");
        String no = textCss("a[id='a_propose_no']");
        System.out.println("--------------------");
        System.out.println("| "+yes+" - 1 | "+no+" - 2 |");
        System.out.println("--------------------");


        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();

        switch (option) {
            case 1:
                return 1;
            case 2:
                return 2;
            default:
                System.out.println("Этого варианта не существует. Попробуйте еще раз. Спасибо!");
                showTwoOptions();
        }
        return 0;
    }

    public static void answerQuestions() throws InterruptedException, NoSuchElementException {
        TimeUnit.SECONDS.sleep(5);
        while (isElementPresent()) {
            System.out.println(driver.findElement(By.xpath("//p[@class='question-text']")).getText());

            showFiveOptions();
            chooseYourOption();
            TimeUnit.SECONDS.sleep(5);
        }
    }
    //* переписав простіше
    public static boolean isElementPresent() {
        try {
            driver.findElement(By.xpath("//p[@class='question-text']"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static void chooseYourOption() {
        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();

        switch (option) {
//Потрібно зрозуміти різницю між вибором по cssSelector-y i xpath і вибрати краще.
            case 1:
                clickCss("a[id='a_yes']");
                break;
            case 2:
                clickCss("a[id='a_no']");
                break;
            case 3:
                clickCss("a[id='a_dont_know']");
                break;
            case 4:
                clickCss("a[id='a_probably']");
                break;
            case 5:
                clickCss("a[id='a_probaly_not']");
                break;
            case 6:
                clickCss("a[id='a_cancel_answer']");
                break;

//default + break заставляє ще раз вибрати, чи все таки потрібно примусово запустити цей switch - case це зробити?
            //* я так розумію, що в default не треба писати break. Так як break просто перериває перевірку умов.
// Зробив так, але ще не тестував.
            default:
                System.out.println("Этого варианта не существует. Попробуйте еще раз. Спасибо!");
                chooseYourOption();
        }
    }

    public static void confirmYourHero() throws InterruptedException, NoSuchElementException {
        if (!driver.findElement(By.xpath("//span[@class='proposal-title']")).isEnabled()) {
            System.out.println("Извините произошла ошибка. Попробуйте еще раз!");
            return;
        } else {
            String answer = driver.findElement(By.xpath("//span[@class='proposal-title']")).getText() + " (" +
                    driver.findElement(By.xpath("//span[@class='proposal-subtitle']")).getText() + ")";

            System.out.println("Я думаю это: " + answer);
        }

        confirmProposal();
    }

    public static void confirmProposal() throws InterruptedException, NoSuchElementException {
        if (showTwoOptions() == 1) {
            clickCss("a[id='a_propose_yes']");
//  Підтягнути текст з Акінатора
            System.out.println("Я знал! Спасибо за участие!");
            endGame();
        } else {
            clickCss("a[id='a_propose_no']");
            continueGame();
        }
    }

    public static void continueGame() throws InterruptedException, NoSuchElementException{
        System.out.println(driver.findElement(By.xpath("//span[@class='proposal-title']")).getText());
        if (showTwoOptions() == 1) {
            clickCss("a[id='a_continue_yes']");
            answerQuestions();
            confirmYourHero();
        } else {
            clickCss("a[id='a_continue_no']");
            enterYourHero();
        }
    }

    public static void enterYourHero() throws InterruptedException {
        TimeUnit.SECONDS.sleep(5);

        System.out.println(driver.findElement(By.xpath("//div[@class='col-md-12 page-formulaire soundlike3']")).getText());
        System.out.println("Имя:");

        Scanner scanner = new Scanner(System.in);
        String value = new String();
        value = scanner.nextLine();

        driver.findElement(By.cssSelector("input[name='name']")).sendKeys(value + Keys.ENTER);

        TimeUnit.SECONDS.sleep(5);

        System.out.println(driver.findElement(By.xpath("//div[@class='col-md-12 page-formulaire aki-formulaire soundlike2']")).getText());
        System.out.println("Имя:");

        value = scanner.nextLine();
        driver.findElement(By.cssSelector("input[name='name']")).sendKeys(value + Keys.ENTER);

        System.out.println("Описание:");
        value = scanner.nextLine();
        driver.findElement(By.cssSelector("input[name='name']")).sendKeys(value);

        clickCss("input[id='add_perso']");
    }
    private static void clickCss(String selector) {
        driver.findElement(By.cssSelector(selector)).click();
    }
    private static String textCss(String selector) {
        return driver.findElement(By.cssSelector(selector)).getText();
    }

}
