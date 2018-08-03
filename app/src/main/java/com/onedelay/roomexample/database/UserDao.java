package com.onedelay.roomexample.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM User")
    List<User> selectAll();

    @Insert // onConflict default 3 : ABORT
    void insertAll(User... users);

    @Delete
    void deleteUser(User user);

    @Update
    void updateUser(User user);
}
