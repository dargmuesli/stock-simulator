package de.uniks.codliners.stock_simulator.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResult(
    val symbol: String,
    val securityName: String,
    val securityType: String,
    val exchange: String
)

@JsonClass(generateAdapter = true)
data class Symbol(
    val symbol: String,
    val exchange: String,
    val name: String,
    val date: String,
    val isEnabled: Boolean,
    val type: IssueType,
    val region: String,
    val currency: String,
    val iexId: String
)

enum class IssueType {
    ad, re, ce, si, lp, cs, et, wt, rt, oef, cef, ps, ut, struct, temp
}

@Entity
@JsonClass(generateAdapter = true)
data class Quote(
    @PrimaryKey
    val symbol: String,
    val companyName: String,
    val calculationPrice: String,
    val open: Double,
    val openTime: Long,
    val close: Double,
    val closeTime: Long,
    val high: Double,
    val low: Double,
    val latestPrice: Double,
    val latestSource: String,
    val latestTime: String,
    val latestUpdate: Long,
    val latestVolume: Long,
    val volume: Long,
    val iexRealtimePrice: Double,
    val iexRealtimeSize: Long,
    val iexLastUpdated: Long,
    val delayedPrice: Double,
    val delayedPriceTime: Long,
    val oddLotDelayedPrice: Double,
    val oddLotDelayedPriceTime: Long,
    val extendedPrice: Double,
    val extendedChange: Double,
    val extendedChangePercent: Double,
    val extendedPriceTime: Long,
    val previousClose: Double,
    val previousVolume: Long,
    val change: Double,
    val changePercent: Double,
    val iexMarketPercent: Double,
    val iexVolume: Long,
    val avgTotalVolume: Long,
    val iexBidPrice: Double,
    val iexBidSize: Long,
    val iexAskPrice: Double,
    val iexAskSize: Double,
    val marketCap: Long,
    val week52High: Double,
    val week52Low: Double,
    val ytdChange: Double,
    val peRatio: Double,
    val lastTradeTime: Long,
    val isUSMarketOpen: Boolean
)
