package team.lodestar.lodestone.modules.rendering.model.entity.armor;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import team.lodestar.lodestone.modules.rendering.model.entity.EntityModelHolder;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class LodestoneArmorClientItemExtensions {
    private final Function<EntityModelSet, ? extends Model> model;

    public LodestoneArmorClientItemExtensions(EntityModelHolder<? extends Model> model) {
        this((Function<EntityModelSet, ? extends Model>) model::getModel);
    }

    public LodestoneArmorClientItemExtensions(Supplier<? extends Model> model) {
        this(entityModels -> model.get());
    }

    private LodestoneArmorClientItemExtensions(Function<EntityModelSet, ? extends Model> model) {
        this.model = Objects.requireNonNull(model);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Model getGenericArmorModel(LivingEntity entity, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel playerModel, EntityModelSet entityModels, float partialTicks) {
        Model armorModel = this.model.apply(entityModels);
        if (armorModel instanceof EntityModel entityModel) {
            float bodyRotation = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
            float headRotation = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
            float walkPosition = entity.walkAnimation.position();
            float walkSpeed = entity.walkAnimation.speed();
            float tickCount = entity.tickCount + partialTicks;
            float netHeadYaw = headRotation - bodyRotation;
            float netHeadPitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

            if (entityModel instanceof LodestoneArmorModel lodestoneArmorModel) {
                lodestoneArmorModel.slot = armorSlot;
                lodestoneArmorModel.copyFromDefault(playerModel);
            }
            entityModel.setupAnim(entity, walkPosition, walkSpeed, tickCount, netHeadYaw, netHeadPitch);
            if (entityModel instanceof HumanoidModel humanoidModel) {
                copyHumanoidProperties(playerModel, humanoidModel);
                setHumanoidPartVisibility(humanoidModel, armorSlot);
            } else {
                playerModel.copyPropertiesTo(entityModel);
            }
        }
        return armorModel;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void copyHumanoidProperties(HumanoidModel source, HumanoidModel target) {
        source.copyPropertiesTo(target);
        target.head.visible = source.head.visible;
        target.hat.visible = source.hat.visible;
        target.body.visible = source.body.visible;
        target.rightArm.visible = source.rightArm.visible;
        target.leftArm.visible = source.leftArm.visible;
        target.rightLeg.visible = source.rightLeg.visible;
        target.leftLeg.visible = source.leftLeg.visible;
    }

    private static void setHumanoidPartVisibility(HumanoidModel<?> model, EquipmentSlot slot) {
        model.setAllVisible(false);
        switch (slot) {
            case HEAD -> {
                model.head.visible = true;
                model.hat.visible = true;
            }
            case CHEST -> {
                model.body.visible = true;
                model.rightArm.visible = true;
                model.leftArm.visible = true;
            }
            case LEGS -> {
                model.body.visible = true;
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
            case FEET -> {
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
        }
    }
}
