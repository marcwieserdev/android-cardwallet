package fr.marcwieser.qrwallet.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SanitaryPass::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sanitaryDao(): SanitaryDao
}