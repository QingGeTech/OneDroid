//package cn.recommender.androiddevtoolbox.ui.adapter
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import cn.recommender.androiddevtoolbox.data.entity.AppData
//import cn.recommender.androiddevtoolbox.databinding.ItemAppListBinding
//
//class AppListRvAdapter(var appData: List<AppData>) :
//    RecyclerView.Adapter<AppListRvAdapter.VH>() {
//
//    class VH(val binding: ItemAppListBinding) : RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(
//        ItemAppListBinding.inflate(
//            LayoutInflater.from(parent.context), parent, false
//        )
//    )
//
//    override fun getItemCount(): Int = appData.size
//
//    override fun onBindViewHolder(holder: VH, position: Int) {
//        holder.binding.apply {
//            ivLogo.setImageDrawable(appData[position].icon)
//            tvAppName.text = appData[position].appName
////            tvVersion.text = "${appData[position].versionName}(${appData[position].versionCode})"
//            tvPkgName.text = appData[position].pkgName
//        }
//    }
//
//}