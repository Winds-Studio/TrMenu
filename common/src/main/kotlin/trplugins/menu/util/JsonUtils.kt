package trplugins.menu.util

import com.google.gson.JsonElement
import com.google.gson.JsonParser

/**
 * TrMenu
 * trplugins.menu.util.GsonUtils
 *
 * @author Score2
 * @since 2022/02/06 22:33
 */

fun String.parseJson(): JsonElement = JsonParser.parseString(this)