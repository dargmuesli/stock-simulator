package de.uniks.codliners.stock_simulator.ui

import android.view.View
import android.widget.*
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.uniks.codliners.stock_simulator.R
import de.uniks.codliners.stock_simulator.database.DepotQuote
import de.uniks.codliners.stock_simulator.domain.*
import de.uniks.codliners.stock_simulator.domain.TransactionType.*
import de.uniks.codliners.stock_simulator.isWholeNumber
import de.uniks.codliners.stock_simulator.ui.account.DepotQuoteRecyclerViewAdapter
import de.uniks.codliners.stock_simulator.ui.achievements.AchievementsAdapter
import de.uniks.codliners.stock_simulator.ui.history.HistoryRecyclerViewAdapter
import de.uniks.codliners.stock_simulator.ui.news.NewsAdapter
import de.uniks.codliners.stock_simulator.ui.search.SearchResultAdapter
import de.uniks.codliners.stock_simulator.ui.stockbrot.StockbrotQuoteRecyclerViewAdapter

@BindingAdapter("visible")
fun View.bindVisibility(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("onRefresh")
fun SwipeRefreshLayout.bindRefreshListener(listener: Runnable) {
    setOnRefreshListener {
        listener.run()
    }
}

@BindingAdapter("searchResults")
fun RecyclerView.bindSearchResults(symbols: List<Symbol>?) {
    val adapter = adapter as SearchResultAdapter
    adapter.submitList(symbols)
}

@BindingAdapter("news")
fun RecyclerView.bindNews(news: List<News>?) {
    val adapter = adapter as NewsAdapter
    adapter.submitList(news)
}

@BindingAdapter("achievements")
fun RecyclerView.bindAchievements(achievements: List<Achievement>?) {
    val adapter = adapter as AchievementsAdapter
    adapter.submitList(achievements)
}

@BindingAdapter("depotQuotes")
fun RecyclerView.bindDepotQuotes(quotes: List<DepotQuote>?) {
    val adapter = adapter as DepotQuoteRecyclerViewAdapter
    adapter.submitList(quotes)
}

@BindingAdapter("depotQuoteText")
fun TextView.bindDepotQuoteText(depotQuote: DepotQuote?) {
    depotQuote?.let {
        text = when (depotQuote.amount.isWholeNumber()) {
            true ->
                String.format(
                    resources.getText(R.string.long_depot_quote_amount_format).toString(),
                    depotQuote.amount.toLong()
                )
            false ->
                String.format(
                    resources.getText(R.string.double_depot_quote_amount_format).toString(),
                    depotQuote.amount
                )
        }
    }
}

@BindingAdapter("depotQuote")
fun TextView.bindDepotQuote(depotQuote: DepotQuote?) {
    depotQuote?.let {
        text = when (depotQuote.amount.isWholeNumber()) {
            true ->
                String.format(
                    resources.getText(R.string.long_depot_quote_format).toString(),
                    depotQuote.id,
                    depotQuote.amount.toLong()
                )
            false ->
                String.format(
                    resources.getText(R.string.double_depot_quote_format).toString(),
                    depotQuote.id,
                    depotQuote.amount
                )
        }
    }
}

@BindingAdapter("transactions")
fun RecyclerView.bindTransactions(transactions: List<Transaction>?) {
    val adapter = adapter as HistoryRecyclerViewAdapter
    adapter.submitList(transactions)
}

@BindingAdapter("stockbrotQuotes")
fun RecyclerView.bindStockbrotQuotes(quotes: List<StockbrotQuote>?) {
    val adapter = adapter as StockbrotQuoteRecyclerViewAdapter
    adapter.submitList(quotes)
}

@BindingAdapter("transactionType")
fun ImageView.bindTransactionType(transactionType: TransactionType?) {
    transactionType?.let {
        val drawableId = when (transactionType) {
            BUY -> R.drawable.ic_shopping_cart_24dp
            SELL -> R.drawable.ic_attach_money_24dp
        }
        setImageDrawable(resources.getDrawable(drawableId, context.theme))
    }
}

@BindingAdapter("transaction")
fun TextView.bindTransaction(transaction: Transaction?) {
    transaction?.let {
        text = if (transaction.amount.isWholeNumber()) {
            val stringId = when (transaction.transactionType) {
                BUY -> R.string.long_buy_amount_format
                SELL -> R.string.long_sell_amount_format
            }
            String.format(resources.getText(stringId).toString(), transaction.amount.toLong())
        } else {
            val stringId = when (transaction.transactionType) {
                BUY -> R.string.double_buy_amount_format
                SELL -> R.string.double_sell_amount_format
            }
            String.format(resources.getText(stringId).toString(), transaction.amount)
        }
    }
}

@BindingAdapter("observeSelection")
fun Spinner.bindSelection(selection: MutableLiveData<String>) {
    val self = this

    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            // ignore, as it's impossible
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val selectedItem = self.getItemAtPosition(position).toString()
            selection.postValue(selectedItem)
        }
    }
}

@BindingAdapter("botAddRemoveQuote")
fun Button.bindBotAddRemoveQuote(enabled: Boolean) {
    text = if (enabled) {
        context.getText(R.string.stockbrot_remove_control_quote)
    } else {
        context.getText(R.string.stockbrot_add_control_quote)
    }
}
