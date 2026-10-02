package team.lodestar.lodestone.modules.rendering.model.obj.modifier;


public interface ModifierQueue {
    void queueEarlyModifier(ModelModifier modifier);
    void queueModifier(ModelModifier modifier);
}
