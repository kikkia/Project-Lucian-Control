package com.kikkia.project_lucian.clients

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kikkia.project_lucian.enums.AnimationStates
import com.kikkia.project_lucian.enums.LEDController
import com.kikkia.project_lucian.models.LEDState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.lang.Exception
import java.util.logging.Logger

sealed class GetRequestResult {
    data class Success(val data: LEDState) : GetRequestResult()
    data class Error(val error: Throwable) : GetRequestResult()
}

sealed class GetAllRequestResult {
    data class Success(val data: MutableMap<LEDController, GetRequestResult>) : GetAllRequestResult()
    data class Error(val error: Throwable) : GetAllRequestResult()
}

class LEDClient : ViewModel() {
    // Create a state for the response message
    private val _getAllRequestResult = MutableStateFlow<GetAllRequestResult?>(null)
    val getAllRequestResult: StateFlow<GetAllRequestResult?> = _getAllRequestResult

    private val _getLaserRequestResult = MutableStateFlow<GetRequestResult?>(null)
    val getLaserRequestResult: StateFlow<GetRequestResult?> = _getLaserRequestResult
    private val _getRevolverRequestResult = MutableStateFlow<GetRequestResult?>(null)
    val getRevolverRequestResult: StateFlow<GetRequestResult?> = _getRevolverRequestResult
    private val _getHelmetRequestResult = MutableStateFlow<GetRequestResult?>(null)
    val getHelmetRequestResult: StateFlow<GetRequestResult?> = _getHelmetRequestResult

    private val _statusMessage = MutableStateFlow("")
    private val _getAllRequestResultTest = mutableStateMapOf<LEDController, GetRequestResult>()
    val testState: SnapshotStateMap<LEDController, GetRequestResult> get() = _getAllRequestResultTest
    val statusMessage = _statusMessage
    val httpClient = OkHttpClient()

    /**
     * Get the LED State for a given controller
     */
    fun getState(controller: LEDController): LEDState {
        val request: Request = Request.Builder()
            .url(controller.getJsonApiPath())
            .build()

        var respJson : JSONObject
        httpClient.newCall(request).execute().use { response -> respJson = JSONObject(response.body?.string()) }
        return LEDState.fromJSON(controller, respJson)
    }

    /**
     * Changes the LED State between on and off, but keeps the controller active
     */
    fun toggleLEDsOn(controller: LEDController, on: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val json = JSONObject()
                    json.put("on", on)
                    val request: Request = Request.Builder()
                        .url(controller.getJsonApiPath())
                        .post(json.toString().toRequestBody())
                        .build()

                    httpClient.newCall(request).execute()
                } catch (e: Exception) {
                    _statusMessage.value = e.message!!
                    Log.e("toggleLEDs", e.message!!)
                }
                getAllStates()
            }
        }
    }

    /**
     * Set a given brightness on an LED controller
     */
    fun setLEDBrightness(controller: LEDController, bri: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val json = JSONObject()
                    json.put("bri", bri)
                    val request: Request = Request.Builder()
                        .url(controller.getJsonApiPath())
                        .post(json.toString().toRequestBody())
                        .build()

                    httpClient.newCall(request).execute()
                } catch (e: Exception) {
                    _statusMessage.value = e.message!!
                    Log.e("setLEDBrightness", e.message!!)
                }
                getAllStates()
            }
        }
    }

    /**
     * Set an active LED animation on a given LED controller
     */
    fun setPlaylist(controller: LEDController, playlist: AnimationStates) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val json = JSONObject()
                    json.put("ps", playlist.playlistId)
                    val request: Request = Request.Builder()
                        .url(controller.getJsonApiPath())
                        .post(json.toString().toRequestBody())
                        .build()

                    val response = httpClient.newCall(request).execute()
                    Logger.getLogger("Test").warning(response.body.toString())
                } catch (e: Exception) {
                    _statusMessage.value = e.message!!
                    Log.e("setPlaylist", e.message!!)
                }
                getAllStates()
            }
        }
    }

    /**
     * Immediately restarts the given LED Controller
     */
    fun restartController(controller: LEDController) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val json = JSONObject()
                    json.put("rb", true)
                    val request: Request = Request.Builder()
                        .url(controller.getJsonApiPath())
                        .post(json.toString().toRequestBody())
                        .build()

                    httpClient.newCall(request).execute()
                } catch (e: Exception) {
                    _statusMessage.value = e.message!!
                    Log.e("restartController", e.message!!)
                }
                getAllStates()
            }
        }
    }

    /**
     * Get all states and pass the values through to the UI
     */
    fun getAllStates() {
        viewModelScope.launch {
            val map: MutableMap<LEDController, GetRequestResult> = mutableStateMapOf()
            withContext(Dispatchers.IO) {
                for (controller in LEDController.values()) {
                    try {
                        val state = getState(controller)
                        map[controller] = GetRequestResult.Success(state)
                    } catch (e: Exception) {
                        map[controller] = GetRequestResult.Error(e)
                    }
                }
                _getHelmetRequestResult.value = map[LEDController.HELMET]
                _getLaserRequestResult.value = map[LEDController.LASER]
                _getRevolverRequestResult.value = map[LEDController.REVOLVER]
            }
        }
    }
}