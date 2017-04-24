package ru.noties.todo.state.action;

import java.util.Map;

import dedux.Action;
import dedux.State;
import dedux.StateItem;

public class FirebaseSyncAction implements Action {

    private final Map<Class<? extends StateItem>, StateItem> map;

    public FirebaseSyncAction(Map<Class<? extends StateItem>, StateItem> map) {
        this.map = map;
    }

    public Map<Class<? extends StateItem>, StateItem> map() {
        return map;
    }

    @Override
    public String toString() {
        return "FirebaseSyncAction{" +
                "map=" + map +
                '}';
    }
}
