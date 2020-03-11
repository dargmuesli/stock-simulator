package de.uniks.codliners.stock_simulator.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import de.uniks.codliners.stock_simulator.domain.*
import de.uniks.codliners.stock_simulator.domain.Transaction

/**
 * The [Room](https://developer.android.com/jetpack/androidx/releases/room) database with all its [Dao](https://developer.android.com/reference/androidx/room/Dao)s.
 */
@Database(
    entities = [Symbol::class, DepotQuotePurchase::class, News::class, Transaction::class, Quote::class, Balance::class, HistoricalPrice::class, StockbrotQuote::class, DepotValue::class, Achievement::class],
    version = 27,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class StockAppDatabase : RoomDatabase() {
    /**
     * The database's [Symbol] [Dao](https://developer.android.com/reference/androidx/room/Dao).
     */
    abstract val symbolDao: SymbolDao

    /**
     * The database's [Quote] [Dao](https://developer.android.com/reference/androidx/room/Dao).
     */
    abstract val quoteDao: QuoteDao

    /**
     * The database's [News] [Dao](https://developer.android.com/reference/androidx/room/Dao).
     */
    abstract val newsDao: NewsDao

    /**
     * The database's [TransactionDao] [Dao](https://developer.android.com/reference/androidx/room/Dao).
     */
    abstract val transactionDao: TransactionDao

    abstract val accountDao: AccountDao

    abstract val stockbrotDao: StockbrotDao

    /**
     * The database's [HistoricalPrice] [Dao](https://developer.android.com/reference/androidx/room/Dao).
     */
    abstract val historicalDao: HistoricalPriceDao

    /**
     * The database's [Achievement] [Dao](https://developer.android.com/reference/androidx/room/Dao).
     */
    abstract val achievementDao: AchievementsDao
}

private lateinit var INSTANCE: StockAppDatabase

/**
 * Creates or returns this app's [StockAppDatabase].
 *
 * @param context The context of the app.
 * @return The [StockAppDatabase].
 *
 * @author Jan Müller
 */
fun getDatabase(context: Context): StockAppDatabase {
    synchronized(StockAppDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room
                .databaseBuilder(
                    context.applicationContext,
                    StockAppDatabase::class.java,
                    "StockAppDatabase"
                )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}

/**
 * [Dao] that manages account entities in the database.
 *
 * @author TODO
 * @author Jan Müller
 */
@Dao
interface AccountDao {

    /**
     * Deletes all [Balance]s from the database.
     *
     */
    @Query("DELETE FROM balance")
    fun deleteBalances()

    @Query("DELETE FROM depotquotepurchase")
    fun deleteDepot()

    @Delete
    fun deleteDepotQuotes(vararg depotPurchase: DepotQuotePurchase)

    @Query("DELETE FROM depotquotepurchase WHERE id == :id")
    fun deleteDepotQuoteById(id: String)

    @Query("DELETE FROM depotvalue")
    fun deleteDepotValues()

    @Query("SELECT COUNT(*) FROM balance")
    fun getBalanceCount(): Long

    @Query("SELECT * FROM (SELECT * FROM balance ORDER BY balance.timestamp DESC LIMIT :limit) ORDER BY timestamp ASC")
    fun getBalancesLimited(limit: Int): LiveData<List<Balance>>

    @Query("SELECT id, symbol, type, SUM(amount) as amount, AVG(buyingPrice) as buyingPrice FROM depotquotepurchase GROUP BY id, symbol, type")
    fun getDepotQuotes(): LiveData<List<DepotQuote>>

    @Query("SELECT * FROM depotquotepurchase WHERE id == :id LIMIT 1")
    fun getDepotQuoteById(id: String): DepotQuotePurchase?

    @Query("SELECT id, symbol, type, SUM(amount) as amount, AVG(buyingPrice) as buyingPrice FROM depotquotepurchase WHERE id = :id GROUP BY id, symbol, type")
    fun getDepotQuoteWithId(id: String): LiveData<DepotQuote>

    @Query("SELECT * FROM depotquotepurchase WHERE id = :id ORDER BY depotquotepurchase.buyingPrice ASC")
    fun getDepotQuotePurchasesByIdOrderedByPrice(id: String): List<DepotQuotePurchase>

    @Query("SELECT * FROM depotquotepurchase ORDER BY depotquotepurchase.buyingPrice ASC")
    fun getDepotQuotePurchasesValuesOrderedByPrice(): List<DepotQuotePurchase>

    @Query("SELECT * FROM (SELECT * FROM depotvalue ORDER BY depotvalue.timestamp DESC LIMIT :limit) ORDER BY timestamp ASC")
    fun getDepotValuesLimited(limit: Int): LiveData<List<DepotValue>>

    /**
     * Returns the latest [Balance], wrapped in [LiveData](https://developer.android.com/reference/android/arch/lifecycle/LiveData).
     *
     * @return [LiveData](https://developer.android.com/reference/android/arch/lifecycle/LiveData) containing the latest [Balance].
     */
    @Query("SELECT * FROM balance ORDER BY balance.timestamp DESC LIMIT 1")
    fun getLatestBalance(): LiveData<Balance>

    /**
     * Returns the latest [Balance].
     *
     * @return The latest [Balance].
     */
    @Query("SELECT * FROM balance ORDER BY balance.timestamp DESC LIMIT 1")
    fun getLatestBalanceValue(): Balance

    @Query("SELECT * FROM depotvalue ORDER BY depotvalue.timestamp DESC LIMIT 1")
    fun getLatestDepotValues(): LiveData<DepotValue>

    /**
     * Inserts a [Balance] into the database.
     *
     * @param balance The [Balance] to be inserted.
     */
    @Insert(onConflict = REPLACE)
    fun insertBalance(balance: Balance)

    @Insert(onConflict = REPLACE)
    fun insertDepotQuote(depotPurchase: DepotQuotePurchase)

    @Insert(onConflict = REPLACE)
    fun insertDepotValue(depotValue: DepotValue)
}

/**
 * [Dao] that manages [Achievement]s in the database
 *
 * @author Jan Müller
 * @author Lucas Held
 */
@Dao
interface AchievementsDao {

    @Query("delete from achievement where name = :name")
    fun deleteAchievementById(name: Int)

    @Query("delete from achievement")
    fun deleteAchievements()

    @Query("select * from achievement where name = :name")
    fun getAchievementByName(name: Int): Achievement?

    @Query("SELECT * FROM achievement ORDER BY timestamp DESC")
    fun getAchievements(): LiveData<List<Achievement>>

    @Query("select * from achievement where name = :name")
    fun getAchievementWithName(name: Int): LiveData<Achievement>

    @Insert(onConflict = REPLACE)
    fun insert(achievement: Achievement)

}

@Dao
interface HistoricalPriceDao {

    @Query("DELETE FROM historicalprice WHERE id = :id")
    fun deleteHistoricalPricesById(id: String)

    @Query("SELECT * FROM historicalprice WHERE id = :id")
    fun getHistoricalPricesById(id: String): LiveData<List<HistoricalPrice>>

    @Insert(onConflict = REPLACE)
    fun insertAll(vararg prices: HistoricalPrice)
}

/**
 * [Dao](https://developer.android.com/reference/androidx/room/Dao) that manages [News] in the database.
 *
 * @author Jonas Thelemann
 */
@Dao
interface NewsDao {

    /**
     * Deletes everything from the "news" table.
     */
    @Query("DELETE FROM news")
    fun deleteNews()

    /**
     * Inserts all given news articles into the "news" table.
     */
    @Insert(onConflict = REPLACE)
    fun insertAll(vararg news: News)
}

/**
 * [Dao](https://developer.android.com/reference/androidx/room/Dao) that manages [Symbol]s in the database.
 *
 * @author Jan Müller
 */
@Dao
interface QuoteDao {

    /**
     * Deletes all [Quote]s from the database.
     *
     */
    @Query("DELETE FROM quote")
    fun deleteQuotes()

    /**
     * Returns the [Quote] with the matching id.
     *
     * @param id The [Quote] id used in this query.
     * @return The [Quote] with this id or null if no such quote exists.
     */
    @Query("SELECT * FROM quote WHERE quote.id == :id")
    fun getQuoteValueById(id: String): Quote?

    /**
     * Returns the [Quote] with the matching id, wrapped in [LiveData](https://developer.android.com/reference/android/arch/lifecycle/LiveData).
     *
     * @param id The [Quote] id used in this query.
     * @return [LiveData](https://developer.android.com/reference/android/arch/lifecycle/LiveData) containing the [Quote] that matches the query parameters.
     */
    @Query("SELECT * FROM quote WHERE quote.id == :id")
    fun getQuoteWithId(id: String): LiveData<Quote>

    /**
     * Inserts a [Quote] into the database.
     *
     * @param quote The quote to be inserted into the database.
     */
    @Insert(onConflict = REPLACE)
    fun insert(quote: Quote)
}

/**
 * [Dao] that manages [StockbrotQuote]s in the database
 *
 * @author Lucas Held
 */
@Dao
interface StockbrotDao {

    @Query("DELETE FROM stockbrotquote WHERE id == :id")
    fun deleteStockbrotQuoteById(id: String)

    @Query("DELETE FROM stockbrotquote")
    fun deleteStockbrotQuotes()

    @Query("SELECT * FROM stockbrotquote WHERE id == :id LIMIT 1")
    fun getStockbrotQuoteById(id: String): StockbrotQuote?

    @Query("SELECT * FROM stockbrotquote")
    fun getStockbrotQuotes(): LiveData<List<StockbrotQuote>>

    @Query("SELECT * FROM stockbrotquote WHERE id == :id LIMIT 1")
    fun getStockbrotQuoteWithId(id: String): LiveData<StockbrotQuote>

    @Insert(onConflict = REPLACE)
    fun insertStockbrotQuote(stockbrotQuote: StockbrotQuote)
}

/**
 * [Dao](https://developer.android.com/reference/androidx/room/Dao) that manages [Symbol]s in the database.
 *
 * @author Jan Müller
 */
@Dao
interface SymbolDao {

    /**
     * Returns all [Symbol]s, wrapped in [LiveData](https://developer.android.com/reference/android/arch/lifecycle/LiveData).
     *
     * @return [LiveData](https://developer.android.com/reference/android/arch/lifecycle/LiveData) containing a [List] of all [Symbol]s.
     */
    @Query("SELECT * FROM symbol ORDER BY symbol.symbol ASC")
    fun getAll(): LiveData<List<Symbol>>

    /**
     * Returns all [Symbol]s matching the query parameters, wrapped in [LiveData](https://developer.android.com/reference/android/arch/lifecycle/LiveData).
     *
     * @param symbolQuery The symbol fragment used in this query.
     * @param nameQuery The name fragment used in this query.
     * @param type The [Symbol.Type] used in this query.
     * @return [LiveData](https://developer.android.com/reference/android/arch/lifecycle/LiveData) containing a [List] of all [Symbol]s matching the query parameters.
     */
    @Query("SELECT * FROM symbol WHERE symbol.type == :type AND (symbol.symbol LIKE :symbolQuery OR symbol.name LIKE :nameQuery) ORDER BY symbol.symbol ASC")
    fun getAllFiltered(symbolQuery: String, nameQuery: String, type: Symbol.Type): LiveData<List<Symbol>>

    /**
     * Returns all [Symbol]s matching the query parameters, wrapped in [LiveData](https://developer.android.com/reference/android/arch/lifecycle/LiveData).
     *
     * @param symbolQuery The symbol fragment used in this query.
     * @param nameQuery The name fragment used in this query.
     * @return [LiveData](https://developer.android.com/reference/android/arch/lifecycle/LiveData) containing a [List] of all [Symbol]s matching the query parameters.
     */
    @Query("SELECT * FROM symbol WHERE symbol.symbol LIKE :symbolQuery OR symbol.name LIKE :nameQuery ORDER BY symbol.symbol ASC")
    fun getAllFiltered(symbolQuery: String, nameQuery: String): LiveData<List<Symbol>>

    /**
     * Inserts all [Symbol]s into the [StockAppDatabase].
     *
     * @param symbols The [Symbol]s to be inserted.
     */
    @Insert(onConflict = REPLACE)
    fun insertAll(vararg symbols: Symbol)
}

@Dao
interface TransactionDao {

    @Query("DELETE FROM `transaction`")
    fun deleteTransactions()

    @Query("SELECT * FROM `transaction` ORDER BY `transaction`.date DESC")
    fun getTransactions(): LiveData<List<Transaction>>

    @Query("SELECT * from `transaction` WHERE `transaction`.id = :id")
    fun getTransactionsById(id: String): LiveData<List<Transaction>>

    @Query("SELECT * from `transaction` LIMIT :limit")
    fun getTransactionsLimited(limit: Int): LiveData<List<Transaction>>

    @Insert
    fun insert(transaction: Transaction)
}
