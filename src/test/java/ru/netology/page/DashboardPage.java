package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.netology.page.TransferPage;

import static com.codeborne.selenide.Selenide.*;

public class DashboardPage {
    
    public DashboardPage() {
        // Проверяем, что мы на странице Dashboard
        $("[data-test-id='dashboard']").shouldBe(Condition.visible);
    }

    // Проверка видимости карт с четырьмя цифрами
    public void cardsShouldBeVisible() {
        $("body").shouldHave(Condition.text("**** 0001"));
        $("body").shouldHave(Condition.text("**** 0002"));
    }

    // Нажатие на кнопку "Пополнить" для определенной карты
    public TransferPage clickDepositOnFirstCard() {
        $("[data-test-id='92df3f1c-a033-48e6-8390-206f6b1f56c0'] [data-test-id='action-deposit']")
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text("Пополнить"))
            .click();
        return new TransferPage();
    }
    
    public TransferPage clickDepositOnSecondCard() {
        $("[data-test-id='0f3f5c2a-249e-4c3d-8287-09f7a039391d'] [data-test-id='action-deposit']")
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text("Пополнить"))
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
    // Ищем баланс в разных форматах
    String[] patterns = {"баланс: ", "Баланс: ", "balance: "};
    
    for (String pattern : patterns) {
        int startIndex = text.indexOf(pattern);
        if (startIndex != -1) {
            startIndex += pattern.length();
            // Ищем конец числа (руб., ₽, р., или конец строки)
            int endIndex = text.length();
            String[] endMarkers = {" р.", " руб.", " ₽", "\n", " "};
            for (String marker : endMarkers) {
                int markerIndex = text.indexOf(marker, startIndex);
                if (markerIndex != -1 && markerIndex < endIndex) {
                    endIndex = markerIndex;
                }
            }
            
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
    return 0;
}
    
    // Дополнительные методы для работы с картами
    public SelenideElement getFirstCardElement() {
        return $("[data-test-id='92df3f1c-a033-48e6-8390-206f6b1f56c0']")
            .shouldBe(Condition.visible, 15000);
    }
    
    public SelenideElement getSecondCardElement() {
        return $("[data-test-id='0f3f5c2a-249e-4c3d-8287-09f7a039391d']")
            .shouldBe(Condition.visible, 15000);
    }
    
    public SelenideElement getDepositButtonForFirstCard() {
        return $("[data-test-id='92df3f1c-a033-48e6-8390-206f6b1f56c0'] [data-test-id='action-deposit']")
            .shouldBe(Condition.visible, 15000);
    }
    
    public SelenideElement getDepositButtonForSecondCard() {
        return $("[data-test-id='0f3f5c2a-249e-4c3d-8287-09f7a039391d'] [data-test-id='action-deposit']")
            .shouldBe(Condition.visible, 15000);
    }
}
