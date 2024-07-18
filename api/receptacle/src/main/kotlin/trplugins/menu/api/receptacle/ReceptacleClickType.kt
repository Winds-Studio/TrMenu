package trplugins.menu.api.receptacle

import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryAction

/**
 * @author Arasple
 * @date 2020/12/5 22:01
 */
enum class ReceptacleClickType(
    private val mode: Int,
    private val button: Int,
    private val typeName: String? = null,
    private val action: Any? = null
) {


    ALL(-1, -1),

    LEFT(0, 0, "LEFT"),

    RIGHT(0, 1, "RIGHT"),

    SHIFT_LEFT(1, 0, "SHIFT_LEFT"),

    SHIFT_RIGHT(1, 1, "SHIFT_RIGHT"),

    OFFHAND(2, 40, "SWAP_OFFHAND"),

    NUMBER_KEY(2, -1, "NUMBER_KEY", -1),

    NUMBER_KEY_1(2, 0, "NUMBER_KEY", 0),

    NUMBER_KEY_2(2, 1, "NUMBER_KEY", 1),

    NUMBER_KEY_3(2, 2, "NUMBER_KEY", 2),

    NUMBER_KEY_4(2, 3, "NUMBER_KEY", 3),

    NUMBER_KEY_5(2, 4, "NUMBER_KEY", 4),

    NUMBER_KEY_6(2, 5, "NUMBER_KEY", 5),

    NUMBER_KEY_7(2, 6, "NUMBER_KEY", 6),

    NUMBER_KEY_8(2, 7, "NUMBER_KEY", 7),

    NUMBER_KEY_9(2, 8, "NUMBER_KEY", 8),

    MIDDLE(3, 2, "MIDDLE"),

    // clicked Item will be empty
    DROP(4, 0, "DROP"),

    CONTROL_DROP(4, 1, "CONTROL_DROP"),

    ABROAD_LEFT_EMPTY(4, 0, "DROP", InventoryAction.DROP_ONE_SLOT),

    ABROAD_RIGHT_EMPTY(4, 1, "CONTROL_DROP", InventoryAction.DROP_ALL_SLOT),

    ABROAD_LEFT_ITEM(0, 0, "LEFT", InventoryAction.DROP_ALL_CURSOR),

    ABROAD_RIGHT_ITEM(0, 1, "RIGHT", InventoryAction.DROP_ONE_CURSOR),

    LEFT_MOUSE_DRAG_ADD(5, 1),

    RIGHT_MOUSE_DRAG_ADD(5, 5),

    MIDDLE_MOUSE_DRAG_ADD(5, 9),

    DOUBLE_CLICK(6, 0, "DOUBLE_CLICK"),

    UNKNOWN(-1, -1);

    fun toBukkitType(): ClickType {
        return typeName?.let { ClickType.valueOf(it) } ?: ClickType.UNKNOWN
    }

    fun equals(mode: Int, button: Int): Boolean {
        return this.mode == mode && this.button == button
    }

    fun equals(type: ClickType?, action: Any?): Boolean {
        return toBukkitType() == type && this.action == action
    }

    fun isRightClick(): Boolean {
        return this == RIGHT || this == SHIFT_RIGHT
    }

    fun isLeftClick(): Boolean {
        return this == LEFT || this == SHIFT_LEFT || this == DOUBLE_CLICK
    }

    private fun isShiftClick(): Boolean {
        return this == SHIFT_LEFT || this == SHIFT_RIGHT
    }

    private fun isKeyboardClick(): Boolean {
        return this == DROP || this == CONTROL_DROP || this == OFFHAND || isNumberKeyClick()
    }

    fun isNumberKeyClick(): Boolean {
        return NUMBER_KEY.typeName == typeName
    }

    private fun isDoubleClick(): Boolean {
        return this == DOUBLE_CLICK
    }

    private fun isCreativeAction(): Boolean {
        return this == MIDDLE || this == MIDDLE_MOUSE_DRAG_ADD
    }

    fun isItemMoveable(): Boolean {
        return isKeyboardClick() || isShiftClick() || isCreativeAction() || isDoubleClick()
    }

    companion object {

        private val modes = arrayOf("PICKUP", "QUICK_MOVE", "SWAP", "CLONE", "THROW", "QUICK_CRAFT", "PICKUP_ALL")
        private val actions = setOf(
            InventoryAction.DROP_ALL_CURSOR,
            InventoryAction.DROP_ONE_CURSOR,
            InventoryAction.DROP_ONE_SLOT,
            InventoryAction.DROP_ALL_SLOT
        )

        private fun matchesFirst(string: String): ReceptacleClickType {
            return entries.find { it.name.equals(string, true) } ?: ALL
        }

        fun matches(string: String): Set<ReceptacleClickType> {
            return string.split(",", ";").map { matchesFirst(it) }.toSet()
        }

        fun from(mode: String, button: Int, slot: Int = -1): ReceptacleClickType? {
            return from(modes.indexOf(mode), button, slot)
        }

        fun from(mode: Int, button: Int, slot: Int = -1): ReceptacleClickType? {
            if (slot == -999) {
                return when {
                    LEFT.equals(mode, button) -> ABROAD_LEFT_ITEM
                    RIGHT.equals(mode, button) -> ABROAD_RIGHT_ITEM
                    ABROAD_LEFT_EMPTY.equals(mode, button) -> ABROAD_LEFT_EMPTY
                    ABROAD_RIGHT_EMPTY.equals(mode, button) -> ABROAD_RIGHT_EMPTY
                    else -> UNKNOWN
                }
            }
            return entries.find { it.equals(mode, button) }
        }

        fun from(type: ClickType?, action: InventoryAction?, slot: Int = -1): ReceptacleClickType? {
            val act: Any? = if (type == ClickType.NUMBER_KEY) {
                slot
            } else if (actions.contains(action)) {
                action
            } else {
                null
            }
            return entries.find { it.equals(type, act) }
        }

    }
}