package cn.recommender.androiddevtoolbox.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import cn.recommender.androiddevtoolbox.util.LogUtil
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<B> : Fragment() where B : ViewBinding {

    lateinit var binding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.d("${javaClass.name} onCreate")
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LogUtil.d("${javaClass.name} onCreateView")
        val bClass =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<B>
        val inflateMethod = bClass.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        binding =
            inflateMethod.invoke(null, LayoutInflater.from(requireContext()), container, false) as B
        initViews()
        return binding.root
    }

    abstract fun initViews()

    override fun onDestroy() {
        LogUtil.d("${javaClass.name} onDestroy")
        super.onDestroy()
    }

    override fun onDestroyView() {
        LogUtil.d("${javaClass.name} onDestroyView")
        super.onDestroyView()
    }


}