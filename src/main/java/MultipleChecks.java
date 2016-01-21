import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.sbt.qa.bdd.AutotestError;
import ru.sbt.qa.bdd.db.Db;
import ru.sbt.qa.bdd.pageFactory.ElementTitle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by sbt-neradovskiy-kl on 19.01.2016.
 */
public class MultipleChecks {
    private static WebDriver driver;


    @ElementTitle(value = "Название типа вклада")
    private WebElement nameDepositLink;

    @ElementTitle(value = "Сумма вклада")
    private WebElement amountDepositLink;

    @ElementTitle(value = "Дата окончания вклада")
    private WebElement dateDepositLink;

    @ElementTitle(value = "Доступно для снятия")
    private WebElement maxAmountDepositLink;

    @ElementTitle(value = "Сумма неснижаемого остатка")
    private WebElement amountLink;

    @ElementTitle(value = "Номер счета")
    private WebElement accountLink;

    @ElementTitle(value = "Кнопка 'Операции по вкладу'")
    private WebElement operationButton;

    private void checkDate(String date2check) throws Exception {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = sdf1.parse(date2check);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Assert.assertEquals(sdf.format(date), dateDepositLink.getText());
    }

    private void checkCurrency(String currency2check) throws Exception {
        String currency = amountDepositLink.getText().replaceAll("[^(руб.|\\\\$|€)]", "");
        switch (currency) {
            case "руб.":
                Assert.assertEquals("РОССИЙСКИЙ РУБЛЬ", currency2check);
                break;
            case "$":
                Assert.assertEquals("ДОЛЛАР США", currency2check);
                break;
            case "€":
                Assert.assertEquals("ЕВРО", currency2check);
                break;
            default:
                throw new AutotestError("Валюта:" + currency + " - не совпадает");
        }
    }

    private void checkMinBalanceAmount(String checkMinAmount) throws Exception {
        String minBalanceAmountStr = checkMinAmount;
        String minBalanceAmount = String.format("%.2f", Double.parseDouble(minBalanceAmountStr));
        String amountMinIF = amountLink.getText().replaceAll("(руб.|\\$|€)", "").replaceAll(" ", "");
        Assert.assertEquals(minBalanceAmount, amountMinIF);
    }

    private void checkMaxBalanceAmount(String checkMaxAmount) throws Exception {
        String maxBalanceAmountStr = checkMaxAmount;
        String maxBalanceAmount = String.format("%.2f", Double.parseDouble(maxBalanceAmountStr));
        String amountmaxIF = maxAmountDepositLink.getText().replaceAll("(руб.|\\$|€)", "").replaceAll(" ", "");
        Assert.assertEquals(maxBalanceAmount, amountmaxIF);
    }

    private void checkBalanceAmount(String checkBalanceAmount) throws Exception {
        String maxBalanceAmountStr = checkBalanceAmount;
        String maxBalanceAmount = String.format("%.2f", Double.parseDouble(maxBalanceAmountStr));
        String amountmaxIF = amountDepositLink.getText().replaceAll("(руб.|\\$|€)", "").replaceAll(" ", "");
        Assert.assertEquals(maxBalanceAmount, amountmaxIF);
    }



    public void проверяет_значение_поля(String name) throws Throwable {
        String query = "select * from deposit_and_account_data";
        List<Map<String, String>> fetchAll = Db.fetchAll(query, "main");
        switch (name) {
            case "Имя счета":
                Assert.assertEquals(fetchAll.get(0).get("ACCOUNT_NAME"), nameDepositLink.getText());
                break;
            case "Дата закрытия":
                checkDate(fetchAll.get(0).get("CLOSE_DATE"));
                break;
            case "Код валюты":
                checkCurrency(fetchAll.get(0).get("NAME"));
            case "Сумма неснижаемого остатка":
                checkMinBalanceAmount(fetchAll.get(0).get("MIN_BALANCE_AMOUNT"));
                break;
            case "Максимальная сумма для снятия":
                checkMaxBalanceAmount(fetchAll.get(0).get("MAX_SUM_AMOUNT"));
                break;
            case "Сумма вклада":
                checkBalanceAmount(fetchAll.get(0).get("BALANCE_AMOUNT"));
                break;

            default:
                throw new AutotestError("Неожиданное поле");
        }

    }
}
