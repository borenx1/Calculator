package com.bx.calculator.ui;

import com.bx.calculator.R;
import com.bx.calculator.calc.CFormat;
import com.bx.calculator.db.Variable;

import android.text.Html;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

public class VariableAdapter extends ListAdapter<Variable, VariableAdapter.VariableViewHolder> {

    private static final String TAG = "VariableAdapter";

    private static final DiffUtil.ItemCallback<Variable> DIFF_CALLBACK = new DiffUtil.ItemCallback<Variable>() {
        @Override
        public boolean areItemsTheSame(@NonNull Variable oldItem, @NonNull Variable newItem) {
            return oldItem.display.equals(newItem.display);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Variable oldItem, @NonNull Variable newItem) {
            return oldItem.value.equals(newItem.value);
        }
    };

    @Nullable
    private final OnVariableClickListener listener;

    public VariableAdapter(@Nullable OnVariableClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public VariableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_variable, parent, false);
        return new VariableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VariableViewHolder holder, int position) {
        final Variable variable = getItem(position);

        holder.displayTextView.setText(Html.fromHtml(variable.display, Html.FROM_HTML_MODE_LEGACY));
        holder.valueTextView.setText(CFormat.toConditionalString(variable.value, holder.itemView.getContext()));
    }

    public class VariableViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener {

        private final TextView displayTextView;
        private final TextView valueTextView;
        private final MaterialButton button;

        public VariableViewHolder(@NonNull View itemView) {
            super(itemView);
            displayTextView = itemView.findViewById(R.id.variable_item_display);
            valueTextView = itemView.findViewById(R.id.variable_item_value);
            button = itemView.findViewById(R.id.variable_item_button);
            button.setOnClickListener(v -> {
                if (listener != null) {
                    final int position = getAdapterPosition();
                    listener.onVariableClick(position, getItem(position));
                }
            });
            button.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            final Variable variable = getItem(getAdapterPosition());
            menu.setHeaderTitle(variable.display);
            menu.add(R.string.variable_set_value).setOnMenuItemClickListener(item -> {
                if (listener != null) {
                    final int position = getAdapterPosition();
                    listener.onVariableSet(position, getItem(position));
                }
                return true;
            });
            menu.add(R.string.reset).setOnMenuItemClickListener(item -> {
                if (listener != null) {
                    final int position = getAdapterPosition();
                    listener.onVariableReset(position, getItem(position));
                }
                return true;
            });
        }
    }

    public interface OnVariableClickListener {
        /**
         * Called when a variable in the {@link RecyclerView} is clicked.
         *
         * @param position The position of the item within the adapter's data set.
         * @param variable The variable that item is referring to.
         */
        void onVariableClick(int position, Variable variable);

        /**
         * Called when the set variable menu item in the context menu is clicked.
         *
         * @param position The position of the item within the adapter's data set.
         * @param variable The variable that item is referring to.
         */
        void onVariableSet(int position, Variable variable);

        /**
         * Called when the reset menu item in the context menu is clicked.
         *
         * @param position The position of the item within the adapter's data set.
         * @param variable The variable that item is referring to.
         */
        void onVariableReset(int position, Variable variable);
    }
}
