package de.uniks.codliners.stock_simulator.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.uniks.codliners.stock_simulator.BuildConfig
import de.uniks.codliners.stock_simulator.domain.HistoricalPrice
import de.uniks.codliners.stock_simulator.domain.Quote
import de.uniks.codliners.stock_simulator.domain.SearchResult
import de.uniks.codliners.stock_simulator.domain.Symbol
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val IEX_API_BASE_URL = BuildConfig.IEX_API_BASE_URL
const val IEX_API_TOKEN = BuildConfig.IEX_API_TOKEN

val moshi: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

interface IexApi {

    @GET("ref-data/symbols")
    suspend fun symbols(@Query("token") token: String = IEX_API_TOKEN): List<Symbol>

    @GET("search/{fragment}")
    suspend fun search(@Path("fragment") fragment: String, @Query("token") token: String = IEX_API_TOKEN): List<SearchResult>

    @GET("stock/{symbol}/chart/{range}/{date}")
    suspend fun historical(@Path("symbol") symbol: String, @Path("range") range: String, @Path("date") date: String, @Query("token") token: String = IEX_API_TOKEN, @Query("chartCloseOnly") chartCloseOnly: Boolean): List<HistoricalPrice>

    @GET("stock/{symbol}/quote")
    suspend fun quote(@Path("symbol") symbol: String, @Query("token") token: String = IEX_API_TOKEN): Quote
}

object NetworkService {
    private val retrofit = Retrofit.Builder()
        .baseUrl(IEX_API_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val IEX_API: IexApi = retrofit.create(IexApi::class.java)
}
