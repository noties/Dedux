package ru.noties.tddd.utils;

import java.util.Collection;

import javax.annotation.Nullable;

public class CollectionUtils {

    public static <C extends Collection<?>> boolean isEmpty(@Nullable C collection) {
        return collection == null || collection.size() == 0;
    }

    private CollectionUtils() {}
}
