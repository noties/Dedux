package ru.noties.todo.state.action;

import java.util.List;

import dedux.Action;

public class FirebaseSyncAction implements Action {

    private final List<Object> list;

    public FirebaseSyncAction(List<Object> list) {
        this.list = list;
    }

    public List<Object> list() {
        return list;
    }

    @Override
    public String toString() {
        return "FirebaseSyncAction{" +
                "list=" + list +
                '}';
    }
}
