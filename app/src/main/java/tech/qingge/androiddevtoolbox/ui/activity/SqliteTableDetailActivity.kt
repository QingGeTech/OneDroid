package tech.qingge.androiddevtoolbox.ui.activity

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.androiddevtoolbox.R
import tech.qingge.androiddevtoolbox.base.BaseActivity
import tech.qingge.androiddevtoolbox.base.SimpleRvAdapter
import tech.qingge.androiddevtoolbox.databinding.ActivitySqliteTableDetailBinding
import tech.qingge.androiddevtoolbox.databinding.ItemDbCellBinding
import tech.qingge.androiddevtoolbox.util.ClipboardUtil


//TODO：添加更多数据库操作
@AndroidEntryPoint
class SqliteTableDetailActivity : BaseActivity<ActivitySqliteTableDetailBinding>() {

    private lateinit var filePath: String
    private lateinit var tableName: String

    //    private lateinit var adapter: TableDetailRvAdapter
    private lateinit var adapter: SimpleRvAdapter<String, ItemDbCellBinding>

    //
//    private val tableNames: MutableList<String> = mutableListOf()
//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initData()

        initViews()

    }


    private fun initData() {
        filePath = intent.getStringExtra("filePath")!!
        tableName = intent.getStringExtra("tableName")!!
    }

    private fun initViews() {

        initToolbar()
        initRv()

    }

    @SuppressLint("Range")
    private fun initRv() {
        val columnNames: MutableList<String> = mutableListOf()
        val rows: MutableList<MutableList<String>> = mutableListOf()
        SQLiteDatabase.openDatabase(filePath, null, SQLiteDatabase.OPEN_READWRITE).use { db ->
            db.rawQuery("PRAGMA table_info($tableName)", null).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val name = cursor.getString(cursor.getColumnIndex("name"))
                        columnNames.add(name)
                    } while (cursor.moveToNext())
                }
            }

            db.rawQuery("select * from $tableName", null).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val row: MutableList<String> = mutableListOf()
                        for (i in 0 until columnNames.size) {
                            val value: Any? = when (cursor.getType(i)) {
                                Cursor.FIELD_TYPE_NULL -> null
                                Cursor.FIELD_TYPE_INTEGER -> cursor.getInt(i)
                                Cursor.FIELD_TYPE_FLOAT -> cursor.getFloat(i)
                                Cursor.FIELD_TYPE_STRING -> cursor.getString(i)
                                Cursor.FIELD_TYPE_BLOB -> cursor.getBlob(i)
                                else -> null
                            }
                            row.add(value.toString())
                        }
                        rows.add(row)
                    } while (cursor.moveToNext())
                }
            }

        }

        val layoutManager =
            GridLayoutManager(this, columnNames.size, RecyclerView.VERTICAL, false)
        binding.rv.layoutManager = layoutManager

        val allData: MutableList<String> = mutableListOf()
        allData.addAll(columnNames)
        for (row in rows) {
            for (i in 0 until columnNames.size) {
                allData.add(row[i])
            }
        }
        adapter = SimpleRvAdapter(allData, ItemDbCellBinding::inflate) { itemBinding, item, index ->
            if (index < columnNames.size) {
                TextViewCompat.setTextAppearance(
                    itemBinding.tv,
                    R.style.TextAppearance_Material3_TitleLarge
                )
            } else {
                TextViewCompat.setTextAppearance(
                    itemBinding.tv,
                    R.style.TextAppearance_Material3_BodySmall
                )
            }
            itemBinding.tv.text = item
            itemBinding.root.setOnLongClickListener {
                val popupMenu = PopupMenu(this, itemBinding.root)
                popupMenu.inflate(R.menu.popup_menu_db_cell)
                popupMenu.setOnMenuItemClickListener {
                    ClipboardUtil.copyToClipboard(
                        this,
                        itemBinding.tv.text.toString()
                    )
                    true
                }
                popupMenu.show()
                return@setOnLongClickListener true
            }
        }
        val divider1 = DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
            setDrawable(ColorDrawable(Color.GRAY))
        }
        val divider2 = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL).apply {
            setDrawable(ColorDrawable(Color.GRAY))
        }
        binding.rv.addItemDecoration(divider1)
        binding.rv.addItemDecoration(divider2)

        binding.rv.adapter = adapter


    }


    private fun initToolbar() {
//        binding.toolbar.title = filePath.substring(filePath.lastIndexOf("/") + 1)
        binding.toolbar.title = tableName
        binding.toolbar.setNavigationOnClickListener { finish() }
    }


}