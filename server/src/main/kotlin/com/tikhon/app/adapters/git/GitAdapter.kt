package com.tikhon.app.adapters.git

import com.tikhon.app.events.IGitEvent

abstract class GitAdapter(val gitName: String) {
    abstract fun parseEvent(data: String): IGitEvent?
}