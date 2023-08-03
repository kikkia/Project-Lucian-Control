package com.kikkia.project_lucian.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.nats.client.Connection
import io.nats.client.Nats
import io.nats.client.Options
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.nio.charset.StandardCharsets
import java.util.logging.Logger
import kotlin.math.log

class NatsClient : ViewModel() {

    lateinit var natsConn: Connection
    var natsEnabled = MutableStateFlow<Boolean>(false)
    val logger = Logger.getLogger(this.javaClass.name)

    fun toggleNats(state: Boolean) {
        if (natsEnabled.value == state) {
            return
        }
        if (state && !this::natsConn.isInitialized) {
            connect()
        }
        natsEnabled.value = state
    }

    fun connect() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val o: Options = Options.Builder().server("nats://144.172.83.54:4222").token("stantononlyfools")
                        .maxReconnects(-1).build()
                    natsConn = Nats.connect(o)
                    logger.info("Connected to Nats")

                    val commandDispatcher = natsConn.createDispatcher {
                        if (natsEnabled.value) {
                            val msg = String(it.data, StandardCharsets.UTF_8)
                            logger.info("Command received: " + msg)
                        }
                    }

                    commandDispatcher.subscribe("lucian.command")
                    logger.info("Created subscribers")

                } catch (e: Exception) {
                    Logger.getLogger(this.javaClass.name).warning("Failed to connect to NATS: " + e.message)
                }
            }
        }
    }
}