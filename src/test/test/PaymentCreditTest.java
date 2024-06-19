package test;


import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import data.SQLHelper;
import test.PaymentPage;

import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static data.SQLHelper.cleanDatabase;

public class PaymentCreditTest {
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
        open("http://localhost:8080/", PaymentPage.class);
    }

    @AfterEach
    void tearDownAllDatabase() throws SQLException {
        cleanDatabase();
    }

    @Test
    @DisplayName("Go to the form for filling in the card data by clicking the 'Buy on credit' button")
    void shouldOpenFormByButtonPayCredit() {
        paymentPage.openFormToPayCredit();
    }

    @Test
    @DisplayName("Sending payment using card No. 1 with valid data.")
    void shouldOfSuccessfulPurchaseTourWithValidCreditCard1() throws SQLException {
        paymentPage.chooseByInCredit("Кредит по данным карты");
        paymentPage.enteringApprovedCard();
        paymentPage.enteringValidCardValidityPeriod();
        paymentPage.enteringValidOwner();
        paymentPage.enteringValidCVC();
        paymentPage.verifySuccessfulNotification("Операция одобрена Банком.");
        var actualStatusLastLineCreditRequestEntity = SQLHelper.getStatusLastLineCreditRequestEntity();
        var expectedStatus = "APPROVED";
        assertEquals(actualStatusLastLineCreditRequestEntity, expectedStatus);
    }

    @Test
    @DisplayName("Sending payment using card No. 2 with valid data.")
    void shouldOfSuccessfulPurchaseTourWithValidCreditCard2() throws SQLException {
        paymentPage.chooseByInCredit("Кредит по данным карты");
        paymentPage.enteringDeclinedCard();
        paymentPage.enteringValidCardValidityPeriod();
        paymentPage.enteringValidOwner();
        paymentPage.enteringValidCVC();
        paymentPage.verifyErrorNotification("Ошибка! Банк отказал в проведении операции.");
        var actualStatusLastLineCreditRequestEntity = SQLHelper.getStatusLastLinePaymentRequestEntity();
        var expectedStatus = "DECLINED";
        assertEquals(actualStatusLastLineCreditRequestEntity, expectedStatus);
    }

}
