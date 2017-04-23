package ru.noties.todo.app.todo.core;

public class Todo {

    private final long id;
    private final String name;
    private final boolean done;

    // comment for now
//    final long whenCreated;
//    final long whenDone;
    // final String label;


    public Todo(long id, String name, boolean done) {
        this.id = id;
        this.name = name;
        this.done = done;
    }

    public long id() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isDone() {
        return done;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", done=" + done +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Todo todo = (Todo) o;

        return id == todo.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
