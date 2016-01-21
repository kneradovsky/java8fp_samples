import javaslang.control.Try;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.sbt.qa.bdd.AutotestError;
import ru.sbt.qa.bdd.db.Db;
import ru.sbt.qa.bdd.pageFactory.ElementTitle;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Created by sbt-neradovskiy-kl on 19.01.2016.
 */
public class MultipleChecksFP {
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

    Map<String,Predicate<Map<String,String>>> checkMap = new HashMap<>();

    Map<String,String> currencies = new HashMap<>();
    {
        currencies.put("руб.","РОССИЙСКИЙ РУБЛЬ");
        currencies.put("$","ДОЛЛАР США");
        currencies.put("€","ЕВРО");
    }

    {
        checkMap.put("Код валюты",exp -> {
            String currency = amountDepositLink.getText().replaceAll("[^(руб.|\\\\$|€)]", "");
            //change to optional
            return Optional.ofNullable(currencies.get(currency))
                    .orElseThrow(()->new AutotestError("Валюта:" + currency + " - не совпадает"))
                    .equals(exp.get("NAME"));
        });
        checkMap.put("Имя счета",exp -> exp.get("ACCOUNT_NAME").equals(nameDepositLink.getText()));
        checkMap.put("Дата закрытия",exp -> {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = Try.of(()->sdf1.parse(exp.get("CLOSE_DATE")))
                    .onFailure(ex->{throw new AutotestError("parse date error",ex);}).get();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            return sdf.format(date).equals(dateDepositLink.getText());
        });

        checkMap.put("Сумма неснижаемого остатка",exp -> {
            String minBalanceAmountStr = exp.get("MIN_BALANCE_AMOUNT");
            String minBalanceAmount = String.format("%.2f", Double.parseDouble(minBalanceAmountStr));
            String amountMinIF = amountLink.getText().replaceAll("(руб.|\\$|€)", "").replaceAll(" ", "");
            return minBalanceAmount.equals(amountMinIF);
        });
        checkMap.put("Максимальная сумма для снятия", exp -> {
            String maxBalanceAmountStr = exp.get("MAX_SUM_AMOUNT");
            String maxBalanceAmount = String.format("%.2f", Double.parseDouble(maxBalanceAmountStr));
            String amountmaxIF = maxAmountDepositLink.getText().replaceAll("(руб.|\\$|€)", "").replaceAll(" ", "");
            return maxBalanceAmount.equals(amountmaxIF);
        });
        checkMap.put("Сумма вклада",exp -> {
            String maxBalanceAmountStr = exp.get("BALANCE_AMOUNT");
            String maxBalanceAmount = String.format("%.2f", Double.parseDouble(maxBalanceAmountStr));
            String amountmaxIF = amountDepositLink.getText().replaceAll("(руб.|\\$|€)", "").replaceAll(" ", "");
            return maxBalanceAmount.equals(amountmaxIF);
        });
    }

    public void проверяет_значение_поля(String name) throws Throwable {
        String query = "select * from deposit_and_account_data";
        Map<String, String> expected = Db.fetchAll(query, "main").get(0);
        Assert.assertTrue(name,
                Optional.ofNullable(checkMap.get(name))
                .orElseThrow(()->new AutotestError("Неожиданное поле"))
                .test(expected));
    }

}
