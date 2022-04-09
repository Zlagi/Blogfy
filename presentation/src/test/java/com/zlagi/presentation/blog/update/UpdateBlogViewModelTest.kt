package com.zlagi.presentation.blog.update

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.google.firebase.storage.FirebaseStorage
import com.zlagi.common.exception.NetworkException
import com.zlagi.common.utils.BlogError
import com.zlagi.common.utils.validator.result.UpdateBlogResult
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.usecase.blog.dateformat.DateFormatUseCase
import com.zlagi.domain.usecase.blog.detail.GetBlogUseCase
import com.zlagi.domain.usecase.blog.update.UpdateBlogUseCase
import com.zlagi.presentation.R
import com.zlagi.presentation.TestCoroutineRule
import com.zlagi.presentation.fakes.FakeDataGenerator
import com.zlagi.presentation.mapper.BlogDomainPresentationMapper
import com.zlagi.presentation.viewmodel.blog.update.UpdateBlogContract
import com.zlagi.presentation.viewmodel.blog.update.UpdateBlogViewModel
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
class UpdateBlogViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    // systemUnderTest
    private lateinit var sut: UpdateBlogViewModel

    @MockK
    private lateinit var getBlogUseCase: GetBlogUseCase

    @MockK
    private lateinit var updateBlogUseCase: UpdateBlogUseCase

    @MockK
    private lateinit var dateFormatUseCase: DateFormatUseCase

    private lateinit var blogDomainPresentationMapper: BlogDomainPresentationMapper

    @Before
    fun setup() {
        // Turn relaxUnitFun on for all mocks
        MockKAnnotations.init(this, relaxUnitFun = true)

        mockkStatic(FirebaseStorage::class)
        every { FirebaseStorage.getInstance() } returns mockk(relaxed = true)

        blogDomainPresentationMapper = BlogDomainPresentationMapper()

        sut = UpdateBlogViewModel(
            getBlogUseCase = getBlogUseCase,
            updateBlogUseCase = updateBlogUseCase,
            dateFormatUseCase = dateFormatUseCase,
            blogDomainPresentationMapper = blogDomainPresentationMapper,
            storageReference = FirebaseStorage.getInstance(),
            savedStateHandle = SavedStateHandle()
        )
    }

    @Test
    fun `when GetBlogUseCase returns blog then verify viewState`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result = FakeDataGenerator.oldBlog

            coEvery { getBlogUseCase.invoke(any()) } returns result

            sut.blogPk = FakeDataGenerator.oldBlog.pk

            val expectedViewState = UpdateBlogContract.UpdateBlogViewState(
                blog = blogDomainPresentationMapper.from(FakeDataGenerator.oldBlog)
            )

            // When
            sut.setEvent(UpdateBlogContract.UpdateBlogEvent.Initialization)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }
        }

    @Test
    fun `when CancelUpdateButtonClicked then verify viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val expectedViewEffect =
                UpdateBlogContract.UpdateBlogViewEffect.ShowDiscardChangesDialog

            // When
            sut.setEvent(UpdateBlogContract.UpdateBlogEvent.CancelUpdateButtonClicked)

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
            val expectedViewEffect = UpdateBlogContract.UpdateBlogViewEffect.NavigateUp

            // When
            sut.setEvent(UpdateBlogContract.UpdateBlogEvent.ConfirmDialogButtonClicked)

            // Then
            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }

    @Test
    fun `when UpdateBlogUseCase returns NetworkUnavailable then verify viewState and viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val getBlogResult = FakeDataGenerator.oldBlog
            val updateBlogResult =
                UpdateBlogResult(result = DataResult.Error(NetworkException.NetworkUnavailable))

            coEvery { getBlogUseCase.invoke(any()) } returns getBlogResult
            coEvery {
                updateBlogUseCase.invoke(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns updateBlogResult

            sut.blogPk = FakeDataGenerator.oldBlog.pk

            val blog = blogDomainPresentationMapper.from(FakeDataGenerator.updatedBlog)

            val expectedViewState = UpdateBlogContract.UpdateBlogViewState(
                blog = blog,
                originalUri = FakeDataGenerator.blogImage.toUri()
            )

            val expectedViewEffect =
                UpdateBlogContract.UpdateBlogViewEffect.ShowSnackBarError(R.string.network_unavailable_message)

            // When
            sut.setEvent(UpdateBlogContract.UpdateBlogEvent.Initialization)
            sut.setEvent(UpdateBlogContract.UpdateBlogEvent.TitleChanged(FakeDataGenerator.updatedBlog.title))
            sut.setEvent(UpdateBlogContract.UpdateBlogEvent.DescriptionChanged(FakeDataGenerator.updatedBlog.description))
            sut.setEvent(UpdateBlogContract.UpdateBlogEvent.OriginalUriChanged(uri = FakeDataGenerator.blogImage.toUri()))
            sut.setEvent(
                UpdateBlogContract.UpdateBlogEvent.ConfirmUpdateButtonClicked(
                    FakeDataGenerator.blogImage.toUri()
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
    fun `when UpdateBlogUseCase returns titleError then verify viewState and viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val getBlogResult = FakeDataGenerator.oldBlog
            val updateBlogResult = UpdateBlogResult(titleError = BlogError.InputTooShort)

            coEvery { getBlogUseCase.invoke(any()) } returns getBlogResult
            coEvery {
                updateBlogUseCase.invoke(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns updateBlogResult

            sut.blogPk = FakeDataGenerator.oldBlog.pk

            val blog = blogDomainPresentationMapper.from(FakeDataGenerator.blogMissingTitle)

            val expectedViewState = UpdateBlogContract.UpdateBlogViewState(
                blog = blog
            )

            val expectedViewEffect = UpdateBlogContract.UpdateBlogViewEffect.ShowSnackBarError(
                R.string.title_error_message
            )

            // When
            sut.setEvent(UpdateBlogContract.UpdateBlogEvent.Initialization)
            sut.setEvent(UpdateBlogContract.UpdateBlogEvent.TitleChanged(title = ""))
            sut.setEvent(
                UpdateBlogContract.UpdateBlogEvent.ConfirmUpdateButtonClicked(
                    FakeDataGenerator.blogImage.toUri()
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
    fun `when UpdateBlogUseCase returns descriptionError then verify viewState and viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val getBlogResult = FakeDataGenerator.oldBlog
            val updateBlogResult = UpdateBlogResult(descriptionError = BlogError.InputTooShort)

            coEvery { getBlogUseCase.invoke(any()) } returns getBlogResult
            coEvery {
                updateBlogUseCase.invoke(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns updateBlogResult

            sut.blogPk = FakeDataGenerator.oldBlog.pk

            val blog = blogDomainPresentationMapper.from(FakeDataGenerator.blogMissingDescription)

            val expectedViewState = UpdateBlogContract.UpdateBlogViewState(
                blog = blog
            )

            val expectedViewEffect = UpdateBlogContract.UpdateBlogViewEffect.ShowSnackBarError(
                R.string.description_error_message
            )

            // When
            sut.setEvent(UpdateBlogContract.UpdateBlogEvent.Initialization)
            sut.setEvent(UpdateBlogContract.UpdateBlogEvent.DescriptionChanged(description = ""))
            sut.setEvent(
                UpdateBlogContract.UpdateBlogEvent.ConfirmUpdateButtonClicked(
                    FakeDataGenerator.blogImage.toUri()
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