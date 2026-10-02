package team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.boilerplate;

import team.lodestar.lodestone.modules.core.easing.*;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.*;

public interface ITrinaryConfig {

    ConstantLerpOrDoubleLerp getMode();

    float[] getValues();

    Easing[] getEasings();
}
