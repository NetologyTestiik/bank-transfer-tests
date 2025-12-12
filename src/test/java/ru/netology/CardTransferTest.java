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
        // Настройка браузера
        Configuration.browserSize = "1280x800";
        Configuration.headless = false; // для визуального наблюдения
    }
    
    @Test
    void shouldSuccessfullyTransferMoneyBetweenCards() {
        // 1. АВТОРИЗАЦИЯ
        DataHelper.AuthInfo authInfo = DataHelper.getAuthInfo();
        DataHelper.VerificationCode verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        
        open("http://localhost:9999");
        
        LoginPage loginPage = new LoginPage();
        var verificationPage = loginPage.validLogin(authInfo);
        DashboardPage dashboardPage = verificationPage.validVerify(verificationCode);
        
        // Проверяем, что мы на странице Dashboard
        dashboardPage.getFirstCard().shouldBe(Condition.visible, 15000);
        dashboardPage.getSecondCard().shouldBe(Condition.visible, 15000);
        
        // 2. ПОЛУЧАЕМ НАЧАЛЬНЫЕ БАЛАНСЫ
        int initialBalanceFirstCard = dashboardPage.getCardBalance("0001");
        int initialBalanceSecondCard = dashboardPage.getCardBalance("0002");
        
        System.out.println("Начальный баланс карты 0001: " + initialBalanceFirstCard + " руб.");
        System.out.println("Начальный баланс карты 0002: " + initialBalanceSecondCard + " руб.");
        
        // 3. ВЫПОЛНЯЕМ ПЕРЕВОД
        int transferAmount = 1000; // Сумма перевода
        String sourceCard = DataHelper.getFirstCardInfo().getCardNumber(); // Карта-источник
        
        // Нажимаем "Пополнить" на второй карте (куда переводим)
        TransferPage transferPage = dashboardPage.clickDepositOnSecondCard();
        
        // Проверяем, что форма открылась
        transferPage.getAmountField().shouldBe(Condition.visible, 15000);
        transferPage.getFromField().shouldBe(Condition.visible, 15000);
        transferPage.getTransferButton().shouldBe(Condition.visible, 15000);
        
        // Заполняем форму перевода
        transferPage.makeTransfer(String.valueOf(transferAmount), sourceCard);
        
        // 4. ПРОВЕРЯЕМ РЕЗУЛЬТАТЫ ПЕРЕВОДА
        // Ждем возврата на Dashboard
        dashboardPage.getFirstCard().shouldBe(Condition.visible, 15000);
        dashboardPage.getSecondCard().shouldBe(Condition.visible, 15000);
        
        // Получаем конечные балансы
        int finalBalanceFirstCard = dashboardPage.getCardBalance("0001");
        int finalBalanceSecondCard = dashboardPage.getCardBalance("0002");
        
        System.out.println("Конечный баланс карты 0001: " + finalBalanceFirstCard + " руб.");
        System.out.println("Конечный баланс карты 0002: " + finalBalanceSecondCard + " руб.");
        
        // 5. ПРОВЕРЯЕМ КОРРЕКТНОСТЬ ПЕРЕВОДА
        // Проверяем, что баланс первой карты уменьшился на сумму перевода
        assertEquals(initialBalanceFirstCard - transferAmount, finalBalanceFirstCard, 
            "Баланс первой карты должен уменьшиться на " + transferAmount + " руб.");
        
        // Проверяем, что баланс второй карты увеличился на сумму перевода
        assertEquals(initialBalanceSecondCard + transferAmount, finalBalanceSecondCard,
            "Баланс второй карты должен увеличиться на " + transferAmount + " руб.");
        
        // Проверяем, что общая сумма средств не изменилась
        int totalBefore = initialBalanceFirstCard + initialBalanceSecondCard;
        int totalAfter = finalBalanceFirstCard + finalBalanceSecondCard;
        assertEquals(totalBefore, totalAfter,
            "Общая сумма средств на картах не должна измениться");
    }
    
    @Test
    void shouldNotAllowTransferWhenAmountExceedsBalance() {
        // 1. АВТОРИЗАЦИЯ
        DataHelper.AuthInfo authInfo = DataHelper.getAuthInfo();
        DataHelper.VerificationCode verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        
        open("http://localhost:9999");
        
        LoginPage loginPage = new LoginPage();
        var verificationPage = loginPage.validLogin(authInfo);
        DashboardPage dashboardPage = verificationPage.validVerify(verificationCode);
        
        // 2. ПОЛУЧАЕМ БАЛАНС КАРТЫ-ИСТОЧНИКА
        int balanceFirstCard = dashboardPage.getCardBalance("0001");
        
        // 3. ПЫТАЕМСЯ ПЕРЕВЕСТИ СУММУ БОЛЬШЕ БАЛАНСА
        int transferAmount = balanceFirstCard + 1000; // Сумма превышает баланс
        String sourceCard = DataHelper.getFirstCardInfo().getCardNumber();
        
        TransferPage transferPage = dashboardPage.clickDepositOnSecondCard();
        
        // Заполняем форму слишком большой суммой
        transferPage.makeTransfer(String.valueOf(transferAmount), sourceCard);
        
        // 4. ПРОВЕРЯЕМ, ЧТО ПОЯВИЛАСЬ ОШИБКА
        // Ожидаем сообщение об ошибке
        transferPage.getErrorNotification().shouldBe(Condition.visible, 15000)
            .shouldHave(Condition.text("Недостаточно средств"));
        
        // 5. ПРОВЕРЯЕМ, ЧТО БАЛАНСЫ НЕ ИЗМЕНИЛИСЬ
        // Нажимаем "Отмена" для возврата на Dashboard
        transferPage.clickCancel();
        
        dashboardPage.getFirstCard().shouldBe(Condition.visible, 15000);
        
        int balanceAfterAttempt = dashboardPage.getCardBalance("0001");
        assertEquals(balanceFirstCard, balanceAfterAttempt,
            "Баланс не должен измениться при неудачном переводе");
    }
    
    @Test
    void shouldNotAllowTransferWithEmptyFields() {
        // 1. АВТОРИЗАЦИЯ
        DataHelper.AuthInfo authInfo = DataHelper.getAuthInfo();
        DataHelper.VerificationCode verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        
        open("http://localhost:9999");
        
        LoginPage loginPage = new LoginPage();
        var verificationPage = loginPage.validLogin(authInfo);
        DashboardPage dashboardPage = verificationPage.validVerify(verificationCode);
        
        // 2. ПЕРЕХОДИМ НА ФОРМУ ПЕРЕВОДА
        TransferPage transferPage = dashboardPage.clickDepositOnSecondCard();
        
        // 3. ПЫТАЕМСЯ ОТПРАВИТЬ ПУСТУЮ ФОРМУ
        transferPage.getTransferButton().click();
        
        // 4. ПРОВЕРЯЕМ СООБЩЕНИЯ ОБ ОШИБКАХ ВАЛИДАЦИИ
        transferPage.getAmountField().shouldHave(Condition.cssClass("input_invalid"));
        transferPage.getFromField().shouldHave(Condition.cssClass("input_invalid"));
        
        // ИЛИ проверяем текстовые сообщения об ошибках
        transferPage.getAmountError().shouldBe(Condition.visible)
            .shouldHave(Condition.text("Поле обязательно для заполнения"));
    }
}
