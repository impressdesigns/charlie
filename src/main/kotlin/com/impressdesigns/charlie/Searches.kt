package com.impressdesigns.charlie

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


data class Order(
    val orderNumber: Int,
    val customerPo: String,
    val customer: String,
    val customerRep: String,
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
    val statusProduced: Double,
    val statusShipped: Double,
    val statusInvoiced: Double,
    val statusPaid: Double,
    val notesOnOrder: String,
    val notesOnHold: String,
    val notesOnArt: String,
    val notesOnPurchasing: String,
    val notesOnPurchasingSub: String,
    val notesOnReceiving: String,
    val notesOnProduction: String,
    val notesOnShipping: String,
    val notesOnAccounting: String,
    val notesToArt: String,
    val notesToPurchasing: String,
    val notesToPurchasingSub: String,
    val notesToReceiving: String,
    val notesToProduction: String,
    val notesToFinishing: String,
    val notesToShipping: String,
    val notesToAccounting: String,
    val notesFormsOrderApproval: String,
    val notesFormsInvoice: String,
    val notesFormsPackingList: String,
    val notesToWebCustomer: String,
    val notesToWebSalesperson: String,
)

data class ProductionLine(val orderNumber: Int, val designNumber: Int, val quantity: Int, val instructions: String)
data class Design(val designNumber: Int)


@RestController
@RequestMapping(path = ["search"])
class SearchController {
    @GetMapping("/open-digital-production-lines/")
    fun openDigitalProductionLines() = getOpenDigitalProductionLines()

    @GetMapping("/open-orders/")
    fun openOrders() = getOpenOrders()

    @GetMapping("/designs-on-po/{po}/")
    fun designsOnPo(@PathVariable po: String) = getDesignsOnPo(po)
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
                Orders.CustomerPurchaseOrder                   AS customer_po,
                Cust.CompanyName                               AS customer,
                Cust.CustomerServiceRep                        AS customer_rep,
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
                Orders.sts_Produced                            AS status_produced,
                Orders.sts_Shipped                             AS status_shipped,
                Orders.sts_Invoiced                            AS status_invoiced,
                Orders.sts_Paid                                AS status_paid,
            
                -- Notes "on"
                Orders.NotesOnOrder                            AS notes_on_order,
                Orders.NotesOnHold                             AS notes_on_hold,
                Orders.NotesOnArt                              AS notes_on_art,
                Orders.NotesOnPurchasing                       AS notes_on_purchasing,
                Orders.NotesOnPurchasingSub                    AS notes_on_purchasing_sub,
                Orders.NotesOnReceiving                        AS notes_on_receiving,
                Orders.NotesOnProduction                       AS notes_on_production,
                Orders.NotesOnShipping                         AS notes_on_shipping,
                Orders.NotesOnAccounting                       AS notes_on_accounting,
            
                -- Notes "to"
                Orders.NotesToArt                              AS notes_to_art,
                Orders.NotesToPurchasing                       AS notes_to_purchasing,
                Orders.NotesToPurchasingSub                    AS notes_to_purchasing_sub,
                Orders.NotesToReceiving                        AS notes_to_receiving,
                Orders.NotesToProduction                       AS notes_to_production,
                Orders.NotesToFinishing                        AS notes_to_finishing,
                Orders.NotesToShipping                         AS notes_to_shipping,
                Orders.NotesToAccounting                       AS notes_to_accounting,
            
                -- Notes "forms"
                Orders.NotesFormsOrderApproval                 AS notes_forms_order_approval,
                Orders.NotesFormsInvoice                       AS notes_forms_invoice,
                Orders.NotesFormsPackingList                   AS notes_forms_packing_list,
            
                -- Notes "web"
                Orders.NotesToWebCustomer                      AS notes_to_web_customer,
                Orders.NotesToWebSalesperson                   AS notes_to_web_salesperson
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
                    result.getString("customer_po") ?: "",
                    result.getString("customer"),
                    result.getString("customer_rep") ?: "",
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
                    result.getDouble("status_produced"),
                    result.getDouble("status_shipped"),
                    result.getDouble("status_invoiced"),
                    result.getDouble("status_paid"),
                    result.getString("notes_on_order") ?: "",
                    result.getString("notes_on_hold") ?: "",
                    result.getString("notes_on_art") ?: "",
                    result.getString("notes_on_purchasing") ?: "",
                    result.getString("notes_on_purchasing_sub") ?: "",
                    result.getString("notes_on_receiving") ?: "",
                    result.getString("notes_on_production") ?: "",
                    result.getString("notes_on_shipping") ?: "",
                    result.getString("notes_on_accounting") ?: "",
                    result.getString("notes_to_art") ?: "",
                    result.getString("notes_to_purchasing") ?: "",
                    result.getString("notes_to_purchasing_sub") ?: "",
                    result.getString("notes_to_receiving") ?: "",
                    result.getString("notes_to_production") ?: "",
                    result.getString("notes_to_finishing") ?: "",
                    result.getString("notes_to_shipping") ?: "",
                    result.getString("notes_to_accounting") ?: "",
                    result.getString("notes_forms_order_approval") ?: "",
                    result.getString("notes_forms_invoice") ?: "",
                    result.getString("notes_forms_packing_list") ?: "",
                    result.getString("notes_to_web_customer") ?: "",
                    result.getString("notes_to_web_salesperson") ?: "",
                )
            )
        }
        return orders
    }
}

fun getDesignsOnPo(po: String): List<Design> {
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
        val designs = mutableListOf<Design>()
        while (result.next()) {
            designs.add(Design(result.getInt("design_number")))
        }
        return designs
    }
}
