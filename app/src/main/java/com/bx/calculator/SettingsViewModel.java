package com.bx.calculator;

import android.app.Application;

import com.bx.calculator.calc.CUnit;
import com.bx.calculator.db.Variable;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class SettingsViewModel extends AndroidViewModel {

    /**
     * Data source.
     */
    private final Repository repository;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
    }

    public void deleteHistory() {
        repository.deleteHistory();
    }

    public void resetVariables() {
        for (final CUnit v: CUnit.variables) {
            repository.updateVariable(Variable.getDefault(v.toString()));
        }
    }
}
