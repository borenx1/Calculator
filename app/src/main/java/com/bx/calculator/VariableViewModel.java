package com.bx.calculator;

import com.bx.calculator.db.Variable;

import java.util.List;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class VariableViewModel extends AndroidViewModel {

    private final Repository repository;

    public VariableViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
    }

    @NonNull
    public LiveData<List<Variable>> getAllVariables() {
        return repository.getAllVariables();
    }

    public void updateVariable(Variable variable) {
        repository.updateVariable(variable);
    }
}
