package team.lodestar.lodestone.modules.toolkit.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import team.lodestar.lodestone.helpers.DataHelper;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntity;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntityTicker;
import team.lodestar.lodestone.modules.toolkit.inventory.display.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A class designed to help with tracking and then displaying items rotating around an object.
 */
@SuppressWarnings("unused")
public class ItemStackHandlerItemDisplayData implements LodestoneBlockEntityTicker.BlockEntityTickerAttachment {

    protected final LodestoneItemStackBlockHandler handler;

    protected ItemDisplayDataEntry[] dataEntries = new ItemDisplayDataEntry[]{};

    protected float oldTurn, turn;

    public ItemStackHandlerItemDisplayData(LodestoneItemStackBlockHandler handler) {
        this.handler = handler;
    }

    public void onContentsChanged(int slot) {

    }

    @Override
    public void tick(LodestoneBlockEntity parent, Level level, BlockPos pos, BlockState state) {
        oldTurn = turn;
        turn += getTurnRate();
        var stacks = handler.getStacks();

        int tickedStacks = 0;
        int totalStacks = handler.getNonEmptyStacks().size();
        if (dataEntries.length != stacks.size()) {
            dataEntries = new ItemDisplayDataEntry[stacks.size()];
        }
        for (int i = 0; i < stacks.size(); i++) {
            var stack = stacks.get(i);
            if (stack.isEmpty()) {
                dataEntries[i] = null;
                continue;
            }
            var entry = dataEntries[i];
            if (entry == null) {
                entry = addNewItem(i, stack);
            }
            float targetAngle = getAngleForItem(entry, tickedStacks, totalStacks);
            float correctedAngle = handleAngleCorrection(entry, tickedStacks, totalStacks, targetAngle);
            float itemTurn = getItemRotationRateForItem(entry, tickedStacks, totalStacks);
            float distance = getDistanceForItem(entry, tickedStacks, totalStacks);
            float lift = getLiftForItem(entry, tickedStacks, totalStacks);
            float scale = getItemScaleForItem(entry, tickedStacks, totalStacks);
            entry.tick(this, correctedAngle, itemTurn, distance, lift, scale);
            tickedStacks++;
        }
    }

    public Optional<ItemDisplayDataEntry> getEntry(int i) {
        if (dataEntries.length < i+1) {
            return Optional.empty();
        }
        return Optional.ofNullable(dataEntries[i]);
    }

    public List<ItemDisplayDataEntry> getDataEntries() {
        return Arrays.stream(dataEntries).filter(Objects::nonNull).toList();
    }

    public ItemDisplayDataEntry addNewItem(int index, ItemStack stack) {
        var blockEntity = handler.getParent();
        var random = Objects.requireNonNull(blockEntity.getLevel()).random;
        var seed = random.nextLong();
        var entry = new ItemDisplayDataEntry(stack, seed);
        dataEntries[index] = entry;
        return entry;
    }

    public final Vec3 getDisplayCenter() {
        return getDisplayCenter(0);
    }

    public final Vec3 getDisplayCenter(float partialTicks) {
        return getDisplayCenter(handler.getParent(), partialTicks);
    }

    public Vec3 getDisplayCenter(LodestoneBlockEntity parent, float partialTicks) {
        return parent.getBlockPos().getCenter();
    }

    public Optional<Vec3> getItemPosition(int index) {
        if (dataEntries[index] != null) {
            return Optional.of(getItemPosition(dataEntries[index]));
        }
        return Optional.empty();
    }

    public Vec3 getItemPosition(ItemDisplayDataEntry dataEntry) {
        return getItemPosition(dataEntry, 0);
    }

    public Vec3 getItemPosition(ItemDisplayDataEntry dataEntry, float partialTicks) {
        var center = getDisplayCenter(partialTicks);
        float distance = dataEntry.getDistance(partialTicks);
        float angle = dataEntry.getAngle(partialTicks) + getTurn(partialTicks);
        var x = center.x + Math.sin(angle) * distance;
        var y = center.y + dataEntry.getLift(partialTicks);
        var z = center.z + Math.cos(angle) * distance;
        return new Vec3(x, y, z);
    }

    public float getTurnRate() {
        return 0.02f;
    }

    public float handleAngleCorrection(ItemDisplayDataEntry item, int index, float total, float targetAngle) {
        float angle = item.getAngle(0);
        return DataHelper.approach(angle, targetAngle, 0.05f);
    }

    public float getItemScaleForItem(ItemDisplayDataEntry item, int index, float total) {
        return 1;
    }

    public float getAngleForItem(ItemDisplayDataEntry item, int index, float total) {
        return 6.28f * (index / total);
    }

    public float getDistanceForItem(ItemDisplayDataEntry item, int index, float total) {
        return 1;
    }

    public float getLiftForItem(ItemDisplayDataEntry item, int index, float total) {
        return 0;
    }

    public float getItemRotationRateForItem(ItemDisplayDataEntry item, int index, float total) {
        return 0.157f;
    }

    public float getTurn(float partialTicks) {
        return Mth.lerp(partialTicks, oldTurn, turn);
    }

}