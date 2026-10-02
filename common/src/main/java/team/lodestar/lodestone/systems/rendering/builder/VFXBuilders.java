package team.lodestar.lodestone.systems.rendering.builder;

import com.mojang.blaze3d.vertex.*;
import org.joml.*;

import java.util.*;

public class VFXBuilders {

    public static ScreenVFXBuilder createScreen() {
        return new ScreenVFXBuilder();
    }

    public static WorldVFXBuilder createWorld() {
        return new WorldVFXBuilder();
    }

}