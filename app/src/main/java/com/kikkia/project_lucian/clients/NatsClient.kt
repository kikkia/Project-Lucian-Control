package com.kikkia.project_lucian.clients

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kikkia.project_lucian.models.APIEvent
import com.kikkia.project_lucian.models.NATSCommand
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
    private val statusMessage = MutableLiveData<APIEvent<String>>()
    val message: LiveData<APIEvent<String>>
        get() = statusMessage
    private val natsCommand = MutableLiveData<APIEvent<NATSCommand>>()
    val command: LiveData<APIEvent<NATSCommand>>
        get() = natsCommand

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
                    statusMessage.postValue(APIEvent("Connected to NATS"))

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