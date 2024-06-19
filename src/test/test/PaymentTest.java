package test;


import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import data.SQLHelper;

import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static data.SQLHelper.cleanDatabase;

public class PaymentTest {
    PaymentPage paymentPage;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("Allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:8080/",PaymentPage.class);
    }

    @AfterEach
    void tearDownAllDatabase() throws SQLException {
        cleanDatabase();
    }

    @Test
    @DisplayName("Go to the form for filling in the card data by clicking the 'Buy' button")
    void shouldOpenFormByButtonPay() {
        paymentPage.openFormToPay();
    }
    @Test
    @DisplayName("Sending payment using card No. 1 with valid data.")
    void shouldOfSuccessfulPurchaseTourWithValidCard1() throws SQLException {
        paymentPage.chooseBy("Оплата по карте");
        paymentPage.enteringApprovedCard();
        paymentPage.enteringValidCardValidityPeriod();
        paymentPage.enteringValidOwner();
        paymentPage.enteringValidCVC();
        paymentPage.verifySuccessfulNotification("Операция одобрена Банком.");
        var actualStatusLastLinePaymentRequestEntity = SQLHelper.getStatusLastLinePaymentRequestEntity();
        var expectedStatus = "APPROVED";
        assertEquals(actualStatusLastLinePaymentRequestEntity, expectedStatus);
    }

    @Test
    @DisplayName("Sending payment using card No. 2 with valid data.")
    void shouldOfSuccessfulPurchaseTourWithValidCard2() throws SQLException {
        paymentPage.chooseBy("Оплата по карте");
        paymentPage.enteringDeclinedCard();
        paymentPage.enteringValidCardValidityPeriod();
        paymentPage.enteringValidOwner();
        paymentPage.enteringValidCVC();
        paymentPage.verifyErrorNotification("Ошибка! Банк отказал в проведении операции.");
        var actualStatusLastLinePaymentRequestEntity = SQLHelper.getStatusLastLinePaymentRequestEntity();
        var expectedStatus = "DECLINED";
        assertEquals(actualStatusLastLinePaymentRequestEntity, expectedStatus);
    }

    @Test
    @DisplayName("Submitting a form with an empty value.")
    void shouldReturnErrorWhenEmptyForm() {
        paymentPage.chooseBy("Оплата по карте");
        paymentPage.verifySuccessfulNotificationIsNotVisible();
        paymentPage.verifyErrorCardNumberField("Неверный формат");
        paymentPage.verifyErrorMonthField("Неверный формат");
        paymentPage.verifyErrorYearField("Неверный формат");
        paymentPage.verifyErrorOwnerField("Поле обязательно для заполнения");
        paymentPage.verifyErrorCVCField("Неверный формат");
    }

    @Test
    @DisplayName("Error when filling out a form with expired card data.")
    void shouldReturnAnErrorWithExpiredCardData() {
        paymentPage.chooseBy("Оплата по карте");
        paymentPage.enteringApprovedCard();
        paymentPage.enteringInvalidCardValidityPeriod();
        paymentPage.enteringValidOwner();
        paymentPage.enteringValidCVC();
        paymentPage.verifySuccessfulNotificationIsNotVisible();
        paymentPage.verifyPeriodErrorYearField("Истёк срок действия карты");
    }



    @Test
    @DisplayName("Submitting a form with an empty card value.")
    void
    shouldReturnAnErrorIfTheCardNumberIsEmpty() {
        paymentPage.chooseBy("Оплата по карте");
        paymentPage.enteringInvalidCard();
        paymentPage.enteringValidCardValidityPeriod();
        paymentPage.enteringValidOwner();
        paymentPage.enteringValidCVC();
        paymentPage.verifySuccessfulNotificationIsNotVisible();
        paymentPage.verifyErrorCardNumberField("Неверный формат");
    }


    @Test
    @DisplayName("Error when buying a tour with invalid cardholder data on the form.")
    void shouldReturnAnErrorWhenCardWithAnInvalidCardOwner() {
        paymentPage.chooseBy("Оплата по карте");
        paymentPage.enteringApprovedCard();
        paymentPage.enteringValidCardValidityPeriod();
        paymentPage.enteringInValidOwner();
        paymentPage.enteringValidCVC();
        paymentPage.verifySuccessfulNotificationIsNotVisible();
        paymentPage.verifyErrorOwnerField("Неверный формат");
    }

    @Test
    @DisplayName("Checking the CVC/CVV field with invalid data.")
    void shouldReturnErrorWhenCardWithInvalidCVC() {
        paymentPage.chooseBy("Оплата по карте");
        paymentPage.enteringApprovedCard();
        paymentPage.enteringValidCardValidityPeriod();
        paymentPage.enteringValidOwner();
        paymentPage.enteringInValidCVC();
        paymentPage.verifySuccessfulNotificationIsNotVisible();
        paymentPage.verifyErrorCVCField("Неверный формат");
    }


}

