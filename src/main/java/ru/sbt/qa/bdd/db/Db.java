package ru.sbt.qa.bdd.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sbt-neradovskiy-kl on 25.01.2016.
 */
public class Db {
    public static List<Map<String,String>> fetchAll(String query,String db) {
        List<Map<String,String>> ret = new ArrayList<>();
        ret.add(new HashMap<>());
        return ret;
    }
}
