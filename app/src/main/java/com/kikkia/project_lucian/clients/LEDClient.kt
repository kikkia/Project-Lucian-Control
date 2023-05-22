package com.kikkia.project_lucian.clients

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kikkia.project_lucian.enums.LEDController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.lang.Exception

sealed class RequestResult {
    data class Success(val data: String) : RequestResult()
    data class Error(val error: Throwable) : RequestResult()
}

class LEDClient : ViewModel() {
    // Create a state for the response message
    private val _requestResult = MutableStateFlow<RequestResult?>(null)
    val requestResult: StateFlow<RequestResult?> = _requestResult
    val httpClient = OkHttpClient()

    fun getState(controller: LEDController) {
        viewModelScope.launch {
            val request: Request = Request.Builder()
                .url(controller.getJsonApiPath())
                .build()

            var respJson : JSONObject
            val response = withContext(Dispatchers.IO) {
                try {
                    httpClient.newCall(request).execute().use { response -> respJson = JSONObject(response.body?.string()) }
                    _requestResult.value = RequestResult.Success(respJson.toString())
                } catch(e: Exception) {
                    _requestResult.value = RequestResult.Error(e)
                }
            }
        }
    }
}