package com.impressdesigns.charlie

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


data class ProductionLine(val orderNumber: Int, val designNumber: Int, val quantity: Int, val instructions: String)

@RestController
@RequestMapping(path = ["search"])
class SearchController {
    @GetMapping("/open-digital-production-lines")
    fun openDigitalProductionLines() = getOpenDigitalProductionLines()
}


fun getOpenDigitalProductionLines(): List<ProductionLine> {
    connect().use {
        val queryText = """
            SELECT Orders.ID_Order             AS order_id,
                   OrderDes.id_Design          AS design_id,
                   LinesOE.cn_LineQuantity_Req AS quantity,
                   LinesOE.OrderInstructions   AS instructions
            FROM Orders
                     JOIN OrderDes ON Orders.ID_Order = OrderDes.id_Order
                     JOIN LinesOE ON Orders.ID_Order = LinesOE.id_Order
            WHERE Orders.cn_Display_Status_06 IN (0, 0.5)
              AND Orders.id_OrderType = 63
              AND LinesOE.OrderInstructions IS NOT NULL 
    """.trimIndent()
        val query = it.prepareStatement(queryText)
        val result = query.executeQuery()
        val lines = mutableListOf<ProductionLine>()
        while (result.next()) {
            val orderId = result.getInt("order_id")
            val designId = result.getInt("design_id")
            val quantity = result.getInt("quantity")
            val instructions = result.getString("instructions")
            lines.add(ProductionLine(orderId, designId, quantity, instructions))
        }
        return lines
    }
}
