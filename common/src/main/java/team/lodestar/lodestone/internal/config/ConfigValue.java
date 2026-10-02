package team.lodestar.lodestone.internal.config;

public interface ConfigValue<T> {
    T get();

    void set(T value);
}
