package ru.noties.todo.app.todo.list;

import dedux.Action;

public class ScrollAction implements Action {

    private int position;
    private int offset;

    public ScrollAction(int position, int offset) {
        this.position = position;
        this.offset = offset;
    }

    public int position() {
        return position;
    }

    public int offset() {
        return offset;
    }

    @Override
    public String toString() {
        return "ScrollAction{" +
                "position=" + position +
                ", offset=" + offset +
                '}';
    }
}
