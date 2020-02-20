package de.uniks.codliners.stock_simulator.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.github.mikephil.charting.data.Entry
import de.uniks.codliners.stock_simulator.BuildConfig
import de.uniks.codliners.stock_simulator.database.*
import de.uniks.codliners.stock_simulator.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val BALANCE_LIMIT: Int = 50

class AccountRepository(private val database: StockAppDatabase) {

    constructor(context: Context) : this(getDatabase(context))

    val latestBalance by lazy {
        database.accountDao.getLatestBalance()
    }

    val balances by lazy {
        database.accountDao.getBalances()
    }
    // the last 50 account balance values
    val balancesLimited by lazy {
        database.accountDao.getBalancesLimited(BALANCE_LIMIT)
    }

    val depot by lazy {
        database.accountDao.getDepotQuotes()
    }
    // the last 50 account depot values
    val depotValuesLimited by lazy {
        database.accountDao.getDepotValuesLimited(BALANCE_LIMIT)
    }

    val currentDepotValue by lazy {
        database.accountDao.getLatestDepotValues()
    }

    fun depotQuoteWithSymbol(symbol: String): LiveData<DepotQuote> =
        database.accountDao.getDepotQuoteWithSymbol(symbol)

    suspend fun buy(quote: Quote, amount: Int) {
        val lastBalance = latestBalance.value
        lastBalance?.let {
            withContext(Dispatchers.IO) {
                val cashflow = -(quote.latestPrice * amount) - BuildConfig.TRANSACTION_COSTS
                val newBalance = Balance(lastBalance.value + cashflow)

                val depotQuote = database.accountDao.getDepotQuoteBySymbol(quote.symbol)
                    ?: DepotQuote(quote.symbol, 0)
                val newDepotQuote = depotQuote.copy(amount = depotQuote.amount + amount)

                val transaction = TransactionDatabase(
                    symbol = quote.symbol,
                    companyName = quote.companyName,
                    amount = amount,
                    price = quote.latestPrice,
                    transactionCosts = BuildConfig.TRANSACTION_COSTS,
                    cashflow = cashflow,
                    transactionType = TransactionType.BUY,
                    date = System.currentTimeMillis()
                )

                database.accountDao.apply {
                    insertBalance(newBalance)
                    insertDepotQuote(newDepotQuote)
                }
                database.transactionDao.insert(transaction)
            }
        }
    }

    suspend fun fetchCurrentDepotValue() {
        val depotValue = currentDepotValue.value
        var newDepotValue: Double = 0.0
        depotValue?.let {
            val depotQuotes = database.accountDao.getDepotQuotes()
            depotQuotes.value?.map { depotQuote ->
                val quotePrice = database.quoteDao.getQuoteWithSymbol(depotQuote.symbol).value!!.latestPrice
                val depotQuoteAmount = depotQuote.amount
                val depotQuoteValue = quotePrice * depotQuoteAmount
                newDepotValue += depotQuoteValue
            }
            val newDepotValue = DepotValue(newDepotValue)
            database.accountDao.insertDepotValue(newDepotValue)
        }
    }

    suspend fun sell(quote: Quote, amount: Int) {
        val lastBalance = latestBalance.value
        lastBalance?.let {
            withContext(Dispatchers.IO) {
                val cashflow = quote.latestPrice * amount - BuildConfig.TRANSACTION_COSTS
                val newBalance = Balance(lastBalance.value + cashflow)

                val depotQuote = database.accountDao.getDepotQuoteBySymbol(quote.symbol)!!
                val newDepotQuote = depotQuote.copy(amount = depotQuote.amount - amount)

                val transaction = TransactionDatabase(
                    symbol = quote.symbol,
                    companyName = quote.companyName,
                    amount = amount,
                    price = quote.latestPrice,
                    transactionCosts = BuildConfig.TRANSACTION_COSTS,
                    cashflow = cashflow,
                    transactionType = TransactionType.SELL,
                    date = System.currentTimeMillis()
                )

                database.accountDao.apply {
                    insertBalance(newBalance)
                    if (newDepotQuote.amount > 0) {
                        insertDepotQuote(newDepotQuote)
                    } else {
                        deleteDepotQuoteBySymbol(newDepotQuote.symbol)
                    }
                }
                database.transactionDao.insert(transaction)
            }
        }
    }

    suspend fun resetAccount() {
        withContext(Dispatchers.IO) {
            database.accountDao.apply {
                deleteAccount()
                setStartBalance()
            }
        }
    }

    private fun AccountDao.setStartBalance() {
        val starterBalance = Balance(value = BuildConfig.NEW_ACCOUNT_BALANCE)
        insertBalance(starterBalance)
    }

    private fun AccountDao.deleteAccount() {
        deleteDepot()
        deleteBalances()
        deleteDepotValues()
    }
}
