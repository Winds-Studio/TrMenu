package trplugins.menu.api.action.impl.send

import taboolib.common.platform.ProxyPlayer
import taboolib.module.chat.component
import trplugins.menu.api.action.ActionHandle
import trplugins.menu.api.action.base.ActionBase
import trplugins.menu.api.action.base.ActionContents

/**
 * TrMenu
 * trplugins.menu.api.action.impl.chat.Tell
 *
 * @author lilingfengdev
 * @since 2024/7/6 19:14
 */
class Tell(handle: ActionHandle) : ActionBase(handle) {

    override val regex = "tell|message|msg|talk".toRegex()
    companion object{
        var useComponent = true;
    }
    override fun onExecute(contents: ActionContents, player: ProxyPlayer, placeholderPlayer: ProxyPlayer) {
        contents.stringContent().parseContentSplited(placeholderPlayer).forEach {
            if (useComponent) {
                it.component().buildColored().sendTo(player)
            } else {
                player.sendMessage(it)
            }
        }
    }

}