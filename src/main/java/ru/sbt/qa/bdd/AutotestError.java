package ru.sbt.qa.bdd;

/**
 * Created by sbt-neradovskiy-kl on 25.01.2016.
 */
public class AutotestError extends RuntimeException {
    public AutotestError(String err) {}
    public AutotestError(String err,Throwable e) {}
}
