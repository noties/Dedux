package ru.noties.todo.app.component.list;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.noties.todo.R;
import ru.noties.todo.app.view.IconView;
import ru.noties.vt.Holder;
import ru.noties.vt.ViewType;

class TodoItemViewType extends ViewType<Item.TodoItem, TodoItemViewType.TodoHolder> {

    @Override
    protected TodoHolder createView(LayoutInflater inflater, ViewGroup parent) {
        return new TodoHolder(inflater.inflate(R.layout.vt_todo_item, parent, false));
    }

    @Override
    protected void bindView(Context context, TodoHolder holder, Item.TodoItem item, List<Object> payloads) {

        final boolean isDone = item.isDone;

        final CharSequence text;
        if (isDone) {
            final Spannable spannable = new SpannableString(item.text);
            spannable.setSpan(new StrikethroughSpan(), 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            text = spannable;
        } else {
            text = item.text;
        }

        holder.name.setText(text);
        holder.name.setActivated(isDone);

        holder.checkBox.setActivated(isDone);
    }

    @Override
    public long itemId(Item.TodoItem item) {
        return item.id;
    }

    static class TodoHolder extends Holder {

        final TextView name;
        final IconView checkBox;

        TodoHolder(View itemView) {
            super(itemView);

            this.name = findView(R.id.vt_todo_name);
            this.checkBox = findView(R.id.vt_todo_check_box);
        }
    }
}
