package tech.qingge.onedroid.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import tech.qingge.onedroid.R
import com.google.android.material.color.MaterialColors
import tech.qingge.onedroid.databinding.ItemColorPaletteBinding
import tech.qingge.onedroid.databinding.ItemPureColorBinding
import java.lang.IllegalStateException

class ChooseColorRvAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface Callback {
        fun onChooseColor(color: Int)
        fun onOpenPalette()
    }

    var callback: Callback? = null

    var paletteAsColor: Int = -1

    companion object {
        const val ITEM_SIZE = 8
        const val ITEM_TYPE_COLOR_PALETTE = 1
        const val ITEM_TYPE_PURE_COLOR = 2
        val COLOR_IDS = listOf(
            R.color.color_primary_1,
            R.color.color_primary_2,
            R.color.color_primary_3,
            R.color.color_primary_4,
            R.color.color_primary_5,
            R.color.color_primary_6,
            R.color.color_primary_7
        )
    }


    class ViewHolderColorPalette(val binding: ItemColorPaletteBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ViewHolderPureColor(val binding: ItemPureColorBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            ITEM_TYPE_COLOR_PALETTE
        } else {
            ITEM_TYPE_PURE_COLOR
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ITEM_TYPE_COLOR_PALETTE ->
                return ViewHolderColorPalette(
                    ItemColorPaletteBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )

            ITEM_TYPE_PURE_COLOR ->
                return ViewHolderPureColor(
                    ItemPureColorBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
        }
        throw IllegalStateException()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) {
            holder as ViewHolderColorPalette
            holder.binding.root.setOnClickListener {
                if (paletteAsColor != -1) {
                    callback?.onChooseColor(paletteAsColor)
                } else {
                    callback?.onOpenPalette()
                }
            }
            holder.binding.ccpv.visibility = if (paletteAsColor == -1) View.VISIBLE else View.GONE
            holder.binding.ccv.visibility = if (paletteAsColor == -1) View.GONE else View.VISIBLE
            if (paletteAsColor != -1) {
                holder.binding.ccv.setColor(paletteAsColor)
            }
        } else {
            holder as ViewHolderPureColor
            val color = ResourcesCompat.getColor(
                holder.binding.root.context.resources,
                COLOR_IDS[position - 1], holder.binding.root.context.theme
            )
            val harmonizedColor =
                MaterialColors.harmonizeWithPrimary(holder.binding.root.context, color)
            holder.binding.ccv.setColor(harmonizedColor)
            holder.binding.root.setOnClickListener {
                callback?.onChooseColor(color)
            }
        }
    }

    override fun getItemCount(): Int = ITEM_SIZE

}