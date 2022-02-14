package uz.shokirov.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.ClipboardManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import uz.shokirov.adaoter.OnClick
import uz.shokirov.adaoter.RvAdaptetr
import uz.shokirov.cash.MySharedPreferences
import uz.shokirov.englishquotes.MainActivity
import uz.shokirov.englishquotes.R
import uz.shokirov.englishquotes.databinding.FragmentAllQuotesBinding
import uz.shokirov.englishquotes.databinding.SheetItemBinding
import uz.shokirov.model.Quote
import uz.shokirov.utils.MyViewModel
import uz.shokirov.utils.NetworkHelper

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AllQuotesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllQuotesFragment : Fragment() {
    private var myViewModel: MyViewModel? = null
    lateinit var binding: FragmentAllQuotesBinding
    lateinit var requestQueue: RequestQueue
    var url = "https://type.fit/api/quotes"
    private val TAG = "AllQuotesFragment"
    lateinit var adapter: RvAdaptetr

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllQuotesBinding.inflate(layoutInflater)

        setNetwork()
        return binding.root
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun setNetwork() {
        var networkHelper = NetworkHelper(context!!)

        if (networkHelper.isNetworkConnected()) {
            loading()
            (activity as MainActivity).binding.innerLayout.visibility = View.VISIBLE
            (activity as MainActivity).binding.tabLayout.visibility = View.VISIBLE
            (activity as MainActivity).binding.loadingAnim.visibility = View.VISIBLE
            (activity as MainActivity).binding.connectLayout.visibility = View.GONE
        } else {
            (activity as MainActivity).binding.innerLayout.visibility = View.INVISIBLE
            (activity as MainActivity).binding.tabLayout.visibility = View.INVISIBLE
            (activity as MainActivity).binding.loadingAnim.visibility = View.INVISIBLE
            (activity as MainActivity).binding.connectLayout.visibility = View.VISIBLE
            (activity as MainActivity).binding.updateButton.setOnClickListener {
                setNetwork()
            }
        }
    }


    private fun loading() {
        requestQueue = Volley.newRequestQueue(context)
        VolleyLog.DEBUG = true //qanday ma'lumot kela
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            object : Response.Listener<JSONArray> {
                override fun onResponse(response: JSONArray?) {

                    val type = object : TypeToken<List<Quote>>() {}.type
                    val list = Gson().fromJson<List<Quote>>(response.toString(), type)

                    var listSofted = ArrayList<Quote>()
                    for (i in list.indices) {
                        if (list[i].author != null) {
                            listSofted.add(list[i])
                        }
                    }

                    (activity as MainActivity).binding.loadingAnim.visibility = View.INVISIBLE
                    binding.rv.adapter = RvAdaptetr(listSofted, object : OnClick {
                        override fun click(quote: Quote, position: Int) {
                            var bottomSheetDialog =
                                BottomSheetDialog(context!!, R.style.SheetDialog)
                            var item = SheetItemBinding.inflate(layoutInflater)
                            bottomSheetDialog.setContentView(item.root)
                            bottomSheetDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                            item.itemAuthor.text = quote.author
                            item.itemText.text = quote.text
                            MySharedPreferences.init(context!!)
                            var list = MySharedPreferences.obektString
                            var isHave = false
                            for (i in 0 until list.size) {
                                if (quote != list[i]) {
                                    isHave = false
                                } else {
                                    isHave = true
                                }
                            }
                            item.cardLike.setOnClickListener {
                                if (!isHave) {
                                    bottomSheetDialog.cancel()
                                    list.add(quote)
                                    Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show()
                                } else if (isHave) {
                                    Toast.makeText(
                                        context,
                                        "This has already been added",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                MySharedPreferences.obektString = list

                            }
                            item.cardShare.setOnClickListener {
                                val sendIntent = Intent()
                                sendIntent.action = Intent.ACTION_SEND
                                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
                                sendIntent.type = "text/plain"

                                val shareIntent = Intent.createChooser(sendIntent, null)
                                startActivity(shareIntent)
                            }
                            item.cardCopy.setOnClickListener {
                                copyText(quote.text, quote.author)
                            }

                            bottomSheetDialog.show()
                        }
                    })

                    Log.d(TAG, "onResponse : ${response.toString()}")
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {

                }
            })

        jsonArrayRequest.tag = "tag1" //tag berilyapti
        requestQueue.add(jsonArrayRequest)

    }

    @SuppressLint("UseRequireInsteadOfGet", "ObsoleteSdkInt")
    private fun copyText(text: String, author: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            val clipboard =
                context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.text = text + " ($author)"
        } else {
            val clipboard =
                context!!.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", text + " ($author)")
            clipboard.setPrimaryClip(clip)
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AllQuotesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AllQuotesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}