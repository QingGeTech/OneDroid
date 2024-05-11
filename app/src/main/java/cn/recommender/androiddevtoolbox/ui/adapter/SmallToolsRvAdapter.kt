//package cn.recommender.androiddevtoolbox.ui.adapter
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.annotation.DrawableRes
//import androidx.recyclerview.widget.RecyclerView
//import cn.recommender.androiddevtoolbox.data.entity.AppData
//import cn.recommender.androiddevtoolbox.databinding.ItemSmallToolsBinding
//
//class SmallToolsRvAdapter(private var items: List<Item>) :
//    RecyclerView.Adapter<SmallToolsRvAdapter.VH>() {
//
//    data class Item(
//        @DrawableRes var imgResId: Int,
//        var title: String
//    )
//
//    class VH(val binding: ItemSmallToolsBinding) :
//        RecyclerView.ViewHolder(binding.root)
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
//        return VH(ItemSmallToolsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
//    }
//
//    override fun getItemCount(): Int {
//        return items.size
//    }
//
//    override fun onBindViewHolder(holder: VH, position: Int) {
//        holder.binding.apply {
//            img.setImageResource(items[position].imgResId)
//            tv.text = items[position].title
//        }
//    }
//
//}