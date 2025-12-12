package ru.netology.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DashboardPage {
    
    // КОНСТРУКТОР - без проверок
    public DashboardPage() {
        // НИКАКИХ shouldBe() в конструкторе
    }

    // МЕТОДЫ ВОЗВРАЩАЮТ ЭЛЕМЕНТЫ (без проверок)
    public SelenideElement getFirstCardElement() {
        return $("[data-test-id='92df3f1c-a033-48e6-8390-206f6b1f56c0']");
    }
    
    public SelenideElement getSecondCardElement() {
        return $("[data-test-id='0f3f5c2a-249e-4c3d-8287-09f7a039391d']");
    }
    
    public SelenideElement getDepositButtonForFirstCard() {
        return $("[data-test-id='92df3f1c-a033-48e6-8390-206f6b1f56c0'] [data-test-id='action-deposit']");
    }
    
    public SelenideElement getDepositButtonForSecondCard() {
        return $("[data-test-id='0f3f5c2a-249e-4c3d-8287-09f7a039391d'] [data-test-id='action-deposit']");
    }
    
    // МЕТОДЫ ДЕЙСТВИЙ - только клики
    public TransferPage clickDepositOnFirstCard() {
        getDepositButtonForFirstCard().click();
        return new TransferPage();
    }
    
    public TransferPage clickDepositOnSecondCard() {
        getDepositButtonForSecondCard().click();
        return new TransferPage();
    }

    // МЕТОД ДЛЯ ПОЛУЧЕНИЯ БАЛАНСА
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
        
        int startIndex = text.indexOf(balanceStart);
        if (startIndex == -1) return 0;
        
        startIndex += balanceStart.length();
        int endIndex = text.indexOf(balanceFinish, startIndex);
        
        if (endIndex == -1) return 0;
        
        String balanceStr = text.substring(startIndex, endIndex)
            .replaceAll("\\s+", "")
            .replaceAll("[^0-9]", "");
        
        try {
            return Integer.parseInt(balanceStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
