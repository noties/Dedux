package ru.noties.todo.app.components;

import ru.noties.todo.state.BaseState;

public class CountDoneState extends BaseState {

    private final int count;

    public CountDoneState(int count) {
        this.count = count;
    }

    public int count() {
        return count;
    }

    @Override
    public String toString() {
        return "CountDoneState{" +
                "count=" + count +
                '}';
    }
}
