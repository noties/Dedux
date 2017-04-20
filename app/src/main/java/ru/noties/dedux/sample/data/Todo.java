package ru.noties.dedux.sample.data;

public class Todo {

    private final String name;
    private final boolean done;

    // comment for now
//    final long whenCreated;
//    final long whenDone;
    // final String label;


    public Todo(String name, boolean done) {
        this.name = name;
        this.done = done;
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
                "name='" + name + '\'' +
                ", done=" + done +
                '}';
    }
}
