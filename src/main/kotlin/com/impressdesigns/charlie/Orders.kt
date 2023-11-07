package com.impressdesigns.charlie

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

data class Order(val created: String, val number: String, val customerNumber: String)

@RestController
class OrderController {
    @GetMapping("/")
    fun index() = getOrder()
}


fun getOrder(): Order {
    connect().use {
        val query =
            it.prepareStatement("SELECT Orders.date_Creation AS date_created, Orders.ID_Order AS order_id, Orders.id_Customer AS customer_id FROM Orders WHERE Orders.ID_Order IN (314263)")
        val result = query.executeQuery()
        while (result.next()) {
            val dateCreated = result.getString("date_created")
            val orderId = result.getString("order_id")
            val customerId = result.getString("customer_id")
            return Order(dateCreated, orderId, customerId)
        }
    }
    throw Exception("TODO: want to just return an empty list if the order is not found")
}
