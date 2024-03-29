package app.newproj.lbrytv.data.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.ClaimCard
import app.newproj.lbrytv.data.dto.PagingDataList
import app.newproj.lbrytv.data.dto.RowPresentable
import app.newproj.lbrytv.data.dto.SettingCard
import app.newproj.lbrytv.util.cast
import app.newproj.lbrytv.util.mapPagingData
import javax.inject.Inject

class HomeRowPagingSource @Inject constructor(
    private val claimRepo: ClaimRepository,
    private val settingRepository: SettingRepository,
    private val userRepo: UserRepository,
) : PagingSource<Int, RowPresentable>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RowPresentable> {
        var id = -1L

        val subscribedContent = claimRepo.subscribedContent().mapPagingData { ClaimCard(it) }
        val subscribedContentRow =
            PagingDataList(++id, R.string.subscribed_content, R.drawable.lbc, subscribedContent.cast())

        val trending = claimRepo.trending().mapPagingData { ClaimCard(it) }
        val trendingRow = PagingDataList(++id, R.string.trending, R.drawable.trending, trending.cast())

        val subscription = claimRepo.subscription().mapPagingData { ClaimCard(it) }
        val subscriptionRow =
            PagingDataList(++id, R.string.subscriptions, R.drawable.subscriptions, subscription.cast())

        val suggestedChannels = claimRepo.suggestedChannels().mapPagingData { ClaimCard(it) }
        val suggestedChannelRow =
            PagingDataList(++id, R.string.suggested_channels, R.drawable.recommend, suggestedChannels.cast())

        val settings = settingRepository.settings().mapPagingData { SettingCard(it) }
        val settingRow = PagingDataList(++id, R.string.settings, R.drawable.settings, settings.cast())

        val user = userRepo.user()
        if (user.hasVerifiedEmail == true) {
            return LoadResult.Page(
                listOf(
                    subscribedContentRow,
                    trendingRow,
                    subscriptionRow,
                    suggestedChannelRow,
                    settingRow
                ), null, null
            )
        } else {
            return LoadResult.Page(
                listOf(
                    trendingRow,
                    suggestedChannelRow,
                    settingRow
                ), null, null
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, RowPresentable>): Int? = null
}
