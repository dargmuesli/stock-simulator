package de.uniks.codliners.stock_simulator.background

import android.content.Context
import androidx.work.*
import de.uniks.codliners.stock_simulator.background.Constants.Companion.BUY_AMOUNT_KEY
import de.uniks.codliners.stock_simulator.background.Constants.Companion.ID_KEY
import de.uniks.codliners.stock_simulator.background.Constants.Companion.THRESHOLD_BUY_KEY
import de.uniks.codliners.stock_simulator.background.Constants.Companion.THRESHOLD_SELL_KEY
import de.uniks.codliners.stock_simulator.background.Constants.Companion.TYPE_DEFAULT
import de.uniks.codliners.stock_simulator.background.Constants.Companion.TYPE_KEY
import de.uniks.codliners.stock_simulator.background.workers.StockbrotWorker
import de.uniks.codliners.stock_simulator.domain.StockbrotQuote
import java.util.concurrent.TimeUnit

class StockbrotWorkRequest(context: Context) {

    private val workManager: WorkManager = WorkManager.getInstance(context)
    private val intervalMinutes: Long = 15

    fun addQuote(stockbrotQuote: StockbrotQuote) {
        println("start StockbrotWorkRequest")

        val buyAmount = stockbrotQuote.buyAmount
        val thresholdBuy = stockbrotQuote.thresholdBuy
        val thresholdSell = stockbrotQuote.thresholdSell
        val id = stockbrotQuote.id

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val data = Data.Builder()
            .putString(ID_KEY, id)
            .putString(TYPE_KEY, TYPE_DEFAULT.toString())
            .putDouble(BUY_AMOUNT_KEY, buyAmount)
            .putDouble(THRESHOLD_BUY_KEY, thresholdBuy)
            .putDouble(THRESHOLD_SELL_KEY, thresholdSell)
            .build()

        val buildWorkerTag = buildWorkerTag(id)
        val workRequest = PeriodicWorkRequest.Builder(StockbrotWorker::class.java, intervalMinutes, TimeUnit.MINUTES)
            .addTag(buildWorkerTag)
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        workManager.enqueue(workRequest)
    }

    fun removeQuote(stockbrotQuote: StockbrotQuote) {
        println("stop StockbrotWorkRequest")
        val buildWorkerTag = buildWorkerTag(stockbrotQuote.id)
        workManager.cancelAllWorkByTag(buildWorkerTag)
    }

    fun cancelAll() {
        workManager.cancelAllWork()
    }

    private fun buildWorkerTag(symbol: String): String {
        return "WORKER_TAG_$symbol"
    }

}
