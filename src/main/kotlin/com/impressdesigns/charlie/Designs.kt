package com.impressdesigns.charlie

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import kotlin.math.roundToInt
import kotlin.math.truncate


data class Design(val designNumber: Int, val title: String)


@RestController
@RequestMapping(path = ["designs"])
class DesignsController {
    @GetMapping("/{designNumber}")
    fun design(@PathVariable designNumber: Int) = getDesign(designNumber)
}


fun getDesign(designNumber: Int): Design {
    connect().use {
        val queryText = """
            SELECT 
                Des.ID_Design AS id,
                Des.DesignName AS title
            FROM Des
            WHERE Des.ID_Design = ?
    """.trimIndent()
        val query = it.prepareStatement(queryText)
        query.setInt(1, designNumber)
        val result = query.executeQuery()
        if (!result.next()) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Design not found")
        }
        return Design(
            truncate(result.getFloat("id")).roundToInt(),
            result.getString("title")
        )
    }
}
