package account

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User): Long // Returns the row ID of the inserted item

    @Query("SELECT * FROM User WHERE email = :email")
    fun getUserByEmail(email: String): User? // Returns a single User or null

    @Update
    suspend fun updateUser(user: User): Int

    @Query("DELETE FROM user WHERE email = :email")
    suspend fun deleteUserByEmail(email: String): Int
}