package com.impressdesigns.charlie

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


data class ProductionLine(val orderNumber: Int, val designNumber: Int, val quantity: Int, val instructions: String)
data class Order(
    val orderNumber: Int,
    val customer: String,
    val createdBy: String,
    val orderType: Double,
    val onHoldText: String,
    val productQuantity: Int,
    val decorationQuantity: Int,
    val dateCreated: String,
    val datePlaced: String,
    val dateApproximateShip: String,
    val dateInHands: String,
    val statusArt: Double,
    val statusPurchased: Double,
    val statusPurchasedSub: Double,
    val statusReceived: Double,
    val statusReceivedSub: Double,
    val statusShipped: Double,
    val statusInvoiced: Double,
    val statusPaid: Double,
)

@RestController
@RequestMapping(path = ["search"])
class SearchController {
    @GetMapping("/open-digital-production-lines/")
    fun openDigitalProductionLines() = getOpenDigitalProductionLines()

    @GetMapping("/open-orders/")
    fun openOrders() = getOpenOrders()
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

fun getOpenOrders(): List<Order> {
    connect().use {
        val queryText = """
            SELECT
                -- Summary
                Orders.ID_Order                                AS order_id,
                Cust.CompanyName                               AS customer,
                Emp.ct_NameFull                                AS created_by,
                Orders.id_OrderType                            AS order_type_id,
                Orders.HoldOrderText                           AS on_hold_text,
            
                -- Quantities
                Orders.cn_TotalProductQty_ToProduce            AS product_quantity,
                Orders.cn_TotalProductQty_Imprints             AS decoration_quantity,
            
                -- Dates
                COALESCE(Orders.date_Creation, '')             AS date_created,
                COALESCE(Orders.date_OrderPlaced, '')          AS date_placed,
                COALESCE(Orders.date_OrderRequestedToShip, '') AS date_approximate_ship,
                COALESCE(Orders.date_OrderDropDead, '')        AS date_in_hands,
            
                -- Statuses
                Orders.sts_ArtDone                             AS status_art,
                Orders.sts_Purchased                           AS status_purchased,
                Orders.sts_PurchasedSub                        AS status_purchased_sub,
                Orders.sts_Received                            AS status_received,
                Orders.sts_ReceivedSub                         AS status_received_sub,
                Orders.sts_Shipped                             AS status_shipped,
                Orders.sts_Invoiced                            AS status_invoiced,
                Orders.sts_Paid                                AS status_paid
            FROM Orders
                     JOIN Cust ON Cust.ID_Customer = Orders.id_Customer
                     JOIN Emp ON Orders.id_EmpCreatedBy = Emp.ID_Employee
            WHERE Orders.cn_Display_Status_08 = 0
            ORDER BY Orders.date_OrderRequestedToShip
    """.trimIndent()
        val query = it.prepareStatement(queryText)
        val result = query.executeQuery()
        val orders = mutableListOf<Order>()
        while (result.next()) {
            orders.add(
                Order(
                    result.getInt("order_id"),
                    result.getString("customer"),
                    result.getString("created_by"),
                    result.getDouble("order_type_id"),
                    result.getString("on_hold_text"),
                    result.getInt("product_quantity"),
                    result.getInt("decoration_quantity"),
                    result.getString("date_created") ?: "",
                    result.getString("date_placed") ?: "",
                    result.getString("date_approximate_ship") ?: "",
                    result.getString("date_in_hands") ?: "",
                    result.getDouble("status_art"),
                    result.getDouble("status_purchased"),
                    result.getDouble("status_purchased_sub"),
                    result.getDouble("status_received"),
                    result.getDouble("status_received_sub"),
                    result.getDouble("status_shipped"),
                    result.getDouble("status_invoiced"),
                    result.getDouble("status_paid"),
                )
            )
        }
        return orders
    }
}
