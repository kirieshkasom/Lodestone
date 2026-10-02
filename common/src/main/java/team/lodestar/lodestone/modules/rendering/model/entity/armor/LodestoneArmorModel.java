package team.lodestar.lodestone.modules.rendering.model.entity.armor;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

import java.util.*;

/**
 * A simple implementation of an armor tied humanoid model.
 * Designing these in blockbench should be as simple as possible:
 * Start with armor_boilerplate.bbmodel
 * Do not change the pivot points of any bone already present in the boilerplate.
 * Once you're done, Export the model as a java file and paste the geometry into {@link LodestoneArmorModel#createArmorModel}.
 * Remove the generated part definitions of the Root* ModelParts
 * Done
 */
public class LodestoneArmorModel extends HumanoidModel<LivingEntity> {
    public EquipmentSlot slot;
    public ModelPart root, headArmor, bodyArmor, waistArmor, leftArmArmor, rightArmArmor, leftLegArmor, rightLegArmor, leftFootArmor, rightFootArmor;

    public LodestoneArmorModel(ModelPart root) {
        super(root);
        this.root = root;
        this.headArmor = getPart("head");
        this.bodyArmor = getPart("body");
        this.waistArmor = getPart("waist");
        this.leftArmArmor = getPart("left_arm");
        this.rightArmArmor = getPart("right_arm");
        this.leftLegArmor = getPart("left_leg");
        this.rightLegArmor = getPart("right_leg");
        this.leftFootArmor = getPart("left_foot");
        this.rightFootArmor = getPart("right_foot");
    }

    public ModelPart getPart(String name) {
        return getPart(root, name);
    }

    public static PartDefinition createHumanoidAlias(MeshDefinition mesh) {
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", new CubeListBuilder(), PartPose.ZERO);
        root.addOrReplaceChild("body", new CubeListBuilder(), PartPose.ZERO);
        root.addOrReplaceChild("waist", new CubeListBuilder(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", new CubeListBuilder(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", new CubeListBuilder(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", new CubeListBuilder(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", new CubeListBuilder(), PartPose.ZERO);
        root.addOrReplaceChild("right_foot", new CubeListBuilder(), PartPose.ZERO);
        root.addOrReplaceChild("left_foot", new CubeListBuilder(), PartPose.ZERO);
        return root;
    }

    public static LayerDefinition createArmorModel(ILodestoneArmorModelBuilder modelBuilder) {
        MeshDefinition mesh = HumanoidModel.createMesh(new CubeDeformation(0), 0);
        PartDefinition root = createHumanoidAlias(mesh);
        PartDefinition head = root.getChild("head");
        PartDefinition body = root.getChild("body");
        PartDefinition waist = root.getChild("waist");
        PartDefinition right_arm = root.getChild("right_arm");
        PartDefinition left_arm = root.getChild("left_arm");
        PartDefinition right_leg = root.getChild("right_leg");
        PartDefinition left_leg = root.getChild("left_leg");
        PartDefinition right_foot = root.getChild("right_foot");
        PartDefinition left_foot = root.getChild("left_foot");
        return modelBuilder.createArmorLayer(mesh, root, head, body, waist, right_arm, left_arm, right_leg, left_leg, right_foot, left_foot);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return slot == EquipmentSlot.HEAD ? ImmutableList.of(headArmor) : ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        switch (slot) {
            case CHEST -> {
                return ImmutableList.of(bodyArmor, leftArmArmor, rightArmArmor);
            }
            case LEGS -> {
                return ImmutableList.of(leftLegArmor, rightLegArmor, waistArmor);
            }
            case FEET -> {
                return ImmutableList.of(leftFootArmor, rightFootArmor);
            }
        }
        return ImmutableList.of();
    }

    public void copyFromDefault(HumanoidModel model) {
        bodyArmor.copyFrom(model.body);
        headArmor.copyFrom(model.head);
        waistArmor.copyFrom(model.body);
        leftArmArmor.copyFrom(model.leftArm);
        rightArmArmor.copyFrom(model.rightArm);
        leftLegArmor.copyFrom(model.leftLeg);
        rightLegArmor.copyFrom(model.rightLeg);
        leftFootArmor.copyFrom(model.leftLeg);
        rightFootArmor.copyFrom(model.rightLeg);
    }

    public static ModelPart getPart(ModelPart root, String name) {
        try {
            return root.getChild(name);
        } catch (Exception ignored) {
            return new ModelPart(Collections.emptyList(), Collections.emptyMap());
        }
    }

    public interface ILodestoneArmorModelBuilder {
        LayerDefinition createArmorLayer(MeshDefinition mesh, PartDefinition root, PartDefinition head, PartDefinition body, PartDefinition waist, PartDefinition right_arm, PartDefinition left_arm, PartDefinition right_leg, PartDefinition left_leg, PartDefinition right_foot, PartDefinition left_foot);
    }
}