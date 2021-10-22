package fr.marcwieser.qrwallet.db

import androidx.room.*

@Dao
interface SanitaryDao {
    @Query("SELECT * FROM sanitarypass")
    fun getAll(): List<SanitaryPass>

    @Insert
    fun insertAll(vararg sanitaryPass: SanitaryPass)

    @Delete
    fun delete(sanitaryPass: SanitaryPass)

    @Update
    fun update(sanitaryPass: SanitaryPass)
}