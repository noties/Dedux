package ru.noties.todo.utils;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nullable;

public class CollectionUtils {

    public static <C extends Collection<?>> boolean isEmpty(@Nullable C collection) {
        return collection == null || collection.size() == 0;
    }

    public static <M extends Map<?, ?>> boolean isEmpty(M map) {
        return map == null || map.size() == 0;
    }

    public static <C extends Collection<?>> int length(@Nullable C collection) {
        return collection != null ? collection.size() : 0;
    }

    private CollectionUtils() {}
}
