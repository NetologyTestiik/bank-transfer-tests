package ru.netology;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.TransferPage;

import java.io.IOException;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardTransferTest {
    private static Process serverProcess;
    
    @BeforeAll
    static void startServer() throws IOException, InterruptedException {
        String jarPath = "artifacts/app-ibank-build-for-testers.jar";
        ProcessBuilder processBuilder = new ProcessBuilder(
            "java", "-jar", jarPath, "-port=9999"
        );
        serverProcess = processBuilder.start();
        Thread.sleep(5000);
    }
    
    @AfterAll
    static void stopServer() {
        if (serverProcess != null && serverProcess.isAlive()) {
            serverProcess.destroy();
        }
    }
    
    @BeforeEach
    void setup() {
        // ТОЛЬКО размер браузера
        Configuration.browserSize = "1280x800";
       
    }
    
    @Test
    void shouldSuccessfullyTransferMoneyBetweenCards() {
        DataHelper.AuthInfo authInfo = DataHelper.getAuthInfo();
        DataHelper.VerificationCode verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        
        open("http://localhost:9999");
        
        LoginPage loginPage = new LoginPage();
        var verificationPage = loginPage.validLogin(authInfo);
        DashboardPage dashboardPage = verificationPage.validVerify(verificationCode);
        
        // ВСЕ ПРОВЕРКИ В ТЕСТЕ с таймаутами
        dashboardPage.getFirstCardElement().shouldBe(Condition.visible, 15000);
        dashboardPage.getSecondCardElement().shouldBe(Condition.visible, 15000);
        
        int initialBalanceFirstCard = dashboardPage.getCardBalance("0001");
        int initialBalanceSecondCard = dashboardPage.getCardBalance("0002");
        
        int transferAmount = 1000;
        String sourceCard = DataHelper.getFirstCardInfo().getCardNumber();
        
        TransferPage transferPage = dashboardPage.clickDepositOnSecondCard();
        
        // ПРОВЕРКИ формы перевода в тесте
        transferPage.getAmountField().shouldBe(Condition.visible, 15000);
        transferPage.getFromField().shouldBe(Condition.visible, 15000);
        transferPage.getTransferButton().shouldBe(Condition.visible, 15000);
        transferPage.getCancelButton().shouldBe(Condition.visible, 15000);
        
        transferPage.makeTransfer(String.valueOf(transferAmount), sourceCard);
        
        // Возврат на дашборд
        dashboardPage.getFirstCardElement().shouldBe(Condition.visible, 15000);
        dashboardPage.getSecondCardElement().shouldBe(Condition.visible, 15000);
        
        int finalBalanceFirstCard = dashboardPage.getCardBalance("0001");
        int finalBalanceSecondCard = dashboardPage.getCardBalance("0002");
        
        assertEquals(initialBalanceFirstCard - transferAmount, finalBalanceFirstCard);
        assertEquals(initialBalanceSecondCard + transferAmount, finalBalanceSecondCard);
    }
    
    @Test
    void shouldNotAllowTransferWhenAmountExceedsBalance() {
        DataHelper.AuthInfo authInfo = DataHelper.getAuthInfo();
        DataHelper.VerificationCode verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        
        open("http://localhost:9999");
        
        LoginPage loginPage = new LoginPage();
        var verificationPage = loginPage.validLogin(authInfo);
        DashboardPage dashboardPage = verificationPage.validVerify(verificationCode);
        
        int balanceFirstCard = dashboardPage.getCardBalance("0001");
        int transferAmount = balanceFirstCard + 1000;
        String sourceCard = DataHelper.getFirstCardInfo().getCardNumber();
        
        TransferPage transferPage = dashboardPage.clickDepositOnSecondCard();
        
        transferPage.makeTransfer(String.valueOf(transferAmount), sourceCard);
        
        // ПРОВЕРКА ошибки в тесте
        transferPage.getErrorNotification().shouldBe(Condition.visible, 15000)
            .shouldHave(Condition.text("Недостаточно средств"), 15000);
        
        transferPage.clickCancel();
        dashboardPage.getFirstCardElement().shouldBe(Condition.visible, 15000);
    }
    
    @Test
    void shouldNotAllowTransferWithEmptyFields() {
        DataHelper.AuthInfo authInfo = DataHelper.getAuthInfo();
        DataHelper.VerificationCode verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        
        open("http://localhost:9999");
        
        LoginPage loginPage = new LoginPage();
        var verificationPage = loginPage.validLogin(authInfo);
        DashboardPage dashboardPage = verificationPage.validVerify(verificationCode);
        
        TransferPage transferPage = dashboardPage.clickDepositOnSecondCard();
        
        transferPage.getTransferButton().click();
        
        // ПРОВЕРКИ ошибок валидации в тесте
        transferPage.getAmountField().shouldHave(Condition.cssClass("input_invalid"), 15000);
        transferPage.getFromField().shouldHave(Condition.cssClass("input_invalid"), 15000);
        
        transferPage.getAmountError().shouldBe(Condition.visible, 15000)
            .shouldHave(Condition.text("Поле обязательно для заполнения"), 15000);
    }
}
