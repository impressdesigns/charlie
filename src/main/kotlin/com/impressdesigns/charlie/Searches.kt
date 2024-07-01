package com.impressdesigns.charlie

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.sql.Date


data class DigitalProductionLine(
    val orderNumber: Int,
    val designNumber: Int,
    val quantity: Int,
    val instructions: String
)

data class Design(val designNumber: Float, val title: String)
data class DesignNumber(val designNumber: Int)


@RestController
@RequestMapping(path = ["search"])
class SearchController {
    @GetMapping("/open-digital-production-lines")
    fun openDigitalProductionLines() = getOpenDigitalProductionLines()

    @GetMapping("/designs-updated-today")
    fun designsUpdatedToday() = getDesignsUpdatedToday()

    @GetMapping("/designs-on-po/{po}/")
    fun designsOnPo(@PathVariable po: String) = getDesignsOnPo(po)
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

fun getDesignsUpdatedToday(): List<Design> {
    connect().use {
        val queryText = """
            SELECT 
                ID_Design AS id,
                DesignName AS title
            FROM Des
            WHERE Des.date_Modification = ?
    """.trimIndent()
        val query = it.prepareStatement(queryText)
        query.setDate(1, Date(java.util.Date().time))
        val result = query.executeQuery()
        val designs = mutableListOf<Design>()
        while (result.next()) {
            val id = result.getFloat("id")
            val title = result.getString("title") ?: ""
            designs.add(Design(id, title))
        }
        return designs
    }
}

fun getDesignsOnPo(po: String): List<DesignNumber> {
    connect().use {
        val queryText = """
            SELECT OrderDes.id_Design AS design_number
            FROM OrderDes
            JOIN Orders ON Orders.ID_Order = OrderDes.id_Order
            WHERE Orders.CustomerPurchaseOrder = ?
    """.trimIndent()
        val query = it.prepareStatement(queryText)
        query.setString(1, po)
        val result = query.executeQuery()
        val designs = mutableListOf<DesignNumber>()
        while (result.next()) {
            designs.add(DesignNumber(result.getInt("design_number")))
        }
        return designs
    }
}
