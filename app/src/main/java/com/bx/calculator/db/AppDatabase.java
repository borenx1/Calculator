package com.bx.calculator.db;

import android.content.Context;
import android.os.AsyncTask;

import com.bx.calculator.calc.CParams;
import com.bx.calculator.calc.CUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;
import ch.obermuhlner.math.big.BigComplex;

@Database(entities = {Result.class, Variable.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static String TAG = "AppDatabase";

    private static AppDatabase instance;
    private static RoomDatabase.Callback roomCallback = new Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "app_database")
                    .fallbackToDestructiveMigration().addCallback(roomCallback).build();
        }
        return instance;
    }

    public abstract HistoryDao historyDao();

    public abstract VariableDao variableDao();

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        final AppDatabase db;

        PopulateDbAsyncTask(@NonNull AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final List<Variable> dbVariables = db.variableDao().getAllVariables();
            final Map<String, Variable> variableMap = new HashMap<>();
            for (final Variable v: dbVariables) {
                variableMap.put(v.display, v);
            }

            final List<Variable> newVariables = new ArrayList<>();
            for (final CUnit v: CUnit.variables) {
                if (variableMap.get(v.toString()) == null) {
                    newVariables.add(Variable.getDefault(v.toString()));
                }
            }

            db.variableDao().insert(newVariables);
            return null;
        }
    }
}
