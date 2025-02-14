package tech.qingge.androiddevtoolbox.util

import android.view.Menu
import androidx.core.view.get

object CommonUtil {

    /**
     * find position of menuItem in a menu
     */
    fun findMenuPosition(menu: Menu, menuItemId: Int): Int {
        for (i in (0..<menu.size())) {
            if (menuItemId == menu[i].itemId) {
                return i
            }
        }
        return -1;
    }

}