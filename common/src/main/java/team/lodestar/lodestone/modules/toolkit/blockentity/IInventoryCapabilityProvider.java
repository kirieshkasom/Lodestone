package team.lodestar.lodestone.modules.toolkit.blockentity;

import net.minecraft.core.Direction;
import team.lodestar.lodestone.modules.toolkit.inventory.ItemInventory;

public interface IInventoryCapabilityProvider {

    ItemInventory getInventory(Direction direction);
}
