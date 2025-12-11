package ru.netology.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Selenide.*;

public class DashboardPage {
    private ElementsCollection cards = $$(".list__item");
    private final String balanceStart = "??????: ";
    private final String balanceFinish = " ?.";
    
    // ????????? ??????? ?? ??????? ?????
    public int getCardBalance(int index) {
        if (cards.size() > index) {
            String text = cards.get(index).text();
            return extractBalance(text);
        }
        return 0;
    }
    
    // ????????? ??????? ?? ?????? ????? (????????? 4 ?????)
    public int getCardBalance(String cardNumber) {
        String lastFourDigits = cardNumber.substring(cardNumber.length() - 4);
        for (SelenideElement card : cards) {
            if (card.text().contains(lastFourDigits)) {
                return extractBalance(card.text());
            }
        }
        return 0;
    }
    
    // ????? ?????????? (????????????? ???)
    public void selectCardToTransfer(String cardNumber) {
        String lastFourDigits = cardNumber.substring(cardNumber.length() - 4);
        for (SelenideElement card : cards) {
            if (card.text().contains(lastFourDigits)) {
                card.$("[data-test-id='action-deposit']").click();
                break;
            }
        }
    }
    
    private int extractBalance(String text) {
        int start = text.indexOf(balanceStart);
        int finish = text.indexOf(balanceFinish);
        if (start == -1 || finish == -1) {
            return 0;
        }
        String value = text.substring(start + balanceStart.length(), finish).trim();
        return Integer.parseInt(value);
    }
}
