package com.impressdesigns.charlie

import org.springframework.web.bind.annotation.*

data class Design(val designNumber: Float, val title: String)

@RestController
@RequestMapping(path = ["designs"])
class DesignsController {
    @GetMapping("/")
    fun designs() = getDesigns()
}

fun getDesigns(): List<Design> {
    connect().use {
        val queryText = """
            SELECT 
                ID_Design AS id,
                DesignName AS title
            FROM Des
    """.trimIndent()
        val query = it.prepareStatement(queryText)
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
