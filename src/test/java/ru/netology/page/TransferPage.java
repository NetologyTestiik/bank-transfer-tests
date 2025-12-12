package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class TransferPage {
    private SelenideElement amountField = $("[data-test-id='amount'] input");
    private SelenideElement fromField = $("[data-test-id='from'] input");
    private SelenideElement transferButton = $("[data-test-id='action-transfer']");
    private SelenideElement cancelButton = $("[data-test-id='action-cancel']");
    private SelenideElement errorNotification = $("[data-test-id='error-notification'] .notification__content");
    private SelenideElement amountError = $("[data-test-id='amount'] .input__sub");
    private SelenideElement fromError = $("[data-test-id='from'] .input__sub");

    public SelenideElement getAmountField() {
        return amountField;
    }

    public SelenideElement getFromField() {
        return fromField;
    }

    public SelenideElement getTransferButton() {
        return transferButton;
    }

    public SelenideElement getCancelButton() {
        return cancelButton;
    }

    public SelenideElement getErrorNotification() {
        return errorNotification;
    }

    public SelenideElement getAmountError() {
        return amountError;
    }

    public SelenideElement getFromError() {
        return fromError;
    }

    public void makeTransfer(String amount, String fromCard) {
        amountField.setValue(amount);
        fromField.setValue(fromCard);
        transferButton.click();
    }

    public void clickCancel() {
        cancelButton.click();
    }
}
