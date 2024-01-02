package com.impressdesigns.charlie

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CharlieApplication

fun main(args: Array<String>) {
    System.getenv("CHARLIE_JDBC_URI")
        ?: throw IllegalArgumentException("JDBC URI is required, please set CHARLIE_JDBC_URI")
    System.getenv("CHARLIE_JDBC_USERNAME")
        ?: throw IllegalArgumentException("Username is required, please set CHARLIE_JDBC_USERNAME")
    System.getenv("CHARLIE_JDBC_PASSWORD")
        ?: throw IllegalArgumentException("Password is required, please set CHARLIE_JDBC_PASSWORD")
    runApplication<CharlieApplication>(*args)
}
