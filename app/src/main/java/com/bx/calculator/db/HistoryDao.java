package com.bx.calculator.db;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface HistoryDao {

    @Insert
    void insert(Result result);

//    @Update
//    void update(Result result);

    @Delete
    void delete(Result result);

    @Query("DELETE FROM calculation_history")
    void deleteAllResults();

    @Query("SELECT * FROM calculation_history ORDER BY position DESC")
    DataSource.Factory<Integer, Result> loadHistoryNewToOld();

    @Query("SELECT * FROM calculation_history ORDER BY position DESC LIMIT 1")
    LiveData<Result> loadNewestResult();

//    @Query("SELECT * FROM calculation_history WHERE position = :position")
//    Result loadResult(int position);
}
