package dedux.sample.todo.store.action;

public class EditTodoAction implements ModifyTodoAction {

    private final long id;

    public EditTodoAction(long id) {
        this.id = id;
    }

    public long id() {
        return id;
    }

    @Override
    public String toString() {
        return "EditTodoAction{" +
                "id=" + id +
                '}';
    }
}
