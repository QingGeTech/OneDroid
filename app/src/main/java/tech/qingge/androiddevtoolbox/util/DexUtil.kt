package tech.qingge.androiddevtoolbox.util

import org.jf.baksmali.BaksmaliOptions
import org.jf.dexlib2.Opcodes
import org.jf.dexlib2.dexbacked.DexBackedClassDef
import org.jf.dexlib2.dexbacked.DexBackedDexFile

object DexUtil {

    fun getDexClasses(dexFileBytes: ByteArray): Set<DexBackedClassDef?> {
        val dexFile = DexBackedDexFile(Opcodes.getDefault(),dexFileBytes)
        val options = BaksmaliOptions()
        return dexFile.classes
    }

}