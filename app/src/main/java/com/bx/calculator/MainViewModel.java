package com.bx.calculator;

import android.app.Application;

import com.bx.calculator.calc.CResult;
import com.bx.calculator.calc.CUnit;
import com.bx.calculator.db.Result;
import com.bx.calculator.db.Variable;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.PagedList;
import ch.obermuhlner.math.big.BigComplex;

public class MainViewModel extends AndroidViewModel {

    /**
     * Data source.
     */
    private final Repository repository;
    private final MutableLiveData<CUnit[]> input = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isCalculating = new MutableLiveData<>();
    private final LiveData<BigComplex> answerLiveData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
        setInput(new CUnit[] {});
        setCalculating(false);
        answerLiveData = Transformations.map(repository.getNewestResult(), r -> r == null ? null : r.answer);
    }

    @NonNull
    public LiveData<CUnit[]> getInput() {
        return input;
    }

    public void setInput(@Nullable CUnit[] input) {
        if (input == null) {
            this.input.setValue(new CUnit[] {});
        } else {
            for (final CUnit u : input) {
                Objects.requireNonNull(u);
            }
            this.input.setValue(input);
        }
    }

    @NonNull
    public LiveData<Boolean> getIsCalculating() {
        return isCalculating;
    }

    public void setCalculating(boolean calculating) {
        isCalculating.setValue(calculating);
    }

    @NonNull
    public LiveData<BigComplex> getAnswer() {
        return answerLiveData;
    }

    @NonNull
    public LiveData<PagedList<Result>> getHistoryNewToOld() {
        return repository.getHistoryNewToOld();
    }

    public void addResultToHistory(@NonNull CResult result) {
        repository.insertResult(new Result(result.getInput(), result.getParams(), result.getAnswer()));
    }

    public void deleteResult(Result result) {
        repository.deleteResult(result);
    }

    @NonNull
    public LiveData<List<Variable>> getAllVariables() {
        return repository.getAllVariables();
    }

    public void updateVariable(Variable variable) {
        repository.updateVariable(variable);
    }
}
