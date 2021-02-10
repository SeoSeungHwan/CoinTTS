package com.router.cointts.model

data class CoinInfo(
    val acc_trade_price: Double,
    val acc_trade_price_24h: Double,
    val acc_trade_volume: Double,
    val acc_trade_volume_24h: Double,
    val change: String,
    val change_price: Double,
    val change_rate: Double,
    val high_price: Double,
    val highest_52_week_date: String,
    val highest_52_week_price: Double,
    val low_price: Double,
    val lowest_52_week_date: String,
    val lowest_52_week_price: Double,
    val market: String,
    val opening_price: Double,
    val prev_closing_price: Double,
    val signed_change_price: Double,
    val signed_change_rate: Double,
    val timestamp: Long,
    val trade_date: String,
    val trade_date_kst: String,
    val trade_price: String,
    val trade_time: String,
    val trade_time_kst: String,
    val trade_timestamp: Long,
    val trade_volume: Double
)