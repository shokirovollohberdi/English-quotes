package uz.shokirov.englishquotes

import android.animation.Animator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import com.android.volley.*
import com.android.volley.Response.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import uz.shokirov.adaoter.CLick
import uz.shokirov.adaoter.RvAuthor
import uz.shokirov.englishquotes.databinding.ActivityAuthorBinding
import uz.shokirov.model.Quote
import uz.shokirov.utils.QuotesObject
import uz.shokirov.utils.QuotesObject.quoteList

class AuthorActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthorBinding
    lateinit var requestQueue: RequestQueue
    lateinit var authorsList: ArrayList<String>
    var url = "https://type.fit/api/quotes"
    lateinit var adaptetr: RvAuthor
    private val TAG = "AuthorActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        loading()

    }


    private fun loading(): ArrayList<String> {
        authorsList = ArrayList()
        requestQueue = Volley.newRequestQueue(this)
        VolleyLog.DEBUG = true //qanday ma'lumot kela
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            object : Listener<JSONArray> {
                override fun onResponse(response: JSONArray?) {

                    val type = object : TypeToken<List<Quote>>() {}.type
                    val list = Gson().fromJson<List<Quote>>(response.toString(), type)
                   /* quoteList = ArrayList()
                    for (i in list.indices) {
                        quoteList.add(list[i])
                    }*/

                    var hashMap = HashSet<String>()

                    for (i in list.indices) {
                        hashMap.add(list[i].author)
                    }
                    Log.d(TAG, "onResponse: $hashMap")

                    hashMap.forEach {
                        if (it != null) {
                            authorsList.add(it)
                        }
                    }
                    authorsList.sort()

                    adaptetr = RvAuthor(authorsList, object : CLick {
                        override fun author(author: String) {
                            var myIntent =
                                Intent(this@AuthorActivity, QuotesActivity::class.java)
                            QuotesObject.currentAuthor = author
                            startActivity(myIntent)
                        }
                    })
                    binding.rv.setAdapter(adaptetr)
                    binding.animation.addAnimatorListener(object :Animator.AnimatorListener{
                        override fun onAnimationStart(p0: Animator?) {

                        }

                        override fun onAnimationEnd(p0: Animator?) {
                            binding.animation.visibility = View.GONE
                        }

                        override fun onAnimationCancel(p0: Animator?) {

                        }

                        override fun onAnimationRepeat(p0: Animator?) {

                        }
                    })
                    binding.animation.visibility = View.GONE

                    Log.d(TAG, "authorsList: $authorsList")

                }
            }, object : ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {

                }
            })

        jsonArrayRequest.tag = "tag1" //tag berilyapti
        requestQueue.add(jsonArrayRequest)

        return authorsList
    }

}