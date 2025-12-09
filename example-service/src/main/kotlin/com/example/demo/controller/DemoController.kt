package com.example.demo.controller

import org.springframework.web.bind.annotation.*
import kotlin.random.Random

@RestController
@RequestMapping("/api")
class DemoController {

    @GetMapping("/users")
    fun getUsers(): List<User> {
        // Simulate some processing time
        Thread.sleep(Random.nextLong(50, 300))
        return listOf(
            User("1", "John Doe", "john@example.com"),
            User("2", "Jane Smith", "jane@example.com")
        )
    }

    @GetMapping("/users/{id}")
    fun getUserById(@PathVariable id: String): User {
        Thread.sleep(Random.nextLong(50, 200))
        return User(id, "User $id", "user$id@example.com")
    }

    @PostMapping("/users")
    fun createUser(@RequestBody user: User): User {
        Thread.sleep(Random.nextLong(100, 400))
        return user.copy(id = Random.nextInt(100, 999).toString())
    }

    @GetMapping("/slow-endpoint")
    fun slowEndpoint(): Map<String, String> {
        // Simulate slow processing (will trigger slow API alert)
        Thread.sleep(Random.nextLong(600, 1200))
        return mapOf("message" to "This endpoint is intentionally slow")
    }

    @GetMapping("/error-endpoint")
    fun errorEndpoint(): Nothing {
        // Simulate server error (will trigger broken API alert)
        throw RuntimeException("Simulated server error")
    }

    @GetMapping("/reports")
    fun generateReport(): Map<String, Any> {
        // Random latency - sometimes slow
        Thread.sleep(Random.nextLong(100, 800))
        return mapOf(
            "reportId" to Random.nextInt(1000, 9999),
            "status" to "completed",
            "records" to Random.nextInt(100, 1000)
        )
    }
}

data class User(
    val id: String,
    val name: String,
    val email: String
)
