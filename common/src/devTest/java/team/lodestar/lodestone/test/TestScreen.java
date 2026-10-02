package team.lodestar.lodestone.test;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import team.lodestar.lodestone.modules.rendering.particle.standard.builder.ScreenParticleBuilder;
import team.lodestar.lodestone.modules.rendering.particle.standard.data.GenericParticleData;
import team.lodestar.lodestone.modules.rendering.particle.standard.screen.ScreenParticleHolder;
import team.lodestar.lodestone.registry.common.particle.LodestoneScreenParticleTypes;

public final class TestScreen extends Screen {
    private final boolean armor;
    private final ScreenParticleHolder particles = new ScreenParticleHolder();
    private ArmorStand stand;
    private final ItemStack crown = new ItemStack(TestContent.HELMET.get());
    private int ticks;

    public TestScreen(boolean armor) {
        super(Component.literal(armor ? "Lodestone armor test" : "Lodestone screen particle test"));
        this.armor = armor;
    }

    @Override
    protected void init() {
        if (armor && minecraft.level != null) {
            stand = new ArmorStand(minecraft.level, 0, 0, 0);
            stand.setNoBasePlate(true);
            stand.setItemSlot(EquipmentSlot.HEAD, new ItemStack(TestContent.HELMET.get()));
        }
    }

    @Override
    public void tick() {
        particles.tick();
        ticks++;
        if (!armor && ticks % 3 == 0) {
            ScreenParticleBuilder.create(LodestoneScreenParticleTypes.WISP, particles)
                    .setLifetime(60).setScaleData(GenericParticleData.create(0.6f, 0).build())
                    .setMotion(0, -0.5).spawn(width / 2.0 + Math.cos(ticks * 0.1) * 60, height / 2.0);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0x88000000);
        graphics.drawCenteredString(font, title, width / 2, 20, 0xFFFFFF);
        graphics.drawCenteredString(font, "ESC closes this test. F3+T reloads resources; resize the window too.", width / 2, 36, 0xFFFFFF);
        graphics.renderItem(crown, width / 2 - 8, height - 55);
        graphics.drawCenteredString(font, "Item emitter check", width / 2, height - 66, 0xFFFFFF);
        if (armor && stand != null) {
            InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, 20, 45, width - 20, height - 70, Math.max(20, Math.min(60, (height - 125) / 3)), 0, mouseX, mouseY, stand);
            graphics.drawString(font, "expected an oversized gold crown with a central spire.", 10, height - 30, 0xFFFFFF);
            graphics.drawString(font, "model bakes: " + TestClient.modelBakes, 10, height - 18, 0xFFFFFF);
        } else {
            particles.render(graphics);
            graphics.drawString(font, "a continuous arc of rising, shrinking screen-space wisps", 10, height - 20, 0xFFFFFF);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
