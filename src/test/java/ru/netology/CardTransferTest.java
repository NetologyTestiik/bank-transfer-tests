package ru.netology;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.TransferPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardTransferTest {
    
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
}
