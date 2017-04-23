package ru.noties.todo.app.components.list;

abstract class Item {

    static class TodoItem extends Item {

        final long id;
        final String text;
        final boolean isDone;

        TodoItem(long id, String text, boolean isDone) {
            this.id = id;
            this.text = text;
            this.isDone = isDone;
        }
    }
}
