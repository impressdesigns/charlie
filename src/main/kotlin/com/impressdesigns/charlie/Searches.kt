package com.impressdesigns.charlie

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.sql.Date
import kotlin.math.roundToInt


data class DigitalProductionLine(
    val orderNumber: Int,
    val designNumber: Int,
    val quantity: Int,
    val instructions: String
)

data class NumberList(
    val numbers: List<Int>,
)


@RestController
@RequestMapping(path = ["search"])
class SearchController {
    @GetMapping("/open-digital-production-lines")
    fun openDigitalProductionLines() = getOpenDigitalProductionLines()

    @GetMapping("/orders-to-update")
    fun ordersToUpdate() = getOrdersToUpdate()

    @GetMapping("/designs-to-update")
    fun designsToUpdate() = getDesignsToUpdate()
}


fun getOpenDigitalProductionLines(): List<DigitalProductionLine> {
    connect().use {
        val queryText = """
            SELECT Orders.ID_Order             AS order_id,
                   OrderDes.id_Design          AS design_id,
                   LinesOE.cn_LineQuantity_Req AS quantity,
                   LinesOE.OrderInstructions   AS instructions
            FROM Orders
                     JOIN OrderDes ON Orders.ID_Order = OrderDes.id_Order
                     JOIN LinesOE ON Orders.ID_Order = LinesOE.id_Order
            WHERE Orders.sts_Produced IN (0, 0.5)
              AND Orders.id_OrderType = 63
              AND LinesOE.OrderInstructions IS NOT NULL 
    """.trimIndent()
        val query = it.prepareStatement(queryText)
        val result = query.executeQuery()
        val lines = mutableListOf<DigitalProductionLine>()
        while (result.next()) {
            val orderId = result.getInt("order_id")
            val designId = result.getInt("design_id")
            val quantity = result.getInt("quantity")
            val instructions = result.getString("instructions")
            lines.add(DigitalProductionLine(orderId, designId, quantity, instructions))
        }
        return lines
    }
}

fun getOrdersToUpdate(): NumberList {
    connect().use {
        val queryText = """
            SELECT ID_Order AS id
            FROM Orders
            WHERE Orders.ID_Order > 300000 -- Arbitrary limit to reduce runtime and to avoid pulling in invalid data 
              AND (Orders.date_Modification = ? OR Orders.sts_Invoiced = 0)
    """.trimIndent()
        val query = it.prepareStatement(queryText)
        query.setDate(1, Date(java.util.Date().time))
        val result = query.executeQuery()
        val numbers = mutableListOf<Int>()
        while (result.next()) {
            numbers.add(result.getInt("id"))
        }
        return NumberList(numbers)
    }
}


fun getDesignsToUpdate(): NumberList {
    connect().use {
        val queryText = """
            SELECT ID_Design AS id
            FROM Des
            WHERE Des.date_Modification = ?
    """.trimIndent()
        val query = it.prepareStatement(queryText)
        query.setDate(1, Date(java.util.Date().time))
        val result = query.executeQuery()
        val numbers = mutableListOf<Int>()
        while (result.next()) {
            numbers.add(result.getFloat("id").roundToInt())
        }
        return NumberList(numbers)
    }
}
