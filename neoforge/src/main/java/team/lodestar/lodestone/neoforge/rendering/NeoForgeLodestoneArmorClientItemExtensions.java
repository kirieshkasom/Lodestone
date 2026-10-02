package team.lodestar.lodestone.neoforge.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import team.lodestar.lodestone.modules.rendering.model.entity.EntityModelHolder;
import team.lodestar.lodestone.modules.rendering.model.entity.armor.LodestoneArmorClientItemExtensions;

import java.util.function.Supplier;

public final class NeoForgeLodestoneArmorClientItemExtensions implements IClientItemExtensions {
    private final LodestoneArmorClientItemExtensions extensions;

    public NeoForgeLodestoneArmorClientItemExtensions(EntityModelHolder<? extends Model> model) {
        this.extensions = new LodestoneArmorClientItemExtensions(model);
    }

    public NeoForgeLodestoneArmorClientItemExtensions(Supplier<? extends Model> model) {
        this.extensions = new LodestoneArmorClientItemExtensions(model);
    }

    @Override
    public @NotNull Model getGenericArmorModel(@NotNull LivingEntity entity, @NotNull ItemStack itemStack, @NotNull EquipmentSlot armorSlot, @NotNull HumanoidModel playerModel) {
        float partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        return this.extensions.getGenericArmorModel(entity, itemStack, armorSlot, playerModel, Minecraft.getInstance().getEntityModels(), partialTicks);
    }
}
