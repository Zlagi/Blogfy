package com.zlagi.data.repository

import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.zlagi.common.exception.NetworkException
import com.zlagi.common.utils.Constants
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.data.fakes.FakeDataGenerator
import com.zlagi.data.fakes.source.cache.FakeAccountCacheDataSource
import com.zlagi.data.fakes.source.network.FakeAccountNetworkDataSource
import com.zlagi.data.mapper.AccountDataDomainMapper
import com.zlagi.data.repository.account.DefaultAccountRepository
import com.zlagi.domain.repository.account.AccountRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@MediumTest
class AccountRepositoryTest {

    private lateinit var sut: AccountRepository
    private lateinit var accountDataDomainMapper: AccountDataDomainMapper

    @Before
    fun setup() {
        accountDataDomainMapper = AccountDataDomainMapper()

        // systemUnderTest
        sut = DefaultAccountRepository(
            accountNetworkDataSource = FakeAccountNetworkDataSource(
                account = DataResult.Success(FakeDataGenerator.account),
                updatedPassword = DataResult.Success(Constants.PASSWORD_UPDATED)
            ),
            accountCacheDataSource = FakeAccountCacheDataSource(),
            accountDataDomainMapper = AccountDataDomainMapper()
        )
    }

    @Test
    fun `requestAccount() returns account when NetworkDataSource returns success`() = runBlocking {
        // Given
        val expected = accountDataDomainMapper.from(FakeDataGenerator.account)

        // When
        val result = sut.requestAccount()

        // Then
        val actual = (result as DataResult.Success).data

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `requestAccount() returns error when NetworkDataSource throws NetworkUnavailable exception`() =
        runBlocking {
            // Given
            sut = DefaultAccountRepository(
                accountNetworkDataSource = FakeAccountNetworkDataSource(
                    account = DataResult.Error(NetworkException.NetworkUnavailable),
                    updatedPassword = DataResult.Success(Constants.PASSWORD_UPDATED)
                ),
                accountCacheDataSource = FakeAccountCacheDataSource(),
                accountDataDomainMapper = AccountDataDomainMapper()
            )
            val expected = NetworkException.NetworkUnavailable

            // When
            val result = sut.requestAccount()

            // Then
            val actual = (result as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `requestAccount() returns error when NetworkDataSource throws Network exception`() =
        runBlocking {
            // Given
            sut = DefaultAccountRepository(
                accountNetworkDataSource = FakeAccountNetworkDataSource(
                    account = DataResult.Error(NetworkException.Network),
                    updatedPassword = DataResult.Success(Constants.PASSWORD_UPDATED)
                ),
                accountCacheDataSource = FakeAccountCacheDataSource(),
                accountDataDomainMapper = AccountDataDomainMapper()
            )
            val expected = NetworkException.Network

            // When
            val result = sut.requestAccount()

            // Then
            val actual = (result as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `requestAccount() returns error when NetworkDataSource throws NotAuthorized exception`() =
        runBlocking {
            // Given
            sut = DefaultAccountRepository(
                accountNetworkDataSource = FakeAccountNetworkDataSource(
                    account = DataResult.Error(NetworkException.NotAuthorized),
                    updatedPassword = DataResult.Success(Constants.PASSWORD_UPDATED)
                ),
                accountCacheDataSource = FakeAccountCacheDataSource(),
                accountDataDomainMapper = AccountDataDomainMapper()
            )
            val expected = NetworkException.NotAuthorized

            // When
            val result = sut.requestAccount()

            // Then
            val actual = (result as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `updatePassword() returns account when NetworkDataSource returns success`() = runBlocking {
        // Given
        val expected = Constants.PASSWORD_UPDATED

        // When
        val result = sut.updatePassword(
            currentPassword = FakeDataGenerator.password,
            newPassword = FakeDataGenerator.newPassword,
            confirmNewPassword = FakeDataGenerator.newPassword
        )

        // Then
        val actual = (result as DataResult.Success).data

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `updatePassword() returns error when NetworkDataSource throws NetworkUnavailable exception`() =
        runBlocking {
            // Given
            sut = DefaultAccountRepository(
                accountNetworkDataSource = FakeAccountNetworkDataSource(
                    account = DataResult.Success(FakeDataGenerator.account),
                    updatedPassword = DataResult.Error(NetworkException.NetworkUnavailable)
                ),
                accountCacheDataSource = FakeAccountCacheDataSource(),
                accountDataDomainMapper = AccountDataDomainMapper()
            )
            val expected = NetworkException.NetworkUnavailable

            // When
            val result = sut.updatePassword(
                currentPassword = FakeDataGenerator.password,
                newPassword = FakeDataGenerator.newPassword,
                confirmNewPassword = FakeDataGenerator.newPassword
            )

            // Then
            val actual = (result as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `updatePassword() returns error when NetworkDataSource throws Network exception`() =
        runBlocking {
            // Given
            sut = DefaultAccountRepository(
                accountNetworkDataSource = FakeAccountNetworkDataSource(
                    account = DataResult.Success(FakeDataGenerator.account),
                    updatedPassword = DataResult.Error(NetworkException.Network)
                ),
                accountCacheDataSource = FakeAccountCacheDataSource(),
                accountDataDomainMapper = AccountDataDomainMapper()
            )
            val expected = NetworkException.Network

            // When
            val result = sut.updatePassword(
                currentPassword = FakeDataGenerator.password,
                newPassword = FakeDataGenerator.newPassword,
                confirmNewPassword = FakeDataGenerator.newPassword
            )

            // Then
            val actual = (result as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `updatePassword() returns error when NetworkDataSource throws BadRequest exception`() =
        runBlocking {
            // Given
            sut = DefaultAccountRepository(
                accountNetworkDataSource = FakeAccountNetworkDataSource(
                    account = DataResult.Success(FakeDataGenerator.account),
                    updatedPassword = DataResult.Error(NetworkException.BadRequest)
                ),
                accountCacheDataSource = FakeAccountCacheDataSource(),
                accountDataDomainMapper = AccountDataDomainMapper()
            )
            val expected = NetworkException.BadRequest

            // When
            val result = sut.updatePassword(
                currentPassword = FakeDataGenerator.password,
                newPassword = FakeDataGenerator.newPassword,
                confirmNewPassword = FakeDataGenerator.newPassword
            )

            // Then
            val actual = (result as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `getAccount() returns account from CacheDataSource`() = runBlockingTest {
        // Given
        val expected = accountDataDomainMapper.from(FakeDataGenerator.account)

        // When
        sut.storeAccount(expected)
        val result = sut.getAccount()


        // Then
        result.test {
            val actual = awaitItem()

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
