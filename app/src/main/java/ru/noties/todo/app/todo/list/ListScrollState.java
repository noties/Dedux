package ru.noties.todo.app.components.list;

import ru.noties.todo.state.BaseState;

public class ListScrollState extends BaseState {

    private int position;
    private int offset;

    public int position() {
        return position;
    }

    public ListScrollState position(int position) {
        this.position = position;
        return this;
    }

    public int offset() {
        return offset;
    }

    public ListScrollState offset(int offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public String toString() {
        return "ListScrollState{" +
                "position=" + position +
                ", offset=" + offset +
                '}';
    }
}
