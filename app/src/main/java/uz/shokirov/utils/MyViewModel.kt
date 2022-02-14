package uz.shokirov.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import uz.shokirov.model.Quote

class MyViewModel(var context: Context) : ViewModel() {
    private val TAG = "MyViewModel"
    lateinit var requestQueue: RequestQueue
    var url = "https://type.fit/api/quotes"
    private var liveData = MutableLiveData<List<Quote>>()

    fun getQuotes(): LiveData<List<Quote>> {
        requestQueue = Volley.newRequestQueue(context)
        VolleyLog.DEBUG = true //qanday ma'lumot kela
        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            object : Response.Listener<JSONArray> {
                override fun onResponse(response: JSONArray?) {

                    val type = object : TypeToken<List<Quote>>() {}.type
                    val list = Gson().fromJson<List<Quote>>(response.toString(), type)

                    liveData.value = list

                    Log.d(TAG, "onResponse : ${response.toString()}")
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {

                }
            })

        jsonArrayRequest.tag = "tag1" //tag berilyapti
        requestQueue.add(jsonArrayRequest)

        return liveData
    }
}