package cn.recommender.androiddevtoolbox.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.recommender.androiddevtoolbox.util.LogUtil

open class BaseFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.d("${javaClass.name} onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LogUtil.d("${javaClass.name} onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroy() {
        LogUtil.d("${javaClass.name} onDestroy")
        super.onDestroy()
    }

    override fun onDestroyView() {
        LogUtil.d("${javaClass.name} onDestroyView")
        super.onDestroyView()
    }


}