package team.lodestar.lodestone.modules.toolkit.blockentity;

import net.minecraft.core.*;
import net.neoforged.neoforge.items.*;

public interface IInventoryCapabilityProvider {

    IItemHandler getInventory(Direction direction);
}
