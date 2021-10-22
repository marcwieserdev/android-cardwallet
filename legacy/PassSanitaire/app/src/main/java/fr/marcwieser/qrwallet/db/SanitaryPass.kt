package fr.marcwieser.qrwallet.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SanitaryPass(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "firstname") val firstname: String? = null,
    @ColumnInfo(name = "lastname") val lastname: String? = null,
    @ColumnInfo(name = "dob") val dob: String? = null
) {
    fun isAdditionalInfoSet() = firstname != null && lastname != null && dob != null
}
