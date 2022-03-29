package com.zlagi.cache.database.account

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zlagi.cache.model.AccountCacheModel
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query(
        """
        SELECT * FROM account 
        """
    )
    fun fetchAccount(): Flow<AccountCacheModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun storeAccount(accountCacheModel: AccountCacheModel)

    @Query(
        """
        DELETE FROM account
    """
    )
    suspend fun deleteAccount()
}
