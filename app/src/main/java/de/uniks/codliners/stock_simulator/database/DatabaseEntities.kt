package de.uniks.codliners.stock_simulator.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.uniks.codliners.stock_simulator.domain.Share

@Entity
data class ShareDatabase constructor(
    @PrimaryKey
    val id: String,
    val name: String,
    val value: Double,
    val runningCost: Double
)

fun List<ShareDatabase>.asDomainModel(): List<Share> {
    return map {
        Share(
            id = it.id,
            name = it.name,
            value = it.value,
            runningCost = it.runningCost)
    }
}