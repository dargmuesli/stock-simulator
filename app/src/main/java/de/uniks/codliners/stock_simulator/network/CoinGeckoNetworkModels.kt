package de.uniks.codliners.stock_simulator.network

import com.squareup.moshi.JsonClass
import de.uniks.codliners.stock_simulator.database.HistoricalPrice
import de.uniks.codliners.stock_simulator.domain.Quote
import de.uniks.codliners.stock_simulator.domain.Symbol
import retrofit2.http.Field
import java.util.*

@JsonClass(generateAdapter = true)
data class CoinGeckoSymbol(
    val id: String,
    val symbol: String,
    val name: String
)

fun CoinGeckoSymbol.asDomainSymbol() = Symbol(
    id = id,
    symbol = symbol.toUpperCase(Locale.ROOT),
    name = id,
    type = Symbol.Type.CRYPTO
)

fun List<CoinGeckoSymbol>.asDomainSymbols() = map { it.asDomainSymbol() }.toTypedArray()

@JsonClass(generateAdapter = true)
data class CoinGeckoMarketChart(
    val prices: List<Pair<Long, Double>>
)

private fun Pair<Long, Double>.asHistoricalPrice(id: String) = HistoricalPrice(
    id = id,
    date = first,
    price = second
)

fun CoinGeckoMarketChart.asHistoricalPrices(symbol: String) =
    prices.map { it.asHistoricalPrice(symbol) }


@JsonClass(generateAdapter = true)
data class CoinGeckoQuote(
    val id: String,
    val symbol: String,
    val name: String,
    @Field("market_data")
    val marketData: CoinGeckoMarketData
)

@JsonClass(generateAdapter = true)
data class CoinGeckoMarketData(
    @Field("current_price")
    val currentPrices: Map<String, Double>
)

fun CoinGeckoQuote.asDomainQuote() = Quote(
    id = id,
    symbol = symbol.toUpperCase(Locale.ROOT),
    type = Symbol.Type.CRYPTO,
    name = name,
    latestPrice = marketData.currentPrices["usd"] ?: 0.0,
    change = 0.0
)