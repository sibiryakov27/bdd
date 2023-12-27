package ru.netology.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.TransferPage;
import ru.netology.page.VerificationPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyTranferForTest {

    private DashboardPage dashboardPage;
    private DataHelper.CardInfo firstCardInfo;
    private DataHelper.CardInfo secondCardInfo;
    private int firstCardBalance;
    private int secondCardBalance;

    @BeforeEach
    void setUp() {
        LoginPage loginPage = open("http://localhost:9999/", LoginPage.class);
        DataHelper.AuthInfo authInfo = DataHelper.getAuthInfo();
        VerificationPage verificationPage = loginPage.validLogin(authInfo);
        DataHelper.VerificationCode verificationCode = DataHelper.getVerificationCode(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
        firstCardInfo = DataHelper.getFirstCardInfo();
        secondCardInfo = DataHelper.getSecondCardInfo();
        firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
    }

    @Test
    void shouldTransferFromFirstToSecond() {
        int amount = DataHelper.generateValidAmount(firstCardBalance);
        int expectedBalanceFirstCard = firstCardBalance - amount;
        int expectedBalanceSecondCard = secondCardBalance + amount;
        TransferPage transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
        int actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
        int actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);
        assertEquals(expectedBalanceFirstCard, actualBalanceFirstCard);
        assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard);
    }

    @Test
    void shouldGetErrorMessageIfAmountMoreThanBalance() {
        int amount = DataHelper.generateInvalidAmount(firstCardBalance);
        TransferPage transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        transferPage.makeTransfer(String.valueOf(amount), secondCardInfo);
        transferPage.findErrorMessage("Выполнена попытка перевода суммы, превышающей остаток на карте списания");
        int actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
        int actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);
        assertEquals(firstCardBalance, actualBalanceFirstCard);
        assertEquals(secondCardBalance, actualBalanceSecondCard);
    }

}
