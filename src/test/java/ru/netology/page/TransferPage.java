package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class TransferPage {
    
    public TransferPage() {
        // Проверяем, что форма перевода открыта
        $("[data-test-id='amount']").shouldBe(visible);
    }
    
    // Методы для работы с формой перевода
    public SelenideElement getAmountField() {
        return $("[data-test-id='amount'] input");
    }
    
    public SelenideElement getFromField() {
        return $("[data-test-id='from'] input");
    }
    
    public SelenideElement getTransferButton() {
        return $("[data-test-id='action-transfer']");
    }
}
