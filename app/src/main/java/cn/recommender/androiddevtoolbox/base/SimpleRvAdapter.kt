package cn.recommender.androiddevtoolbox.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * ViewBinding+RecyclerViewAdapter封装
 */
class SimpleRvAdapter<D : Any, B : ViewBinding>(
    var items: List<D>,
    private val holderCreator: (LayoutInflater, ViewGroup, Boolean) -> B,
    private val itemBinder: (binding: B, item: D, index: Int) -> Unit
) :
    RecyclerView.Adapter<SimpleRvAdapter.VH<B>>() {

    class VH<B : ViewBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH<B> {
        return VH(holderCreator(LayoutInflater.from(parent.context), parent, false))
    }


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: VH<B>, position: Int) {
        itemBinder(holder.binding, items[position], position)
    }

}