package cn.recommender.androiddevtoolbox.ui.activity

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseActivity
import cn.recommender.androiddevtoolbox.base.SimpleRvAdapter
import cn.recommender.androiddevtoolbox.databinding.ActivitySqliteInspectorBinding
import cn.recommender.androiddevtoolbox.databinding.ItemDbTableBinding
import cn.recommender.androiddevtoolbox.ui.dialog.Dialogs
import cn.recommender.androiddevtoolbox.util.RootUtil
import com.topjohnwu.superuser.nio.ExtendedFile
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class SqliteInspectorActivity : BaseActivity<ActivitySqliteInspectorBinding>() {

    private lateinit var filePath: String

    private lateinit var adapter: SimpleRvAdapter<String, ItemDbTableBinding>

    private val tableNames: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initData()

        initViews()

    }


    private fun initData() {
        val filePath = intent.getStringExtra("filePath")!!
        RootUtil.getRemoteFs(this) { remoteFS ->
            val extendedFile = remoteFS.getFile(filePath)
            val tempFile = getDbFileFromCache(extendedFile)
            this.filePath = tempFile.absolutePath
        }

    }

    private fun getDbFileFromCache(extendedFile: ExtendedFile): File {
        val tempFile =
            File(cacheDir.absolutePath + "/" + extendedFile.name)
        tempFile.delete()
        tempFile.createNewFile()
        extendedFile.newInputStream().copyTo(tempFile.outputStream())
        return tempFile
    }


    private fun initViews() {

        initToolbar()
        initRv()

    }

    private fun initRv() {
        try {
            SQLiteDatabase.openDatabase(filePath, null, SQLiteDatabase.OPEN_READWRITE).use { db ->
                db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
                    .use { cursor ->
                        if (cursor.moveToFirst()) {
                            do {
                                tableNames.add(cursor.getString(0))
                            } while (cursor.moveToNext())
                        }
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Dialogs.showMessageTips(
                this,
                getString(R.string.open_database_fail),
                false,
            ) { _, _ -> finish() }
        }

        adapter = SimpleRvAdapter(tableNames, ItemDbTableBinding::inflate) { itemBinding, item, _ ->
            itemBinding.tvDbName.text = item
            itemBinding.root.setOnClickListener {
                val intent = Intent(this, SqliteTableDetailActivity::class.java)
                intent.putExtra("filePath", filePath)
                intent.putExtra("tableName", item)
                startActivity(intent)
            }
        }
        binding.rv.adapter = adapter

    }


    private fun initToolbar() {
        binding.toolbar.title = filePath.substring(filePath.lastIndexOf("/") + 1)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }


}