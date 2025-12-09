package com.monitoring.collector.controller

import com.monitoring.collector.model.IncidentStatus
import com.monitoring.collector.repository.IncidentRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/incidents")
class IncidentController(
    private val incidentRepository: IncidentRepository
) {

    @GetMapping
    fun getAllIncidents(): ResponseEntity<*> {
        val incidents = incidentRepository.findAll()
        return ResponseEntity.ok(incidents)
    }

    @GetMapping("/open")
    fun getOpenIncidents(): ResponseEntity<*> {
        val incidents = incidentRepository.findByStatus(IncidentStatus.OPEN)
        return ResponseEntity.ok(incidents)
    }

    @GetMapping("/resolved")
    fun getResolvedIncidents(): ResponseEntity<*> {
        val incidents = incidentRepository.findByStatus(IncidentStatus.RESOLVED)
        return ResponseEntity.ok(incidents)
    }

    @PutMapping("/{id}/resolve")
    fun resolveIncident(
        @PathVariable id: String,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<*> {
        val success = incidentRepository.resolveIncident(id, userDetails.username)
        return if (success) {
            ResponseEntity.ok(mapOf("message" to "Incident resolved successfully"))
        } else {
            ResponseEntity.badRequest().body(mapOf("error" to "Failed to resolve incident or already resolved"))
        }
    }
}
