package com.mastugin.test;

import com.mastugin.model.TestProjectInfo;
import com.mastugin.rule.DriverRule;
import com.mastugin.rule.DriverSetupRule;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateTestProjectTest {

    @ClassRule
    public static DriverSetupRule driverSetupRule = DriverSetupRule.getInstance();
    @Rule
    public DriverRule driverRule = DriverRule.getInstance();
    protected WebDriver driver;
    private String TEST_LINK_BASE_URI;
    private String USER_LOGIN;
    private String USER_PASSWORD;
    private TestProjectInfo TEST_PROJECT_INFO;


    @Before
    public void setUp() {
        driver = driverRule.getDriver();
        initTestData();
    }

    private void initTestData() {
        TEST_LINK_BASE_URI = "http://localhost:8080/testlink/";
        USER_LOGIN = "admin";
        USER_PASSWORD = "admin";
        TEST_PROJECT_INFO = TestProjectInfo.getInstance()
                .withName("Project_" + UUID.randomUUID())
                .withPrefix(UUID.randomUUID().toString().substring(0, 5))
                .withDescription("Skibidi wap-pa-pa-pa-pa-pa-pa-pa-pa ")
                .withIsRequirements(false)
                .withIsActive(true)
                .withIsPublic(false);
    }

    //Время выполения на моей машине ~ 1m 8s (объектов в таблицк 20)
    //Время выполения на моей машине без поиска значения Public ~ 12s (объектов в таблицк 20)
    @Test
    public void createTestProject() throws InterruptedException {
        openPage(TEST_LINK_BASE_URI);
        signIn(USER_LOGIN, USER_PASSWORD);
        goToTestProjectManagement();
        openCreateTestProjectForm();
        fillTestProjectData(TEST_PROJECT_INFO);
        clickCreateButton();
        checkTestProjectData(TEST_PROJECT_INFO);
    }

    //Время выполнения на моей машине ~ 5s (объектов в таблицк 20)
    @Test
    public void createTestProjectAlternativeCheck() throws InterruptedException {
        openPage(TEST_LINK_BASE_URI);
        signIn(USER_LOGIN, USER_PASSWORD);
        goToTestProjectManagement();
        openCreateTestProjectForm();
        fillTestProjectData(TEST_PROJECT_INFO);
        clickCreateButton();
        checkTestProjectDataByFindingName(TEST_PROJECT_INFO);
    }

    /**
     * ОБЩИЕ МЕТОДЫ
     */
    private void openPage(String path) {
        driver.get(path);
    }

    private void signIn(String login, String pass) {
        driver.findElement(By.id("tl_login")).sendKeys(login);
        driver.findElement(By.id("tl_password")).sendKeys(pass);
        driver.findElement(By.cssSelector("input[type=submit]")).click();
    }

    private void goToTestProjectManagement() {
        driver.switchTo().frame("mainframe");
        driver.findElement(By.linkText("Test Project Management")).click();
    }

    /**
     * Создание проекта
     */

    private void openCreateTestProjectForm() throws InterruptedException {
        driver.findElement(By.cssSelector("input#create")).click();
        //явное ожидание очень плохо, нужно использовать Fluent ожидание,
        //пока незнаю как
        Thread.sleep(1000);
    }

    private void fillTestProjectData(TestProjectInfo project) {
        fillName(project.getName());
        fillPrefix(project.getPrefix());
        fillDescription(project.getDescription());
        fillIsRequirements(project.isRequirements());
        fillIsPublic(project.isPublic());
        fillisActive(project.isActive());
    }

    private void fillName(String value) {
        driver.findElement(By.cssSelector("input[name=tprojectName]")).sendKeys(value);
    }

    private void fillPrefix(String value) {
        driver.findElement(By.cssSelector("input[name=tcasePrefix]")).sendKeys(value);
    }

    private void fillDescription(String value) {
        WebElement frame = driver.findElement(By.cssSelector("iframe.cke_wysiwyg_frame"));
        fillFrame(frame, value);
    }

    private void fillIsRequirements(boolean value) {
        WebElement checkBox = driver.findElement(By.cssSelector("input[name=optReq]"));
        setCheckBoxState(checkBox, value);
    }

    private void fillIsPublic(boolean value) {
        WebElement checkBox = driver.findElement(By.cssSelector("input[name=is_public]"));
        setCheckBoxState(checkBox, value);
    }

    private void fillisActive(boolean value) {
        WebElement checkBox = driver.findElement(By.cssSelector("input[name=active]"));
        setCheckBoxState(checkBox, value);
    }

    private void clickCreateButton() {
        driver.findElement(By.cssSelector("input[name=doActionButton]")).click();

    }

    private void checkTestProjectData(TestProjectInfo data) {
        WebElement table = driver.findElement(By.cssSelector("#item_view"));
        List<TestProjectInfo> projectsList = parseTable(table);
        assertThat("Expected project not found!", projectsList, hasItem(data));
    }

    /**
     * ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
     */

    private void fillFrame(WebElement iframe, String value) {
        driver.switchTo().frame(iframe);
        driver.findElement(By.xpath("//body[@contenteditable='true']")).sendKeys(value);
        driver.switchTo().parentFrame();
    }

    private void setCheckBoxState(WebElement checkBox, boolean isSelect) {
        if (isSelect) {
            if (!checkBox.isSelected()) {
                checkBox.click();
            }
        } else {
            if (checkBox.isSelected()) {
                checkBox.click();
            }
        }
    }

    /**
     * Уже написав, понял, что парсинг всей таблицы - очень затратно по времени. Причина далее
     */
    private List<TestProjectInfo> parseTable(WebElement table) {
        List<TestProjectInfo> result = new ArrayList<>();
        List<WebElement> rows = table.findElement(By.cssSelector("tbody")).findElements(By.cssSelector("tr[role=row]"));
        rows.stream().forEach(
                row -> result.add(parseRow(row))
        );
        return result;
    }

    private TestProjectInfo parseRow(WebElement row) {
        List<WebElement> cells = row.findElements(By.cssSelector("td"));
        return TestProjectInfo.getInstance()
                .withName(cells.get(0).getText())
                .withDescription(cells.get(1).getText())
                .withPrefix(cells.get(2).getText())
                .withIsRequirements(isEnabled(cells.get(5)))
                .withIsActive(isActive(cells.get(6)))
                .withIsPublic(isPublic(cells.get(7)));
    }

    private boolean isEnabled(WebElement element) {
        return element.findElement(By.tagName("input")).getAttribute("title").contains("Enabled");
    }

    private boolean isActive(WebElement element) {
        return element.findElement(By.tagName("input")).getAttribute("title").contains("Active");
    }

    /**
     * Данный метод - причина сильного снижения производительности теста.
     * без него парсинг всей таблицы заметно быстрее (см комментарий к тесту). Данная ячейка
     * работает следующим образом: если public=true, то есть внутри ячейки img.
     * если public=false, то ячейка пустая. Почему-то, если я пытался пулчить список дитей
     * ячейки, всегда возращается null (element.getAttribute("children")).
     */
    private boolean isPublic(WebElement element) {
        List<WebElement> children = element.findElements(By.xpath(".//*"));
        if (children.size() == 1) {
            return true;
        }
        return false;
    }

    /**
     * Альтернативный вариант поиска строки в таблице по имени
     */
    private TestProjectInfo findInTableByName(WebElement table, String searchName) {
        List<WebElement> rows = table.findElement(By.cssSelector("tbody")).findElements(By.cssSelector("tr[role=row]"));
        WebElement foundRow = rows.stream()
                .filter(row -> parseName(row).equals(searchName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("The cell with the name=" + searchName + " was not found"));
        return parseRow(foundRow);
    }

    private String parseName(WebElement row) {
        List<WebElement> cells = row.findElements(By.cssSelector("td"));
        return cells.get(0).getText();
    }

    private void checkTestProjectDataByFindingName(TestProjectInfo data) {
        WebElement table = driver.findElement(By.cssSelector("#item_view"));
        TestProjectInfo actual = findInTableByName(table, data.getName());
        assertThat("Expected project not found!", actual, CoreMatchers.equalTo(data));
    }
}
