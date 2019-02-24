package com.cse110.personalbest.Utilities;

public interface StorageSolution {

    void put(String key, String value);
    String get(String key, String defaultVal);

    void put(String key, Long value);
    Long get(String key, Long defaultVal);

    void put(String key, Integer value);
    Integer get(String key, Integer defaultVal);
}
