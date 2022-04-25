package com.zlagi.network.source

import androidx.test.filters.MediumTest
import com.google.common.truth.Truth
import com.zlagi.common.exception.NetworkException
import com.zlagi.common.mapper.ExceptionMapper
import com.zlagi.common.utils.Constants
import com.zlagi.network.utils.Extensions
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.data.source.network.account.AccountNetworkDataSource
import com.zlagi.network.apiservice.AccountApiService
import com.zlagi.network.enqueueResponse
import com.zlagi.network.fakes.FakeConnectivityCheckReturnError
import com.zlagi.network.fakes.FakeConnectivityCheckReturnSuccess
import com.zlagi.network.fakes.FakeDataGenerator
import com.zlagi.network.fakes.FakeDataGenerator.newPassword
import com.zlagi.network.fakes.FakeDataGenerator.password
import com.zlagi.network.mapper.AccountNetworkDataMapper
import com.zlagi.network.model.EXPIRED_TOKEN_RESPONSE_JSON
import com.zlagi.network.model.GET_ACCOUNT_RESPONSE_JSON
import com.zlagi.network.model.UPDATE_PASSWORD_FAIL_RESPONSE_JSON
import com.zlagi.network.model.UPDATE_PASSWORD_SUCCESS_RESPONSE_JSON
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@MediumTest
class DefaultAccountNetworkDataSourceTest {

    // systemUnderTest
    private lateinit var sut: AccountNetworkDataSource

    private val mockWebServer = MockWebServer()

    private lateinit var accountApiService: AccountApiService

    private lateinit var accountNetworkDataMapper: AccountNetworkDataMapper

    private lateinit var exceptionMapper: ExceptionMapper

    @Before
    fun setup() {
        accountApiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create(Extensions.moshi))
            .build()
            .create(AccountApiService::class.java)

        accountNetworkDataMapper = AccountNetworkDataMapper()

        exceptionMapper = ExceptionMapper()

        sut = DefaultAccountNetworkDataSource(
            accountApiService = accountApiService,
            accountNetworkDataMapper = accountNetworkDataMapper,
            connectivityChecker = FakeConnectivityCheckReturnSuccess(),
            exceptionMapper = exceptionMapper
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getAccount() returns account when status is 200`() = runBlocking {
        // Given
        mockWebServer.enqueueResponse(GET_ACCOUNT_RESPONSE_JSON, 200)
        val expected = FakeDataGenerator.account

        // When
        val response = sut.getAccount()

        // Then
        val actual = (response as DataResult.Success).data

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `getAccount() throws NetworkUnavailable exception when network is offline`() = runBlocking {
        // Given
        sut = DefaultAccountNetworkDataSource(
            accountApiService = accountApiService,
            accountNetworkDataMapper = accountNetworkDataMapper,
            connectivityChecker = FakeConnectivityCheckReturnError(),
            exceptionMapper = exceptionMapper
        )
        mockWebServer.enqueueResponse(GET_ACCOUNT_RESPONSE_JSON, 200)
        val expected = NetworkException.NetworkUnavailable

        // When
        val response = sut.getAccount()

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `getAccount() throws Network exception when server is unreachable`() = runBlocking {
        // Given
        val expected = NetworkException.Network

        // When
        val response = sut.getAccount()

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `getAccount() throws NotAuthorized exception when status is 401`() = runBlocking {
        // Given
        mockWebServer.enqueueResponse(EXPIRED_TOKEN_RESPONSE_JSON, 401)
        val expected = NetworkException.NotAuthorized

        // When
        val response = sut.getAccount()

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `updatePassword() returns success message when status is 200`() = runBlocking {
        // Given
        mockWebServer.enqueueResponse(UPDATE_PASSWORD_SUCCESS_RESPONSE_JSON, 200)
        val expected = Constants.PASSWORD_UPDATED

        // When
        val response = sut.updatePassword(
            currentPassword = password,
            newPassword = newPassword,
            confirmNewPassword = newPassword
        )

        // Then
        val actual = (response as DataResult.Success).data

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `updatePassword throws NetworkUnavailable when network is offline`() = runBlocking {
        // Given
        sut = DefaultAccountNetworkDataSource(
            accountApiService = accountApiService,
            accountNetworkDataMapper = accountNetworkDataMapper,
            connectivityChecker = FakeConnectivityCheckReturnError(),
            exceptionMapper = exceptionMapper
        )
        mockWebServer.enqueueResponse(UPDATE_PASSWORD_SUCCESS_RESPONSE_JSON, 200)
        val expected = NetworkException.NetworkUnavailable

        // When
        val response = sut.updatePassword(
            currentPassword = password,
            newPassword = newPassword,
            confirmNewPassword = newPassword
        )

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `updatePassword() throws Network exception when server is unreachable`() = runBlocking {
        // Given
        val expected = NetworkException.Network

        // When
        val response = sut.updatePassword(
            currentPassword = password,
            newPassword = newPassword,
            confirmNewPassword = newPassword
        )

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `updatePassword() throws NotAuthorized exception when status is 401`() = runBlocking {
        // Given
        mockWebServer.enqueueResponse(EXPIRED_TOKEN_RESPONSE_JSON, 401)
        val expected = NetworkException.NotAuthorized

        // When
        val response = sut.updatePassword(
            currentPassword = password,
            newPassword = newPassword,
            confirmNewPassword = newPassword
        )

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `updatePassword() throws BadRequest exception when status is 400`() = runBlocking {
        // Given
        mockWebServer.enqueueResponse(UPDATE_PASSWORD_FAIL_RESPONSE_JSON, 400)
        val expected = NetworkException.BadRequest

        // When
        val response = sut.updatePassword(
            currentPassword = password,
            newPassword = newPassword,
            confirmNewPassword = newPassword
        )

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }
}
