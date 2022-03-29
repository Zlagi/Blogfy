package com.zlagi.data.repository

import androidx.test.filters.MediumTest
import com.google.common.truth.Truth
import com.zlagi.common.exception.NetworkException
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.data.fakes.FakeDataGenerator
import com.zlagi.data.fakes.source.network.FakeAuthNetworkDataSource
import com.zlagi.data.fakes.source.preferences.FakePreferences
import com.zlagi.data.mapper.TokensDataDomainMapper
import com.zlagi.data.repository.auth.DefaultAuthRepository
import com.zlagi.domain.repository.auth.AuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

@MediumTest
class AuthRepositoryTest {

    private lateinit var sut: AuthRepository
    private lateinit var tokensDataDomainMapper: TokensDataDomainMapper

    @Before
    fun setup() {
        tokensDataDomainMapper = TokensDataDomainMapper()
        sut = DefaultAuthRepository(
            authNetworkDataSource = FakeAuthNetworkDataSource(
                signInResponse = DataResult.Success(FakeDataGenerator.tokens)
            ),
            tokensDataDomainMapper = tokensDataDomainMapper,
            preferencesDataSource = FakePreferences()
        )
    }

    @Test
    fun `signIn() returns tokens when NetworkDataSource returns success`() = runBlocking {
        // Given
        val expected = tokensDataDomainMapper.from(FakeDataGenerator.tokens)

        // When
        val response =
            sut.signIn(email = FakeDataGenerator.email, password = FakeDataGenerator.password)

        // Then
        val actual = (response as DataResult.Success).data

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `signIn() returns error when NetworkDataSource throws NetworkUnavailable exception`() =
        runBlocking {
            // Given
            val sut = DefaultAuthRepository(
                authNetworkDataSource = FakeAuthNetworkDataSource(
                    signInResponse = DataResult.Error(
                        NetworkException.NetworkUnavailable
                    )
                ),
                tokensDataDomainMapper = TokensDataDomainMapper(),
                preferencesDataSource = FakePreferences()
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
    fun `signIn() returns error when NetworkDataSource throws Network exception`() = runBlocking {
        // Given
        val sut = DefaultAuthRepository(
            authNetworkDataSource = FakeAuthNetworkDataSource(
                signInResponse = DataResult.Error(
                    NetworkException.Network
                )
            ),
            tokensDataDomainMapper = TokensDataDomainMapper(),
            preferencesDataSource = FakePreferences()
        )
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
    fun `signIn() returns error when NetworkDataSource throws NotAuthorized exception`() =
        runBlocking {
            // Given
            val sut = DefaultAuthRepository(
                authNetworkDataSource = FakeAuthNetworkDataSource(
                    signInResponse = DataResult.Error(
                        NetworkException.NotAuthorized
                    )
                ),
                tokensDataDomainMapper = TokensDataDomainMapper(),
                preferencesDataSource = FakePreferences()
            )
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
    fun `signUp() returns tokens when NetworkDataSource returns success`() = runBlocking {
        // Given
        val expected = tokensDataDomainMapper.from(FakeDataGenerator.tokens)

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
    fun `signUp() returns error when NetworkDataSource throws NetworkUnavailable exception`() =
        runBlocking {
            // Given
            val sut = DefaultAuthRepository(
                authNetworkDataSource = FakeAuthNetworkDataSource(
                    signInResponse = DataResult.Error(
                        NetworkException.NetworkUnavailable
                    )
                ),
                tokensDataDomainMapper = TokensDataDomainMapper(),
                preferencesDataSource = FakePreferences()
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
    fun `signUp() returns error when NetworkDataSource throws Network exception`() = runBlocking {
        // Given
        val sut = DefaultAuthRepository(
            authNetworkDataSource = FakeAuthNetworkDataSource(
                signInResponse = DataResult.Error(
                    NetworkException.Network
                )
            ),
            tokensDataDomainMapper = TokensDataDomainMapper(),
            preferencesDataSource = FakePreferences()
        )
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
    fun `signUp() returns error when NetworkDataSource throws NotAuthorized exception`() =
        runBlocking {
            // Given
            val sut = DefaultAuthRepository(
                authNetworkDataSource = FakeAuthNetworkDataSource(
                    signInResponse = DataResult.Error(
                        NetworkException.NotAuthorized
                    )
                ),
                tokensDataDomainMapper = TokensDataDomainMapper(),
                preferencesDataSource = FakePreferences()
            )
            val expected = NetworkException.NotAuthorized

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
