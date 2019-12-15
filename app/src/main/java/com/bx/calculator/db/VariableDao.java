package com.bx.calculator.db;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface VariableDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<Variable> variables);

    @Update
    void update(Variable variable);

    @Query("SELECT * FROM variables ORDER BY `order` ASC")
    LiveData<List<Variable>> loadAllVariables();

    @Query("SELECT * FROM variables ORDER BY `order` ASC")
    List<Variable> getAllVariables();

    @Query("SELECT * FROM variables WHERE display = :display")
    LiveData<Variable> loadVariable(String display);
}
