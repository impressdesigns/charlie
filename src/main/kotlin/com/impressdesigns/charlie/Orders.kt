package com.impressdesigns.charlie

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.sql.Date

data class Notes(
    val onOrder: String,
    val onHold: String,
    val onArt: String,
    val onPurchasing: String,
    val onPurchasingSub: String,
    val onReceiving: String,
    val onProduction: String,
    val onShipping: String,
    val onAccounting: String,
    val toArt: String,
    val toPurchasing: String,
    val toPurchasingSub: String,
    val toReceiving: String,
    val toProduction: String,
    val toFinishing: String,
    val toShipping: String,
    val toAccounting: String,
    val notesFormsOrderApproval: String,
    val notesFormsInvoice: String,
    val notesFormsPackingList: String,
    val toWebCustomer: String,
    val toWebSalesperson: String,
)

data class Statuses(
    val statusArt: Double,
    val statusPurchased: Double,
    val statusPurchasedSub: Double,
    val statusReceived: Double,
    val statusReceivedSub: Double,
    val statusProduced: Double,
    val statusShipped: Double,
    val statusInvoiced: Double,
    val statusPaid: Double,
)

data class Order(
    val orderNumber: Int,
    val customerPo: String,
    val customerID: Int,
    val customerRep: String,
    val createdByID: Int,
    val orderTypeID: Double,
    val onHold: Boolean,
    val productQuantity: Int,
    val decorationQuantity: Int,
    val dateCreated: Date?,
    val datePlaced: Date?,
    val dateApproximateShip: Date?,
    val dateInHands: Date?,
    val statuses: Statuses,
    val notes: Notes,
)


@RestController
@RequestMapping(path = ["orders"])
class OrdersController {
    @GetMapping("/{orderNumber}/")
    fun order(@PathVariable orderNumber: Int) = getOrder(orderNumber)
}


fun getOrder(orderNumber: Int): Order {
    connect().use {
        val queryText = """
            SELECT
                -- Summary
                Orders.ID_Order                                AS order_id,
                Orders.CustomerPurchaseOrder                   AS customer_po,
                Orders.id_Customer                             AS customer_id,
                Cust.CustomerServiceRep                        AS customer_rep,
                Orders.id_EmpCreatedBy                         AS created_by_id,
                Orders.id_OrderType                            AS order_type_id,
                UPPER(Orders.HoldOrderText)                    AS on_hold_text,
            
                -- Quantities
                Orders.cn_TotalProductQty_ToProduce            AS product_quantity,
                Orders.cn_TotalProductQty_Imprints             AS decoration_quantity,
            
                -- Dates
                Orders.date_Creation                           AS date_created,
                Orders.date_OrderPlaced                        AS date_placed,
                Orders.date_OrderRequestedToShip               AS date_approximate_ship,
                Orders.date_OrderDropDead                      AS date_in_hands,
            
                -- Statuses
                CASE Orders.sts_01 WHEN 10 THEN 1 ELSE 0 END   AS status_hold,
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
            WHERE Orders.ID_Order = ?           
    """.trimIndent()
        val query = it.prepareStatement(queryText)
        query.setInt(1, orderNumber)
        val result = query.executeQuery()
        result.next()
        return Order(
            result.getInt("order_id"),
            result.getString("customer_po") ?: "",
            result.getInt("customer_id"),
            result.getString("customer_rep") ?: "",
            result.getInt("created_by_id"),
            result.getDouble("order_type_id"),
            result.getInt("status_hold") == 1,
            result.getInt("product_quantity"),
            result.getInt("decoration_quantity"),
            result.getDate("date_created"),
            result.getDate("date_placed"),
            result.getDate("date_approximate_ship"),
            result.getDate("date_in_hands"),
            Statuses(
                result.getDouble("status_art"),
                result.getDouble("status_purchased"),
                result.getDouble("status_purchased_sub"),
                result.getDouble("status_received"),
                result.getDouble("status_received_sub"),
                result.getDouble("status_produced"),
                result.getDouble("status_shipped"),
                result.getDouble("status_invoiced"),
                result.getDouble("status_paid"),
            ),
            Notes(
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
            ),
        )
    }
}
