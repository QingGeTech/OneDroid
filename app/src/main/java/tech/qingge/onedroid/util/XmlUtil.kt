package tech.qingge.onedroid.util

import java.nio.ByteBuffer
import java.nio.ByteOrder

object XmlUtil {

    // --- AXML 常量标识 ---
    private const val CHUNK_AXML_FILE = 0x00080003
    private const val CHUNK_STRING_POOL = 0x001C0001
    private const val CHUNK_RESOURCEIDS = 0x00080180
    private const val CHUNK_XML_START_NAMESPACE = 0x00100100
    private const val CHUNK_XML_END_NAMESPACE = 0x00100101
    private const val CHUNK_XML_START_TAG = 0x00100102
    private const val CHUNK_XML_END_TAG = 0x00100103
    private const val CHUNK_XML_TEXT = 0x00100104

    // --- 属性类型常量 ---
    private const val TYPE_NULL = 0x00
    private const val TYPE_REFERENCE = 0x01
    private const val TYPE_ATTRIBUTE = 0x02
    private const val TYPE_STRING = 0x03
    private const val TYPE_FLOAT = 0x04
    private const val TYPE_DIMENSION = 0x05
    private const val TYPE_FRACTION = 0x06
    private const val TYPE_INT_DEC = 0x10
    private const val TYPE_INT_HEX = 0x11
    private const val TYPE_INT_BOOLEAN = 0x12
    private const val TYPE_INT_COLOR_ARGB8 = 0x1c
    private const val TYPE_INT_COLOR_RGB8 = 0x1d
    private const val TYPE_INT_COLOR_ARGB4 = 0x1e
    private const val TYPE_INT_COLOR_RGB4 = 0x1f

    private val UNIT_STRS = arrayOf("px", "dip", "sp", "pt", "in", "mm")

    fun getXmlString(binaryXml: ByteArray): String {
        val buffer = ByteBuffer.wrap(binaryXml).order(ByteOrder.LITTLE_ENDIAN)
        val sb = StringBuilder()

        if (buffer.int != CHUNK_AXML_FILE) return ""
        buffer.int // Skip fileSize

        var stringPool = listOf<String>()
        val namespaces = mutableMapOf<String, String>() // URI -> Prefix

        while (buffer.hasRemaining()) {
            val chunkType = buffer.int
            val chunkSize = buffer.int
            val startPos = buffer.position() - 8

            when (chunkType) {
                CHUNK_STRING_POOL -> {
                    stringPool = parseStringPool(buffer, startPos)
                }
                CHUNK_XML_START_NAMESPACE -> {
                    buffer.int // line
                    buffer.int // comment
                    val prefix = stringPool[buffer.int]
                    val uri = stringPool[buffer.int]
                    namespaces[uri] = prefix
                }
                CHUNK_XML_START_TAG -> {
                    buffer.int // line
                    buffer.int // comment
                    val uriIndex = buffer.int
                    val nameIndex = buffer.int
                    buffer.int // fixed 0x140014
                    val attrCount = buffer.short.toInt() and 0xFFFF
                    buffer.short // idIndex
                    buffer.short // classIndex
                    buffer.short // styleIndex

                    val prefix = namespaces[stringPool.getOrNull(uriIndex)]
                    val tagName = stringPool[nameIndex]
                    sb.append("<").append(if (prefix != null) "$prefix:$tagName" else tagName)

                    // 如果是根节点，打印命名空间声明
                    if (namespaces.isNotEmpty() && sb.length < 30) {
                        namespaces.forEach { (uri, pref) ->
                            sb.append(" xmlns:$pref=\"$uri\"")
                        }
                    }

                    for (i in 0 until attrCount) {
                        val aUriIdx = buffer.int
                        val aNameIdx = buffer.int
                        val aValIdx = buffer.int
                        val aType = buffer.int shr 24
                        val aData = buffer.int

                        val aPrefix = namespaces[stringPool.getOrNull(aUriIdx)]
                        val aName = stringPool[aNameIdx]
                        val aValue = formatValue(aType, aData, stringPool, aValIdx)

                        sb.append("\n    ").append(if (aPrefix != null) "$aPrefix:$aName" else aName)
                            .append("=\"").append(aValue).append("\"")
                    }
                    sb.append(">\n")
                }
                CHUNK_XML_END_TAG -> {
                    buffer.int; buffer.int // line, comment
                    val uriIdx = buffer.int
                    val nameIdx = buffer.int
                    val prefix = namespaces[stringPool.getOrNull(uriIdx)]
                    sb.append("</").append(if (prefix != null) "$prefix:${stringPool[nameIdx]}" else stringPool[nameIdx]).append(">\n")
                }
                CHUNK_XML_TEXT -> {
                    buffer.int; buffer.int // line, comment
                    val nameIdx = buffer.int
                    buffer.int; buffer.int // skip
                    sb.append(stringPool[nameIdx]).append("\n")
                }
            }
            buffer.position(startPos + chunkSize)
        }
        return sb.toString()
    }

    private fun parseStringPool(buffer: ByteBuffer, startPos: Int): List<String> {
        val stringCount = buffer.int
        buffer.int // styleCount
        val flags = buffer.int
        val stringsStart = buffer.int
        val stylesStart = buffer.int
        val offsets = IntArray(stringCount) { buffer.int }
        val isUtf8 = (flags and (1 shl 8)) != 0

        return offsets.map { offset ->
            buffer.position(startPos + stringsStart + offset)
            if (isUtf8) {
                val len = buffer.get().toInt() and 0xFF
                if (len and 0x80 != 0) buffer.get() // Skip extended length
                val bytesLen = buffer.get().toInt() and 0xFF
                if (bytesLen and 0x80 != 0) buffer.get()
                val bytes = ByteArray(bytesLen)
                buffer.get(bytes)
                String(bytes, Charsets.UTF_8)
            } else {
                val len = buffer.short.toInt() and 0xFFFF
                if (len and 0x8000 != 0) buffer.short // Skip extended
                val bytes = ByteArray(len * 2)
                buffer.get(bytes)
                String(bytes, Charsets.UTF_16LE)
            }
        }
    }

    private fun formatValue(type: Int, data: Int, pool: List<String>, valIdx: Int): String {
        return when (type) {
            TYPE_STRING -> pool[valIdx]
            TYPE_REFERENCE -> "@${Integer.toHexString(data)}"
            TYPE_ATTRIBUTE -> "?${Integer.toHexString(data)}"
            TYPE_INT_BOOLEAN -> if (data != 0) "true" else "false"
            TYPE_INT_HEX -> "0x${Integer.toHexString(data)}"
            TYPE_INT_DEC -> data.toString()
            TYPE_DIMENSION -> {
                val value = (data shr 8).toFloat()
                val unit = UNIT_STRS[data and 0xFF]
                "$value$unit"
            }
            TYPE_INT_COLOR_ARGB8, TYPE_INT_COLOR_RGB8, TYPE_INT_COLOR_ARGB4, TYPE_INT_COLOR_RGB4 ->
                "#${Integer.toHexString(data)}"
            else -> "($type)$data"
        }
    }
}