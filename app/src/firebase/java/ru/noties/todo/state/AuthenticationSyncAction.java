package ru.noties.todo.state;

import java.util.Map;

import dedux.Action;
import dedux.StateItem;

public class AuthenticationSyncAction implements Action {

    private final Map<Class<? extends StateItem>, StateItem> map;

    public AuthenticationSyncAction(Map<Class<? extends StateItem>, StateItem> map) {
        this.map = map;
    }

    public Map<Class<? extends StateItem>, StateItem> map() {
        return map;
    }

    @Override
    public String toString() {
        return "AuthenticationSyncAction{" +
                "map=" + map +
                '}';
    }
}
