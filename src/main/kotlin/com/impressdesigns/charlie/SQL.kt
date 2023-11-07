package com.impressdesigns.charlie

import java.sql.Connection
import java.sql.DriverManager


fun connect(): Connection {
    val url = System.getenv("CHARLIE_JDBC_URI")
        ?: throw IllegalArgumentException("JDBC URI is required, please set CHARLIE_JDBC_URI")
    val username = System.getenv("CHARLIE_JDBC_USERNAME")
        ?: throw IllegalArgumentException("Username is required, please set CHARLIE_JDBC_USERNAME")
    val password = System.getenv("CHARLIE_JDBC_PASSWORD")
        ?: throw IllegalArgumentException("Password URI is required, please set CHARLIE_JDBC_PASSWORD")
    return DriverManager.getConnection(url, username, password)
}
