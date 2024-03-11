package com.impressdesigns.charlie

import org.springframework.web.bind.annotation.*

data class DesignNumbersList(val designNumbers: List<Int>)
data class Design(val designNumber: Int, val title: String)
data class DesignTitle(val title: String)

@RestController
@RequestMapping(path = ["designs"])
class DesignsController {

    @GetMapping("/{designNumber}/title/")
    fun designTitle(@PathVariable designNumber: Int) = getDesignTitle(designNumber)

    @PostMapping("/")
    fun post(@RequestBody designNumbersList: DesignNumbersList) = getDesignTitlesBulk(designNumbersList.designNumbers)
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

fun getDesignTitlesBulk(designNumbers: List<Int>): HashMap<Int, String> {
    if (designNumbers.isEmpty()) {
        return HashMap()
    }
    connect().use {
        var queryText = """
            SELECT 
                ID_Design AS id,
                DesignName AS title
            FROM Des
            WHERE ID_Design IN (?)
    """.trimIndent()
        val questionMarks = (1..designNumbers.size).joinToString(separator = ",") { "?" }
        queryText = queryText.replace("?", questionMarks)
        val query = it.prepareStatement(queryText)
        designNumbers.forEachIndexed { index, element ->
            query.setInt(index+1, element)
        }
        val result = query.executeQuery()
        val titles: HashMap<Int, String> = HashMap()

        while (result.next()) {
            titles[result.getInt("id")] = result.getString("title")
        }
        return titles
    }
}
