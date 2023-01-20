package com.tikhon.app.adapters.messenger

abstract class MessengerAdapter(val messengerName: String) {
    private var state: MessengerAdapterState = MessengerAdapterState.CREATED

    fun start() {
        try {
            this.beforeStart()
            this.state = MessengerAdapterState.STARTED
        } catch (ex: Exception) {
            ex.printStackTrace() // TODO logger
            this.state = MessengerAdapterState.FAILED
        }
    }

    protected abstract fun beforeStart()
}