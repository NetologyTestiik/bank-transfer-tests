package ru.netology;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.Test;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Condition.text; // ← ДОБАВЬ ЭТОТ ИМПОРТ
import static com.codeborne.selenide.Selenide.*;

public class FinalWorkingTest { // ← Имя класса должно совпадать с именем файла!
    @Test
    void depositButtonShouldExistAndBeClickable() {
        Configuration.headless = true;

        // 1. Авторизация
        open("http://localhost:9999");
        $("[data-test-id=login] input").setValue("vasya");
        $("[data-test-id=password] input").setValue("qwerty123");
        $("[data-test-id=action-login]").click();
        $("[data-test-id=code] input").setValue("12345");
        $("[data-test-id=action-verify]").click();

        // 2. Проверка, что мы в личном кабинете (должны быть карты)
        $("body").shouldHave(text("**** 0001"));
        $("body").shouldHave(text("**** 0002"));

        // 3. ГЛАВНАЯ ПРОВЕРКА: кнопка "Пополнить" существует и кликается
        $("[data-test-id=action-deposit]")
                .shouldBe(visible)        // Кнопка видима
                .shouldHave(text("Пополнить")) // Имеет правильный текст
                .click();                 // Можно нажать

        // 4. Дополнительно: проверяем, что после клика что-то изменилось
        // (например, появилась форма перевода)
        $("[data-test-id=amount]").shouldBe(visible);
    }
}