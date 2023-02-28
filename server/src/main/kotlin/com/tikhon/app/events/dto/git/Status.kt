package com.tikhon.app.events.dto.git

enum class Status(val content: String) {
    FAILED("❗ (failed)"),
    RUNNING("▶ (running)"),
    SUCCESS("✅ (success)"),
    PENDING("⌛ (pending)"),
    CANCELED("⦸ (canceled)"),
}