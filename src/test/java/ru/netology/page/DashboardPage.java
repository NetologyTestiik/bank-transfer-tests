package ru.netology.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.Condition;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class DashboardPage {
    
    public DashboardPage() {
        // Проверяем, что мы на странице Dashboard
        $("[data-test-id='dashboard']").shouldBe(visible);
    }
    
    // Проверка наличия карт по последним 4 цифрам
    public void cardsShouldBeVisible() {
        $("body").shouldHave(text("**** 0001"));
        $("body").shouldHave(text("**** 0002"));
    }
    
    // Проверка и клик по кнопке "Пополнить" для конкретной карты
    public TransferPage clickDepositButton() {
        $("[data-test-id='action-deposit']")
                .shouldBe(visible)
                .shouldHave(text("Пополнить"))
                .click();
        return new TransferPage();
    }
    
    // Метод для получения баланса (если нужно в будущем)
    public int getCardBalance(String lastFourDigits) {
        ElementsCollection cards = $$(".list__item");
        for (SelenideElement card : cards) {
            if (card.text().contains(lastFourDigits)) {
                String text = card.text();
                return extractBalance(text);
            }
        }
        return 0;
    }
    
    private int extractBalance(String text) {
        String balanceStart = "баланс: ";
        String balanceFinish = " р.";
        int start = text.indexOf(balanceStart);
        int finish = text.indexOf(balanceFinish);
        if (start == -1 || finish == -1) {
            return 0;
        }
        String value = text.substring(start + balanceStart.length(), finish).trim();
        return Integer.parseInt(value);
    }
}
