package com.bx.calculator.ui;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bx.calculator.R;
import com.bx.calculator.calc.CFormat;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class ConstantAdapter extends ListAdapter<Constant, ConstantAdapter.ConstantViewHolder> {

    private static final String TAG = "ConstantAdapter";

    private static final DiffUtil.ItemCallback<Constant> DIFF_CALLBACK = new DiffUtil.ItemCallback<Constant>() {
        @Override
        public boolean areItemsTheSame(@NonNull Constant oldItem, @NonNull Constant newItem) {
            return oldItem.getSymbol().equals(newItem.getSymbol());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Constant oldItem, @NonNull Constant newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getValue().equals(newItem.getValue()) &&
                    oldItem.getUnit().equals(newItem.getUnit());
        }
    };

    @Nullable
    private final OnConstantClickListener listener;

    public ConstantAdapter(@Nullable OnConstantClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConstantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_constant, parent, false);
        return new ConstantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConstantViewHolder holder, int position) {
        final Constant constant = getItem(position);

        holder.symbolTextView.setText(Html.fromHtml(constant.getSymbol(), Html.FROM_HTML_MODE_LEGACY));
        holder.nameTextView.setText(constant.getName());
        holder.valueTextView.setText(CFormat.toConditionalString(constant.getValue(), holder.itemView.getContext()));
        holder.unitTextView.setText(Html.fromHtml(constant.getUnit(), Html.FROM_HTML_MODE_LEGACY));
    }

    public class ConstantViewHolder extends RecyclerView.ViewHolder {

        private final TextView symbolTextView;
        private final TextView nameTextView;
        private final TextView valueTextView;
        private final TextView unitTextView;
        private final MaterialButton button;

        public ConstantViewHolder(@NonNull View itemView) {
            super(itemView);
            symbolTextView = itemView.findViewById(R.id.constant_item_symbol);
            nameTextView = itemView.findViewById(R.id.constant_item_name);
            valueTextView = itemView.findViewById(R.id.constant_item_value);
            unitTextView = itemView.findViewById(R.id.constant_item_unit);
            button = itemView.findViewById(R.id.constant_item_button);
            button.setOnClickListener(v -> {
                if (listener != null) {
                    final int position = getAdapterPosition();
                    listener.onConstantClick(position, getItem(position));
                }
            });
        }
    }

    public interface OnConstantClickListener {
        /**
         * Called when a constant in the {@link RecyclerView} is clicked.
         *
         * @param position The position of the item within the adapter's data set.
         * @param constant The constant that item is referring to.
         */
        void onConstantClick(int position, Constant constant);
    }
}
