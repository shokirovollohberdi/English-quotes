package uz.shokirov.adaoter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.shokirov.englishquotes.databinding.ItemAuthorBinding

class RvAuthor(var list: ArrayList<String>, var onClick: CLick) : RecyclerView.Adapter<RvAuthor.Vh>() {
    inner class Vh(var itemRv: ItemAuthorBinding) : RecyclerView.ViewHolder(itemRv.root) {
        fun onBind(position: Int) {
            itemRv.tvAuthor.text = list[position]
            itemRv.root.setOnClickListener {
                onClick.author(list[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemAuthorBinding.inflate(LayoutInflater.from(parent?.context), parent, false))
    }


    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = list.size
}

interface CLick {
    fun author(author: String)
}