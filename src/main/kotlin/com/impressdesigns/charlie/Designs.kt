package com.impressdesigns.charlie

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


data class DesignTitle(val title: String)

@RestController
@RequestMapping(path = ["designs"])
class DesignsController {

    @GetMapping("/{designNumber}/title/")
    fun designTitle(@PathVariable designNumber: Int) = getDesignTitle(designNumber)
}

fun getDesignTitle(designNumber: Int): DesignTitle {
    connect().use {
        val queryText = """
            SELECT DesignName AS design_title
            FROM Des
            WHERE ID_Design = ?
    """.trimIndent()
        val query = it.prepareStatement(queryText)
        query.setInt(1, designNumber)
        val result = query.executeQuery()
        result.next()
        return DesignTitle(result.getString("design_title"))
    }
}
