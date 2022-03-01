package uz.shokirov.englishquotes

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.ClipboardManager
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.shokirov.adaoter.OnClick
import uz.shokirov.adaoter.RvAdaptetr
import uz.shokirov.cash.MySharedPreferences
import uz.shokirov.englishquotes.databinding.ActivityQuotesBinding
import uz.shokirov.englishquotes.databinding.SheetItemBinding
import uz.shokirov.model.Quote
import uz.shokirov.utils.QuotesObject

class QuotesActivity : AppCompatActivity() {
    lateinit var adaptetr: RvAdaptetr
    lateinit var binding: ActivityQuotesBinding
    private val TAG = "QuotesActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        Toast.makeText(this, QuotesObject.currentAuthor, Toast.LENGTH_SHORT).show()
        var list = QuotesObject.quoteList
        var resultList:ArrayList<Quote> = ArrayList()
        Log.d(TAG, "onCreate: $list")
        for (i in list.indices) {
            if (list[i].author==QuotesObject.currentAuthor){
                resultList.add(list[i])
            }
        }

        adaptetr = RvAdaptetr(resultList,object :OnClick{
            override fun click(quote: Quote, position: Int) {
                var bottomSheetDialog =
                    BottomSheetDialog(this@QuotesActivity, R.style.SheetDialog)
                var item = SheetItemBinding.inflate(layoutInflater)
                bottomSheetDialog.setContentView(item.root)
                bottomSheetDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                item.itemAuthor.text = quote.author
                item.itemText.text = quote.text
                MySharedPreferences.init(this@QuotesActivity)
                var list = MySharedPreferences.obektString
                var isHave = false
                item.cardLike.setOnClickListener {
                    for (i in list.indices) {
                        var exam = list[i]
                        if (exam == quote) {
                            isHave = true
                            break
                        }
                    }
                    if (!isHave) {
                        list.add(quote)
                        bottomSheetDialog.cancel()
                        Toast.makeText(this@QuotesActivity, "Added", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            this@QuotesActivity,
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
                    bottomSheetDialog.cancel()
                }
                item.cardCopy.setOnClickListener {
                    copyText(quote.text, quote.author)
                    bottomSheetDialog.cancel()
                }

                bottomSheetDialog.show()
            }

        })
        binding.rv.adapter = adaptetr

    }
    @SuppressLint("UseRequireInsteadOfGet", "ObsoleteSdkInt")
    private fun copyText(text: String, author: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            val clipboard =
                this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.text = text + " ($author)"
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
        } else {
            val clipboard =
                this.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", text + " ($author)")
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
        }
    }

}