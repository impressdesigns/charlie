package com.impressdesigns.charlie

import java.sql.Connection
import java.sql.DriverManager


fun connect(): Connection {
    return DriverManager.getConnection(
        System.getenv("CHARLIE_JDBC_URI"),
        System.getenv("CHARLIE_JDBC_USERNAME"),
        System.getenv("CHARLIE_JDBC_PASSWORD"),
    )
}
