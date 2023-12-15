package Account

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey val email: String,
    val name: String,
    val nickname: String,
    val phoneNumber: String
)