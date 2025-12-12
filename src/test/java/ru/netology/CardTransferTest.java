package ru.netology;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.TransferPage;

import static com.codeborne.selenide.Selenide.open;

public class CardTransferTest {
    
    @BeforeEach
    void setup() {
        // Настройка браузера
        Configuration.browserSize = "1280x800";
        Configuration.timeout = 10000;
    }
    
    @Test
    void shouldSuccessfullyLoginAndAccessDepositFeature() {
        // Получаем тестовые данные через DataHelper
        DataHelper.AuthInfo authInfo = DataHelper.getAuthInfo();
        DataHelper.VerificationCode verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        
        // Используем Page Objects для авторизации
        open("http://localhost:9999");
        
        LoginPage loginPage = new LoginPage();
        var verificationPage = loginPage.validLogin(authInfo);
        DashboardPage dashboardPage = verificationPage.validVerify(verificationCode);
        
        // Проверяем, что мы на странице Dashboard и видны карты
        dashboardPage.cardsShouldBeVisible();
        
        // Проверяем кнопку "Пополнить" и кликаем на нее
        TransferPage transferPage = dashboardPage.clickDepositButton();
        
        // Проверяем, что форма перевода открылась
        transferPage.getAmountField().shouldBe(com.codeborne.selenide.Condition.visible);
        transferPage.getFromField().shouldBe(com.codeborne.selenide.Condition.visible);
        transferPage.getTransferButton().shouldBe(com.codeborne.selenide.Condition.visible);
    }
}
