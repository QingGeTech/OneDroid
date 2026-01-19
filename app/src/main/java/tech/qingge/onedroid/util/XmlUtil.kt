package tech.qingge.onedroid.util

import mt.modder.hub.axml.AXMLPrinter

object XmlUtil {

    fun getXmlString(binaryXml: ByteArray): String {
        val axmlPrinter = AXMLPrinter()
        axmlPrinter.setEnableID2Name(true)
        axmlPrinter.setAttrValueTranslation(true)
        axmlPrinter.setExtractPermissionDescription(true)
        return axmlPrinter.convertXml(binaryXml)
    }

}