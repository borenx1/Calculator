package com.bx.calculator;


import android.app.Application;

import com.bx.calculator.db.AppDatabase;
import com.bx.calculator.db.Result;
import com.bx.calculator.db.HistoryDao;
import com.bx.calculator.db.Variable;
import com.bx.calculator.db.VariableDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

/**
 * Singleton to access all data.
 */
public final class Repository {

    private static Repository instance;

    /**
     * Singleton instance for repository.
     *
     * @param application Application.
     * @return Repository singleton.
     */
    @NonNull
    public static synchronized Repository getInstance(@NonNull Application application) {
        if (instance == null) {
            instance = new Repository(application);
        }
        return instance;
    }

    private final ExecutorService executor;
    private final HistoryDao historyDao;
    private final VariableDao variableDao;
    private final LiveData<PagedList<Result>> historyList;

    private Repository(@NonNull Application application) {
        executor = Executors.newCachedThreadPool();
        final AppDatabase db = AppDatabase.getInstance(application);
        historyDao = db.historyDao();
        variableDao = db.variableDao();
        final DataSource.Factory<Integer, Result> historyDataSource = historyDao.loadHistoryNewToOld();
        historyList = new LivePagedListBuilder<>(historyDataSource, 50).build();
    }

    @NonNull
    public LiveData<PagedList<Result>> getHistoryNewToOld() {
        return historyList;
    }

    @NonNull
    public LiveData<Result> getNewestResult() {
        return historyDao.loadNewestResult();
    }

    /**
     * Adds a result to the {@link Result} table in the database. Set no position to append.
     *
     * @param result result
     */
    public void insertResult(Result result) {
        executor.execute(() -> historyDao.insert(result));
    }

    public void deleteResult(Result result) {
        executor.execute(() -> historyDao.delete(result));
    }

    public void deleteHistory() {
        executor.execute(historyDao::deleteAllResults);
    }

    @NonNull
    public LiveData<List<Variable>> getAllVariables() {
        return variableDao.loadAllVariables();
    }

    @NonNull
    public LiveData<Variable> getVariable(String display) {
        return variableDao.loadVariable(display);
    }

    public void updateVariable(Variable variable) {
        if (variable != null) {
            executor.execute(() -> variableDao.update(variable));
        }
    }
}
