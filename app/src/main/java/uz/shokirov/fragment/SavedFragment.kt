package uz.shokirov.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.ClipboardManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.shokirov.adaoter.OnClick
import uz.shokirov.adaoter.RvAdaptetr
import uz.shokirov.cash.MySharedPreferences
import uz.shokirov.englishquotes.R
import uz.shokirov.englishquotes.databinding.FragmentSavedBinding
import uz.shokirov.englishquotes.databinding.SheetItemBinding
import uz.shokirov.model.Quote


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SavedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SavedFragment : Fragment() {
    lateinit var binding: FragmentSavedBinding

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
        binding = FragmentSavedBinding.inflate(layoutInflater)

        onResume()

        return binding.root
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onResume() {
        super.onResume()
        MySharedPreferences.init(context!!)
        var list = MySharedPreferences.obektString

        binding.rv.adapter = RvAdaptetr(list, object : OnClick {
            @SuppressLint("ResourceType")
            override fun click(quote: Quote, position: Int) {
                var bottomSheetDialog =
                    BottomSheetDialog(context!!, R.style.SheetDialog)
                var item = SheetItemBinding.inflate(layoutInflater)
                bottomSheetDialog.setContentView(item.root)
                item.likeImage.setImageResource(R.drawable.ic_delete)
                item.removeLayout.setBackgroundResource(R.color.red)
                bottomSheetDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                item.itemAuthor.text = quote.author
                item.itemText.text = quote.text
                MySharedPreferences.init(context!!)
                var list = MySharedPreferences.obektString
                item.cardLike.setOnClickListener {
                    list.remove(quote)
                    bottomSheetDialog.cancel()
                    MySharedPreferences.obektString = list
                    onResume()
                }
                item.cardCopy.setOnClickListener {
                    copyText(quote.text, quote.author)
                }
                item.cardShare.setOnClickListener {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "${quote.text} (${quote.author})")
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }


                bottomSheetDialog.show()
            }
        })

    }

    @SuppressLint("UseRequireInsteadOfGet", "ObsoleteSdkInt")
    private fun copyText(text: String, author: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            val clipboard =
                context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.text = text+" ($author)"
        } else {
            val clipboard =
                context!!.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", text+" ($author)")
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
         * @return A new instance of fragment SavedFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SavedFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}