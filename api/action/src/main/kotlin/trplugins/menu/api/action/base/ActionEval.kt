package trplugins.menu.api.action.base

import taboolib.common.platform.ProxyPlayer

/**
 * @author Rubenicos
 * @date 2024/11/21 14:42
 */
interface ActionEval {

    fun onEval(contents: ActionContents, player: ProxyPlayer, placeholderPlayer: ProxyPlayer = player): Boolean
}