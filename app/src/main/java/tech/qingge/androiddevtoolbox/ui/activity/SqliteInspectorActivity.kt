package tech.qingge.androiddevtoolbox.ui.activity

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import com.topjohnwu.superuser.nio.ExtendedFile
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.androiddevtoolbox.R
import tech.qingge.androiddevtoolbox.base.BaseActivity
import tech.qingge.androiddevtoolbox.base.SimpleRvAdapter
import tech.qingge.androiddevtoolbox.databinding.ActivitySqliteInspectorBinding
import tech.qingge.androiddevtoolbox.databinding.ItemDbTableBinding
import tech.qingge.androiddevtoolbox.ui.dialog.Dialogs
import tech.qingge.androiddevtoolbox.util.LogUtil
import tech.qingge.androiddevtoolbox.util.RootUtil
import java.io.File


@AndroidEntryPoint
class SqliteInspectorActivity : BaseActivity<ActivitySqliteInspectorBinding>() {

    private lateinit var tempFilePath: String
    private lateinit var originalFilePath: String

    private lateinit var adapter: SimpleRvAdapter<String, ItemDbTableBinding>

    private val tableNames: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initData()

        initViews()

    }


    private fun initData() {
        originalFilePath = intent.getStringExtra("filePath")!!
        RootUtil.getRemoteFs(this) { remoteFS ->
            val extendedFile = remoteFS.getFile(originalFilePath)
            val tempFile = getDbFileFromCache(extendedFile)
            this.tempFilePath = tempFile.absolutePath
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
            SQLiteDatabase.openDatabase(tempFilePath, null, SQLiteDatabase.OPEN_READWRITE)
                .use { db ->
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
                intent.putExtra("tempFilePath", tempFilePath)
                intent.putExtra("originalFilePath", originalFilePath)
                intent.putExtra("tableName", item)
                startActivity(intent)
            }
        }
        binding.rv.adapter = adapter

    }


    private fun initToolbar() {
        binding.toolbar.title = tempFilePath.substring(tempFilePath.lastIndexOf("/") + 1)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.setOnMenuItemClickListener {
            Dialogs.showInputDialog(
                this,
                getString(R.string.execute_sql),
                getString(R.string.input_sql),
                getString(R.string.execute)
            ) { sql ->
                SQLiteDatabase.openDatabase(tempFilePath, null, SQLiteDatabase.OPEN_READWRITE)
                    .use { db ->
                        try {
                            db.execSQL(sql)
                            Dialogs.showMessageTips(this, getString(R.string.execute_success))

                            RootUtil.getRemoteFs(this) { remoteFS ->
                                val extendedFile = remoteFS.getFile(originalFilePath)
                                val tempFile = File(tempFilePath)
                                tempFile.inputStream().copyTo(extendedFile.newOutputStream())
                            }

                            tableNames.clear()
                            initRv()

                        } catch (e: Exception) {
                            LogUtil.e("execute fail", e)
                            Dialogs.showMessageTips(
                                this,
                                getString(R.string.execute_fail) + "\n" + e.message
                            )
                        }
                    }
            }
            true
        }
    }


}