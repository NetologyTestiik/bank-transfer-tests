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

public class CardTransferTest {
    
    @BeforeEach
    void setup() {
        // Настройка браузера - ТОЛЬКО размер, timeout указывается в should()
        Configuration.browserSize = "1280x800";
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
        // Указываем timeout для каждого условия
        dashboardPage.getFirstCard().shouldBe(Condition.visible, 15000);
        dashboardPage.getSecondCard().shouldBe(Condition.visible, 15000);
        
        // Проверяем кнопку "Пополнить" и кликаем на нее
        TransferPage transferPage = dashboardPage.clickDepositOnSecondCard();
        
        // Проверяем, что форма перевода открылась с указанием таймаута для каждого элемента
        transferPage.getAmountField().shouldBe(Condition.visible, 15000);
        transferPage.getFromField().shouldBe(Condition.visible, 15000);
        transferPage.getToField().shouldBe(Condition.visible, 15000);
        transferPage.getTransferButton().shouldBe(Condition.visible, 15000);
        transferPage.getCancelButton().shouldBe(Condition.visible, 15000);
    }
}
