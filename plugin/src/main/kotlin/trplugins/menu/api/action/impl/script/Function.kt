package trplugins.menu.api.action.impl.script

import taboolib.common.platform.ProxyPlayer
import taboolib.common.util.subList
import trplugins.menu.api.action.ActionHandle
import trplugins.menu.api.action.base.ActionBase
import trplugins.menu.api.action.base.ActionContents
import trplugins.menu.api.action.base.ActionEval
import trplugins.menu.module.display.session
import trplugins.menu.util.Regexs

/**
 * @author Rubenicos
 * @date 2024/11/21 14:42
 */
class Function(handle: ActionHandle) : ActionBase(handle), ActionEval {

    override val regex = "(run-?)?functions?|run".toRegex()

    override fun onExecute(contents: ActionContents, player: ProxyPlayer, placeholderPlayer: ProxyPlayer) {
        onEval(contents, player, placeholderPlayer)
    }

    override fun onEval(contents: ActionContents, player: ProxyPlayer, placeholderPlayer: ProxyPlayer): Boolean {
        val session = player.session()
        val menu = session.menu ?: return true
        val func = contents.stringContent().parseContent(placeholderPlayer).split(' ')

        menu.settings.internalFunctions.forEach {
            if (it.id == func[0]) {
                val args = subList(func, 1, func.size)
                return !it.compile(session, args).asString().lowercase().matches(Regexs.FALSE)
            }
        }
        return true
    }

}