package com.zlagi.network.source

import androidx.test.filters.MediumTest
import com.google.common.truth.Truth
import com.google.firebase.auth.FirebaseAuth
import com.zlagi.common.exception.NetworkException
import com.zlagi.common.mapper.ExceptionMapper
import com.zlagi.common.utils.Extensions
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.data.source.network.auth.AuthNetworkDataSource
import com.zlagi.network.apiservice.AuthApiService
import com.zlagi.network.enqueueResponse
import com.zlagi.network.fakes.FakeConnectivityCheckReturnError
import com.zlagi.network.fakes.FakeConnectivityCheckReturnSuccess
import com.zlagi.network.fakes.FakeDataGenerator
import com.zlagi.network.mapper.TokensNetworkDataMapper
import com.zlagi.network.model.SIGN_IN_FAIL_RESPONSE_JSON
import com.zlagi.network.model.SIGN_IN_SUCCESS_RESPONSE_JSON
import com.zlagi.network.model.SIGN_UP_FAIL_RESPONSE_JSON
import com.zlagi.network.model.SIGN_UP_SUCCESS_RESPONSE_JSON
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@MediumTest
class DefaultAuthNetworkDataSourceTest {

    // systemUnderTest
    private lateinit var sut: AuthNetworkDataSource

    private val mockWebServer = MockWebServer()

    private val authApiService = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .addConverterFactory(MoshiConverterFactory.create(Extensions.moshi))
        .build()
        .create(AuthApiService::class.java)

    private val tokensNetworkDataMapper = TokensNetworkDataMapper()

    private val exceptionMapper = ExceptionMapper()

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns mockk(relaxed = true)

        sut = DefaultAuthNetworkDataSource(
            authApiService = authApiService,
            firebaseAuth = FirebaseAuth.getInstance(),
            tokensNetworkDataMapper = tokensNetworkDataMapper,
            connectivityChecker = FakeConnectivityCheckReturnSuccess(),
            exceptionMapper = exceptionMapper
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `signIn() returns tokens when status is 200`() = runBlocking {
        // Given
        mockWebServer.enqueueResponse(SIGN_IN_SUCCESS_RESPONSE_JSON, 200)
        val expected = FakeDataGenerator.tokens

        // When
        val response =
            sut.signIn(email = FakeDataGenerator.email, password = FakeDataGenerator.password)

        // Then
        val actual = (response as DataResult.Success).data

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `signIn() throws NetworkUnavailable exception when network is offline`() = runBlocking {
        // Given
        sut = DefaultAuthNetworkDataSource(
            authApiService = authApiService,
            firebaseAuth = FirebaseAuth.getInstance(),
            tokensNetworkDataMapper = tokensNetworkDataMapper,
            connectivityChecker = FakeConnectivityCheckReturnError(),
            exceptionMapper = exceptionMapper
        )
        val expected = NetworkException.NetworkUnavailable

        // When
        val response =
            sut.signIn(email = FakeDataGenerator.email, password = FakeDataGenerator.password)

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `signIn() returns Network exception when status is 401`() = runBlocking {
        // Given
        val expected = NetworkException.Network

        // When
        val response =
            sut.signIn(email = FakeDataGenerator.email, password = FakeDataGenerator.password)

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `signIn() returns NotAuthorized exception when status is 401`() = runBlocking {
        // Given
        mockWebServer.enqueueResponse(SIGN_IN_FAIL_RESPONSE_JSON, 401)
        val expected = NetworkException.NotAuthorized

        // When
        val response =
            sut.signIn(email = FakeDataGenerator.email, password = FakeDataGenerator.password)

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `signUp() returns tokens when status is 200`() = runBlocking {
        // Given
        mockWebServer.enqueueResponse(SIGN_UP_SUCCESS_RESPONSE_JSON, 200)
        val expected = FakeDataGenerator.tokens

        // When
        val response =
            sut.signUp(
                email = FakeDataGenerator.email,
                username = FakeDataGenerator.username,
                password = FakeDataGenerator.password,
                confirmPassword = FakeDataGenerator.password
            )

        // Then
        val actual = (response as DataResult.Success).data

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `signUp() throws NetworkUnavailable exception when network is offline`() = runBlocking {
        // Given
        sut = DefaultAuthNetworkDataSource(
            authApiService = authApiService,
            firebaseAuth = FirebaseAuth.getInstance(),
            tokensNetworkDataMapper = tokensNetworkDataMapper,
            connectivityChecker = FakeConnectivityCheckReturnError(),
            exceptionMapper = exceptionMapper
        )
        val expected = NetworkException.NetworkUnavailable

        // When
        val response =
            sut.signUp(
                email = FakeDataGenerator.email,
                username = FakeDataGenerator.username,
                password = FakeDataGenerator.password,
                confirmPassword = FakeDataGenerator.password
            )

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `signUp() throws Network exception when server is unreachable`() = runBlocking {
        // Given
        val expected = NetworkException.Network

        // When
        val response =
            sut.signUp(
                email = FakeDataGenerator.email,
                username = FakeDataGenerator.username,
                password = FakeDataGenerator.password,
                confirmPassword = FakeDataGenerator.password
            )

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `signUp() throws BadRequest exception when status is 400`() = runBlocking {
        // Given
        mockWebServer.enqueueResponse(SIGN_UP_FAIL_RESPONSE_JSON, 400)
        val expected = NetworkException.BadRequest

        // When
        val response =
            sut.signUp(
                email = FakeDataGenerator.email,
                username = FakeDataGenerator.username,
                password = FakeDataGenerator.password,
                confirmPassword = FakeDataGenerator.password
            )

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }
}