package com.zlagi.cache.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.zlagi.cache.database.account.AccountDao
import com.zlagi.cache.database.BlogfyDatabase
import com.zlagi.cache.fakes.FakeDataGenerator
import com.zlagi.cache.mapper.AccountCacheDataMapper
import com.zlagi.cache.source.account.DefaultAccountCacheDataSource
import com.zlagi.data.source.cache.account.AccountCacheDataSource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@HiltAndroidTest
@MediumTest
class DefaultAccountCacheDataSourceTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var accountCacheDataSource: AccountCacheDataSource

    @Inject
    @Named("test_db")
    lateinit var database: BlogfyDatabase

    private lateinit var accountDao: AccountDao

    private lateinit var accountCacheDataMapper: AccountCacheDataMapper

    @Before
    fun setup() {
        hiltRule.inject()
        accountDao = database.accountDao()
        accountCacheDataMapper = AccountCacheDataMapper()
        accountCacheDataSource = DefaultAccountCacheDataSource(accountDao, accountCacheDataMapper)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun storeAccount_success() = runBlockingTest {
        // Given
        val expected = FakeDataGenerator.account

        // When
        accountCacheDataSource.storeAccount(FakeDataGenerator.account)

        // Then
        accountCacheDataSource.fetchAccount().test {
            val actual = awaitItem()

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
