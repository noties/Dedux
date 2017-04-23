package ru.noties.todo.state.action;

import java.util.Map;

import dedux.Action;

public class FirebaseSyncAction implements Action {

    private final Map<String, Object> map;

    public FirebaseSyncAction(Map<String, Object> map) {
        this.map = map;
    }

    public Map<String, Object> map() {
        return map;
    }

    @Override
    public String toString() {
        return "FirebaseSyncAction{" +
                "map=" + map +
                '}';
    }
}
