package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.example.demo", "com.monitoring.client"])
class ExampleServiceApplication

fun main(args: Array<String>) {
    runApplication<ExampleServiceApplication>(*args)
}
