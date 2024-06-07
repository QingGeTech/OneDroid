package cn.recommender.androiddevtoolbox.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseActivity
import cn.recommender.androiddevtoolbox.databinding.ActivityTextRecognitionBinding
import com.bumptech.glide.Glide
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions

class TextRecognitionActivity : BaseActivity<ActivityTextRecognitionBinding>() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews(intent)

    }

    @SuppressLint("SetTextI18n")
    private fun initViews(intent: Intent) {
        val filePath = intent.getStringExtra("filePath")
        val bitmap = BitmapFactory.decodeFile(filePath!!)

        binding.toolbar.setNavigationOnClickListener { finish() }
        Glide.with(this).load(bitmap).into(binding.img)

        val recognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
        val image = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(image)
            .addOnSuccessListener {
                binding.tv.text = it.text
            }.addOnFailureListener {
                binding.tv.text = "Fail:" + it.message
            }

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        initViews(intent)
    }

}