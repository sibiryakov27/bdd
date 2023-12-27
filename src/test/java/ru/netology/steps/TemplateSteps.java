package ru.netology.steps;

import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Пусть;
import io.cucumber.java.ru.Тогда;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.TransferPage;
import ru.netology.page.VerificationPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.Assert.assertEquals;

public class TemplateSteps {

    private static LoginPage loginPage;
    private static DashboardPage dashboardPage;
    private static VerificationPage verificationPage;
    private static TransferPage transferPage;

    @Пусть("пользователь залогинен с именем {string} и паролем {string}")
    public void loginWithNameAndPassword(String login, String password) {
        loginPage = open("http://localhost:9999/", LoginPage.class);
        DataHelper.AuthInfo authInfo = new DataHelper.AuthInfo(login, password);
        verificationPage = loginPage.validLogin(authInfo);
        dashboardPage = verificationPage.validVerify(DataHelper.getVerificationCode(authInfo));
    }

    @Когда("пользователь переводит {int} рублей с карты с номером 5559 0000 0000 0002 на свою 1 карту с главной страницы")
    public void transferFromSecondCardToFirstCard(Integer amount) {
        transferPage = dashboardPage.selectCardToTransfer(DataHelper.getFirstCardInfo());
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), DataHelper.getSecondCardInfo());
    }

    @Тогда("баланс его 1 карты из списка на главной странице должен стать 15 000 рублей")
    public void verifyCardBalance() {
        int balance = dashboardPage.getCardBalance(DataHelper.getFirstCardInfo());
        assertEquals(balance, 15000);
    }

}
