//package cn.recommender.androiddevtoolbox.viewmodel
//
//import android.annotation.SuppressLint
//import android.app.Application
//import androidx.annotation.DrawableRes
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import cn.recommender.androiddevtoolbox.R
//import cn.recommender.androiddevtoolbox.data.entity.AppData
//import cn.recommender.androiddevtoolbox.data.local.sp.SpApi
//import cn.recommender.androiddevtoolbox.ui.dialog.Dialogs
//import cn.recommender.androiddevtoolbox.ui.fragment.ToolsFragment
//import com.hjq.permissions.OnPermissionCallback
//import com.hjq.permissions.Permission
//import com.hjq.permissions.XXPermissions
//import dagger.hilt.android.lifecycle.HiltViewModel
//import dagger.hilt.android.qualifiers.ApplicationContext
//import javax.inject.Inject
//
//@HiltViewModel
//class ToolsFragmentViewModel @Inject constructor(
//    private val app: Application
//) : ViewModel() {
//
//    data class ToolItem(
//        @DrawableRes var imgResId: Int,
//        var title: String,
//        var enabled: Boolean
//    )
//
//    companion object {
//        // process level, sync with fab
//        private val _toolItems: MutableLiveData<List<ToolItem>> = MutableLiveData(listOf())
//        val toolItems: LiveData<List<ToolItem>> get() = _toolItems
//
//        private val _curSelectedPos: MutableLiveData<Int> = MutableLiveData(-1)
//        val curSelectedPos: LiveData<Int> get() = _curSelectedPos
//    }
//
//    fun initData() {
//        if (_toolItems.value!!.isNotEmpty()) {
//            _toolItems.value = _toolItems.value
//        } else {
//            _toolItems.value = listOf(
//                ToolItem(
//                    R.drawable.ic_scroll_screenshot,
//                    app.applicationContext.getString(R.string.scroll_screenshot),
//                    false
//                ),
//                ToolItem(
//                    R.drawable.ic_screen_record,
//                    app.applicationContext.getString(R.string.screen_record),
//                    false
//                ),
//                ToolItem(
//                    R.drawable.ic_color_pick_tool,
//                    app.applicationContext.getString(R.string.color_pick_tool),
//                    false
//                ),
//                ToolItem(
//                    R.drawable.ic_text_ocr,
//                    app.applicationContext.getString(R.string.text_ocr),
//                    false
//                )
//            )
//        }
//
//        _curSelectedPos.value = _curSelectedPos.value
//
//    }
//
//    fun handleItemClick(item: ToolItem, position: Int) {
//        if (item.enabled) {
//            _curSelectedPos.value = -1
//            item.enabled = false
//        } else {
//            _curSelectedPos.value = position
//            item.enabled = true
//            _toolItems.value!!.forEachIndexed { index, itemInList ->
//                if (index != position) itemInList.enabled = false
//            }
//        }
//        _toolItems.value = _toolItems.value
//
//    }
//
//
//    fun clearState() {
//        _curSelectedPos.value = -1
//        _toolItems.value!!.forEach { item -> item.enabled = false }
//        _toolItems.value = _toolItems.value
//    }
//
//
//}