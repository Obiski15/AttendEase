package com.example.attendease.utils

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object DateUtils {
    /**
     * Parses an ISO-8601 string into a Triple of (Day, Month, Time).
     * E.g., "2026-06-25T10:15:30Z" -> ("25", "Jun", "10:15")
     */
    fun parseIsoDateToDayMonthTime(isoString: String?): Triple<String, String, String> {
        if (isoString.isNullOrBlank()) return Triple("--", "---", "--:--")
        return try {
            val zdt = try {
                ZonedDateTime.parse(isoString).withZoneSameInstant(java.time.ZoneId.systemDefault())
            } catch (e: Exception) {
                // If it fails to parse as ZonedDateTime (e.g. missing 'Z'), assume it's UTC from backend
                java.time.LocalDateTime.parse(isoString)
                    .atZone(java.time.ZoneOffset.UTC)
                    .withZoneSameInstant(java.time.ZoneId.systemDefault())
            }
            val day = zdt.format(DateTimeFormatter.ofPattern("dd"))
            val month = zdt.format(DateTimeFormatter.ofPattern("MMM"))
            val time = zdt.format(DateTimeFormatter.ofPattern("HH:mm"))
            Triple(day, month, time)
        } catch (e: Exception) {
            Triple("--", "---", "--:--")
        }
    }
    
    /**
     * Parses an ISO-8601 string and returns just the HH:mm portion.
     */
    fun parseIsoTimeToDisplay(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "--:--"
        return try {
            val zdt = try {
                ZonedDateTime.parse(isoString).withZoneSameInstant(java.time.ZoneId.systemDefault())
            } catch (e: Exception) {
                // If it fails to parse as ZonedDateTime (e.g. missing 'Z'), assume it's UTC from backend
                java.time.LocalDateTime.parse(isoString)
                    .atZone(java.time.ZoneOffset.UTC)
                    .withZoneSameInstant(java.time.ZoneId.systemDefault())
            }
            zdt.format(DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e: Exception) {
            "--:--"
        }
    }
}
