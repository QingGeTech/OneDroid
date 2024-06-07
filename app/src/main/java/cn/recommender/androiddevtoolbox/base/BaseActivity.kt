package cn.recommender.androiddevtoolbox.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

open class BaseActivity<B> : AppCompatActivity() where B : ViewBinding {
    lateinit var binding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initViewBinding()
        setContentView(binding.root)
        updatePadding()
    }

    private fun updatePadding() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.updatePadding(
                v.paddingLeft, statusBarInsets.top, v.paddingRight, navigationBarInsets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun initViewBinding() {
        val bClass =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<B>
        val inflateMethod = bClass.getMethod("inflate", LayoutInflater::class.java)
        binding = inflateMethod.invoke(null, LayoutInflater.from(this)) as B
    }

}