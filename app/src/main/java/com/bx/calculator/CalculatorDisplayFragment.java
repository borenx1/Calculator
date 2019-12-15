package com.bx.calculator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ch.obermuhlner.math.big.BigComplex;

import android.text.SpannableStringBuilder;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bx.calculator.calc.CFormat;
import com.bx.calculator.calc.CUnit;
import com.bx.calculator.db.Result;
import com.bx.calculator.ui.HistoryAdapter;
import com.bx.calculator.ui.dialog.VariableMapDialogFragment;

import java.util.List;


/**
 * A {@link Fragment} subclass that displays data, such as calculation history, input etc.
 */
public class CalculatorDisplayFragment extends Fragment implements HistoryAdapter.OnItemClickListener {

    public static final String TAG = "CalculatorDisplayFragment";

    private MainViewModel model;
    private TextView infoView;
    private ProgressBar progressBar;
    private EditText inputView;
    private RecyclerView historyView;
    private HistoryAdapter historyViewAdapter;

    private final Observer<BigComplex> answerObserver = answer -> {
        final CharSequence answerString;
        if (answer == null) {
            answerString = "none";
        } else {
            answerString = CFormat.toConditionalString(answer, getContext());
        }
        infoView.setText(new SpannableStringBuilder("Ans = ").append(answerString));
    };
    private final Observer<PagedList<Result>> historyObserver = results -> historyViewAdapter.submitList(results);
    private final Observer<Boolean> calculatingObserver = isCalculating -> {
        if (isCalculating) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    };
    private final Observer<CUnit[]> inputObserver = input -> inputView.setText(CFormat.toDisplayString(input));

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup viewModel
        model = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        model.getAnswer().observe(this, answerObserver);
        model.getHistoryNewToOld().observe(this, historyObserver);
        model.getIsCalculating().observe(this, calculatingObserver);
        model.getInput().observe(this, inputObserver);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calculator_display, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        infoView = view.findViewById(R.id.info_text_view);
        progressBar = view.findViewById(R.id.calculate_progress_bar);
        inputView = view.findViewById(R.id.input_edit_text);
        historyView = view.findViewById(R.id.history_recycler_view);
        historyViewAdapter = new HistoryAdapter(this);

        inputView.setShowSoftInputOnFocus(false);
        inputView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {}
        });
        inputView.setCustomInsertionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {}
        });

        historyView.setHasFixedSize(true);
        historyView.setAdapter(historyViewAdapter);
        historyView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        historyView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        historyViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                historyView.scrollToPosition(0);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                historyView.scrollToPosition(positionStart);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                historyView.scrollToPosition(positionStart);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                historyView.scrollToPosition(toPosition);
            }
        });
    }

    @Override
    public void onItemClick(int position, Result result) {
        if (result != null) {
            model.setInput(result.input);
            inputView.setSelection(inputView.length());
        }
    }

    @Override
    public void onItemDelete(int position, Result result) {
        if (result != null) {
            model.deleteResult(result);
        }
    }

    @Override
    public void onItemMapToVariable(int position, Result result) {
        if (result != null) {
            VariableMapDialogFragment.getInstance(result.answer).show(getFragmentManager(), null);
        }
    }

    public EditText getInputView() {
        return inputView;
    }

    public int getInputSelection() {
        return inputView.getSelectionStart();
    }

    public void setInputSelection(int position) {
        inputView.setSelection(position);
    }

    public void setInputSelectionEnd() {
        inputView.setSelection(inputView.getText().length());
    }
}
