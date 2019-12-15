package com.bx.calculator.ui;

import com.bx.calculator.R;
import com.bx.calculator.calc.CFormat;
import com.bx.calculator.db.Result;
import com.google.android.material.button.MaterialButton;

import java.util.Arrays;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import ch.obermuhlner.math.big.BigComplex;

public class HistoryAdapter extends PagedListAdapter<Result, HistoryAdapter.ResultViewHolder> {

    private static final String TAG = "HistoryAdapter";
    private static final DiffUtil.ItemCallback<Result> DIFF_CALLBACK = new DiffUtil.ItemCallback<Result>() {
        @Override
        public boolean areItemsTheSame(@NonNull Result oldItem, @NonNull Result newItem) {
            return oldItem.position == newItem.position;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Result oldItem, @NonNull Result newItem) {
            return Arrays.equals(oldItem.input, newItem.input)
                    && oldItem.params.equals(newItem.params)
                    && oldItem.answer.equals(newItem.answer);
        }
    };

    @Nullable
    private final OnItemClickListener listener;

    public HistoryAdapter(@Nullable OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        final Result result = getItem(position);
        if (result == null) {
            // placeholder
            holder.sequenceTextView.setText(null);
            holder.answerTextView.setText(null);
        } else {
            holder.sequenceTextView.setText(CFormat.toDisplayString(result.input));
            final BigComplex answer = result.answer;
            holder.answerTextView.setText(CFormat.toConditionalString(answer, holder.itemView.getContext()));
        }

    }

    public class ResultViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener {

        private final TextView sequenceTextView;
        private final TextView answerTextView;
        private final MaterialButton button;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            sequenceTextView = itemView.findViewById(R.id.history_item_sequence);
            answerTextView = itemView.findViewById(R.id.history_item_answer);
            button = itemView.findViewById(R.id.history_item_button);
            button.setOnClickListener(v -> {
                if (listener != null) {
                    final int position = getAdapterPosition();
                    listener.onItemClick(position, getItem(position));
                }
            });
            button.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            final Result result = getItem(getAdapterPosition());
            menu.setHeaderTitle(CFormat.toDisplayString(result.input));
            menu.add(R.string.delete).setOnMenuItemClickListener(item -> {
                if (listener != null) {
                    final int position = getAdapterPosition();
                    listener.onItemDelete(position, getItem(position));
                }
                return true;
            });
            menu.add(R.string.variable_map).setOnMenuItemClickListener(item -> {
                if (listener != null) {
                    final int position = getAdapterPosition();
                    listener.onItemMapToVariable(position, getItem(position));
                }
                return true;
            });
        }
    }

    public interface OnItemClickListener {
        /**
         * Called when an item (viewHolder) in the {@link RecyclerView} is clicked.
         *
         * @param position The position of the item within the adapter's data set.
         * @param result The result that item is referring to.
         */
        void onItemClick(int position, Result result);

        /**
         * Called when the delete menu item in the context menu is clicked.
         *
         * @param position The position of the item within the adapter's data set.
         * @param result The result that item is referring to.
         */
        void onItemDelete(int position, Result result);

        void onItemMapToVariable(int position, Result result);
    }
}
