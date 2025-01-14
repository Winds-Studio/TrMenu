package trplugins.menu.api.action.impl.logic

import taboolib.common.platform.ProxyPlayer
import trplugins.menu.api.action.ActionHandle
import trplugins.menu.api.action.base.ActionBase
import trplugins.menu.api.action.base.ActionContents
import trplugins.menu.api.action.base.ActionEval

/**
 * TrMenu
 * trplugins.menu.api.action.impl.logic.Return
 *
 * @author Score2
 * @since 2022/02/10 22:09
 */
class Break(handle: ActionHandle) : ActionBase(handle), ActionEval {
    override val regex = "return|break".toRegex()

    override fun onEval(contents: ActionContents, player: ProxyPlayer, placeholderPlayer: ProxyPlayer): Boolean {
        return false
    }
}