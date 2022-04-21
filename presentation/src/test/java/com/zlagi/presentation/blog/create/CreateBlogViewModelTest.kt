package com.zlagi.presentation.blog.create

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.net.toUri
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.google.firebase.storage.FirebaseStorage
import com.zlagi.common.exception.NetworkException
import com.zlagi.common.utils.BlogError
import com.zlagi.common.utils.validator.result.UpdateBlogResult
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.usecase.blog.create.CreateBlogUseCase
import com.zlagi.domain.usecase.blog.dateformat.DateFormatUseCase
import com.zlagi.presentation.R
import com.zlagi.presentation.TestCoroutineRule
import com.zlagi.presentation.fakes.FakeDataGenerator
import com.zlagi.presentation.viewmodel.blog.create.CreateBlogContract
import com.zlagi.presentation.viewmodel.blog.create.CreateBlogViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Build.VERSION_CODES.P])
@MediumTest
class CreateBlogViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    // systemUnderTest
    private lateinit var sut: CreateBlogViewModel

    @MockK
    private lateinit var createBlogUseCase: CreateBlogUseCase

    @MockK
    private lateinit var dateFormatUseCase: DateFormatUseCase

    @Before
    fun setup() {
        // Turn relaxUnitFun on for all mocks
        MockKAnnotations.init(this, relaxUnitFun = true)

        mockkStatic(FirebaseStorage::class)
        every { FirebaseStorage.getInstance() } returns mockk(relaxed = true)

        sut = CreateBlogViewModel(
            createBlogUseCase = createBlogUseCase,
            dateFormatUseCase = dateFormatUseCase,
            storageReference = FirebaseStorage.getInstance()
        )
    }

    @Test
    fun `when CancelCreateButtonClicked then verify viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val expectedViewEffect =
                CreateBlogContract.CreateBlogViewEffect.ShowDiscardChangesDialog

            // When
            sut.setEvent(CreateBlogContract.CreateBlogEvent.CancelCreateButtonClicked)

            // Then
            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }

    @Test
    fun `when ConfirmDialogButtonClicked then verify viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val expectedViewEffect = CreateBlogContract.CreateBlogViewEffect.NavigateUp

            // When
            sut.setEvent(CreateBlogContract.CreateBlogEvent.ConfirmDialogButtonClicked)

            // Then
            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }

    @Test
    fun `when CreateBlogUseCase returns NetworkUnavailable then verify viewState and viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result =
                UpdateBlogResult(result = DataResult.Error(NetworkException.NetworkUnavailable))

            coEvery { dateFormatUseCase.invoke() } returns ""
            coEvery { createBlogUseCase.invoke(any(), any(), any(), any()) } returns result

            val expectedViewState = CreateBlogContract.CreateBlogViewState(
                title = FakeDataGenerator.blogTitle,
                description = FakeDataGenerator.blogDescription,
                originalUri = FakeDataGenerator.blogImage.toUri()
            )

            val expectedViewEffect =
                CreateBlogContract.CreateBlogViewEffect.ShowSnackBarError(R.string.network_unavailable_message)

            // When
            sut.setEvent(CreateBlogContract.CreateBlogEvent.TitleChanged(title = FakeDataGenerator.blogTitle))
            sut.setEvent(CreateBlogContract.CreateBlogEvent.DescriptionChanged(description = FakeDataGenerator.blogDescription))
            sut.setEvent(
                CreateBlogContract.CreateBlogEvent.OriginalUriChanged(
                    uri = FakeDataGenerator.blogImage.toUri(),
                )
            )
            sut.setEvent(
                CreateBlogContract.CreateBlogEvent.ConfirmCreateButtonClicked(
                    imageUri = FakeDataGenerator.blogImage.toUri()
                )
            )

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }

            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }

    @Test
    fun `when CreateBlogUseCase returns titleError then verify viewState and viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result = UpdateBlogResult(titleError = BlogError.InputTooShort)

            coEvery { dateFormatUseCase.invoke() } returns ""
            coEvery { createBlogUseCase.invoke(any(), any(), any(), any()) } returns result

            val expectedViewState = CreateBlogContract.CreateBlogViewState(
                loading = false,
                title = "",
                description = FakeDataGenerator.blogDescription,
                originalUri = FakeDataGenerator.blogImage.toUri()
            )
            val expectedViewEffect = CreateBlogContract.CreateBlogViewEffect.ShowSnackBarError(
                R.string.title_error_message
            )

            // When
            sut.setEvent(CreateBlogContract.CreateBlogEvent.TitleChanged(title = ""))
            sut.setEvent(CreateBlogContract.CreateBlogEvent.DescriptionChanged(description = FakeDataGenerator.blogDescription))
            sut.setEvent(
                CreateBlogContract.CreateBlogEvent.OriginalUriChanged(
                    uri = FakeDataGenerator.blogImage.toUri(),
                )
            )
            sut.setEvent(
                CreateBlogContract.CreateBlogEvent.ConfirmCreateButtonClicked(
                    imageUri = FakeDataGenerator.blogImage.toUri()
                )
            )

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }

            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }

    @Test
    fun `when CreateBlogUseCase returns descriptionError then verify viewState and viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result = UpdateBlogResult(descriptionError = BlogError.InputTooShort)

            coEvery { dateFormatUseCase.invoke() } returns ""
            coEvery { createBlogUseCase.invoke(any(), any(), any(), any()) } returns result

            val expectedViewState = CreateBlogContract.CreateBlogViewState(
                title = FakeDataGenerator.blogTitle,
                description = "",
                originalUri = FakeDataGenerator.blogImage.toUri()
            )

            val expectedViewEffect = CreateBlogContract.CreateBlogViewEffect.ShowSnackBarError(
                R.string.description_error_message
            )

            // When
            sut.setEvent(CreateBlogContract.CreateBlogEvent.TitleChanged(title = FakeDataGenerator.blogTitle))
            sut.setEvent(CreateBlogContract.CreateBlogEvent.DescriptionChanged(description = ""))
            sut.setEvent(
                CreateBlogContract.CreateBlogEvent.OriginalUriChanged(
                    uri = FakeDataGenerator.blogImage.toUri(),
                )
            )
            sut.setEvent(
                CreateBlogContract.CreateBlogEvent.ConfirmCreateButtonClicked(
                    imageUri = FakeDataGenerator.blogImage.toUri()
                )
            )

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }

            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }

    @Test
    fun `when CreateBlogUseCase returns uriError then verify viewState and viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result = UpdateBlogResult(uriError = BlogError.InputTooShort)

            coEvery { dateFormatUseCase.invoke() } returns ""
            coEvery { createBlogUseCase.invoke(any(), any(), any(), any()) } returns result

            val expectedViewState = CreateBlogContract.CreateBlogViewState(
                title = FakeDataGenerator.blogTitle,
                description = FakeDataGenerator.blogDescription,
                originalUri = FakeDataGenerator.blogImage.toUri()
            )

            val expectedViewEffect = CreateBlogContract.CreateBlogViewEffect.ShowSnackBarError(
                R.string.uri_error_message
            )

            // When
            sut.setEvent(CreateBlogContract.CreateBlogEvent.TitleChanged(title = FakeDataGenerator.blogTitle))
            sut.setEvent(CreateBlogContract.CreateBlogEvent.DescriptionChanged(description = FakeDataGenerator.blogDescription))
            sut.setEvent(
                CreateBlogContract.CreateBlogEvent.OriginalUriChanged(
                    uri = FakeDataGenerator.blogImage.toUri(),
                )
            )
            sut.setEvent(
                CreateBlogContract.CreateBlogEvent.ConfirmCreateButtonClicked(
                    imageUri = FakeDataGenerator.blogImage.toUri()
                )
            )

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }

            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }
}