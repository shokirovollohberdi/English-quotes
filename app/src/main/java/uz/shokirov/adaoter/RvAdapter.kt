package uz.shokirov.adaoter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.shokirov.englishquotes.R
import uz.shokirov.englishquotes.databinding.ItemRvBinding
import uz.shokirov.model.Quote

class RvAdaptetr(var list: List<Quote>, var onClick: OnClick) :
    RecyclerView.Adapter<RvAdaptetr.Vh>() {
    inner class Vh(var itemRv: ItemRvBinding) : RecyclerView.ViewHolder(itemRv.root) {
        @SuppressLint("ResourceAsColor")
        fun onBind(quote: Quote, position: Int) {
            itemRv.tvQuote.text = quote.text
            itemRv.root.setOnClickListener {
                onClick.click(list[position], position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvBinding.inflate(LayoutInflater.from(parent?.context), parent, false))
    }


    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size
}

interface OnClick {
    fun click(quote: Quote, position: Int)
}