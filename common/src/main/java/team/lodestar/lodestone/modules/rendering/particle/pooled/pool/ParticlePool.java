package team.lodestar.lodestone.modules.rendering.particle.pooled.pool;

import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import net.minecraft.client.*;
import team.lodestar.lodestone.modules.rendering.particle.pooled.builder.ParticleSpec;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.ParticleComponentType;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.PostUpdateComponent;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.PreRenderComponent;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.PreUpdateComponent;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticlePhase;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleSpawnContext;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleStorageBinding;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleView;
import team.lodestar.lodestone.modules.rendering.particle.pooled.storage.ParticleComponentStorage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@SuppressWarnings({"rawtypes", "unchecked"})
public class ParticlePool implements ParticleView {
    private static final Comparator<ParticleStorageBinding> BINDING_PRIORITY = Comparator.comparingInt(a -> a.type().priority());

    private final int capacity;
    private int count;

    private final double[] x, y, z;
    private final double[] vx, vy, vz;
    private final float[] r, g, b, a;

    private final float[] spin;

    private final float[] xRot, yRot, zRot;
    private final float[] xScale, yScale, zScale;

    private final int[] age, lifetime;

    private final int[] visualIds;
    private final Map<Integer, Integer> activeVisualCounts = new ConcurrentHashMap<>();

    private final Map<ParticleComponentType<?>, ParticleStorageBinding> bindings = new ConcurrentHashMap<>();
    private long tickCount;
    private final Long2ObjectArrayMap<DelayedParticles> delayedParticles = new Long2ObjectArrayMap<>();

    private final List<ParticleStorageBinding> preUpdateBindings = new ArrayList<>();
    private final List<ParticleStorageBinding> postUpdateBindings = new ArrayList<>();
    private final List<ParticleStorageBinding> preRenderBindings = new ArrayList<>();
    private final List<SwapRemoveListener> swapRemoveListeners = new ArrayList<>();

    public ParticlePool(int capacity, ParticleSpec spec) {
        this.capacity = capacity;

        this.x = new double[capacity];
        this.y = new double[capacity];
        this.z = new double[capacity];

        this.vx = new double[capacity];
        this.vy = new double[capacity];
        this.vz = new double[capacity];

        this.r = new float[capacity];
        this.g = new float[capacity];
        this.b = new float[capacity];
        this.a = new float[capacity];

        this.spin = new float[capacity];

        this.xRot = new float[capacity];
        this.yRot = new float[capacity];
        this.zRot = new float[capacity];

        this.xScale = new float[capacity];
        this.yScale = new float[capacity];
        this.zScale = new float[capacity];

        this.age = new int[capacity];
        this.lifetime = new int[capacity];

        this.visualIds = new int[capacity];

        for (ParticleComponentType<?> type : spec.orderedComponentTypes()) {
            ParticleComponentStorage storage = type.createStorage(capacity);
            registerBinding(new ParticleStorageBinding(type, storage));
        }
    }

    public int capacity() {
        return capacity;
    }

    public int count() {
        return count;
    }

    public boolean isFull() {
        return count >= capacity;
    }

    public Iterable<Integer> getActiveVisualIds() {
        return activeVisualCounts.keySet();
    }

    public synchronized void addSwapRemoveListener(SwapRemoveListener listener) {
        swapRemoveListeners.add(listener);
    }

    public synchronized void spawn(ParticleSpec spec, ParticleSpawnContext ctx) {
        if (count >= capacity) {
            return;
        }
        if (kidnapDelayedParticles(spec, ctx)) {
            return;
        }

        int i = count;

        x[i] = ctx.x;
        y[i] = ctx.y;
        z[i] = ctx.z;

        vx[i] = ctx.vx;
        vy[i] = ctx.vy;
        vz[i] = ctx.vz;

        r[i] = ctx.r;
        g[i] = ctx.g;
        b[i] = ctx.b;
        a[i] = ctx.a;

        xRot[i] = ctx.xRot;
        yRot[i] = ctx.yRot;
        zRot[i] = ctx.zRot;

        xScale[i] = ctx.xScale;
        yScale[i] = ctx.yScale;
        zScale[i] = ctx.zScale;

        age[i] = 0;
        lifetime[i] = ctx.lifetime;

        visualIds[i] = spec.visualId();
        activeVisualCounts.merge(spec.visualId(), 1, Integer::sum);

        for (ParticleComponentType<?> type : spec.orderedComponentTypes()) {
            ParticleStorageBinding binding = bindings.get(type);
            Object config = spec.componentConfigs().get(type);
            binding.storage().onSpawn(i, config, ctx, this);
        }

        count++;
    }

    private void registerBinding(ParticleStorageBinding binding) {
        bindings.put(binding.type(), binding);

        if (binding.type().phases().contains(ParticlePhase.PRE_UPDATE)) {
            preUpdateBindings.add(binding);
            preUpdateBindings.sort(BINDING_PRIORITY);
        }
        if (binding.type().phases().contains(ParticlePhase.POST_UPDATE)) {
            postUpdateBindings.add(binding);
            postUpdateBindings.sort(BINDING_PRIORITY);
        }
        if (binding.type().phases().contains(ParticlePhase.PRE_RENDER)) {
            preRenderBindings.add(binding);
            preRenderBindings.sort(BINDING_PRIORITY);
        }
    }

    public void tick(float dt) {
        tickCount++;

        addReadyDelayedParticles();

        for (ParticleStorageBinding binding : preUpdateBindings) {
            if (binding.storage() instanceof PreUpdateComponent c) {
                c.preUpdate(count, dt, this);
            }
        }

        double[] x = this.x, y = this.y, z = this.z;
        double[] vx = this.vx, vy = this.vy, vz = this.vz;
        int[] age = this.age, lifetime = this.lifetime;

        int i = 0;
        while (i < count) {
            int newAge = age[i] + 1;
            age[i] = newAge;

            if (newAge >= lifetime[i]) {
                remove(i);
                continue;
            }

            x[i] += vx[i] * dt;
            y[i] += vy[i] * dt;
            z[i] += vz[i] * dt;
            i++;
        }

        for (ParticleStorageBinding binding : postUpdateBindings) {
            if (binding.storage() instanceof PostUpdateComponent c) {
                c.postUpdate(count, dt, this);
            }
        }
    }

    public void preRender(float partialTicks) {
        for (ParticleStorageBinding binding : preRenderBindings) {
            if (binding.storage() instanceof PreRenderComponent c) {
                c.preRender(count, this, partialTicks);
            }
        }
    }

    public void remove(int index) {
        int last = count - 1;
        int removedVisualId = visualIds[index];

        if (index != last) {
            x[index] = x[last];
            y[index] = y[last];
            z[index] = z[last];

            vx[index] = vx[last];
            vy[index] = vy[last];
            vz[index] = vz[last];

            r[index] = r[last];
            g[index] = g[last];
            b[index] = b[last];
            a[index] = a[last];

            xRot[index] = xRot[last];
            yRot[index] = yRot[last];
            zRot[index] = zRot[last];

            xScale[index] = xScale[last];
            yScale[index] = yScale[last];
            zScale[index] = zScale[last];

            age[index] = age[last];
            lifetime[index] = lifetime[last];

            visualIds[index] = visualIds[last];
        }

        for (ParticleStorageBinding binding : bindings.values()) {
            binding.storage().onSwapRemove(index, last, this);
        }

        for (SwapRemoveListener listener : swapRemoveListeners) {
            listener.onSwapRemove(index, last);
        }

        count--;

        activeVisualCounts.computeIfPresent(removedVisualId, (k, v) -> (v == 1) ? null : v - 1);
    }

    public void clear() {
        while (count > 0) {
            remove(count - 1);
        }
    }

    public boolean kidnapDelayedParticles(ParticleSpec spec, ParticleSpawnContext ctx) {
        if (ctx.delay > 0) {
            long time = tickCount + ctx.delay;
            ctx.delay = 0;
            if (!delayedParticles.containsKey(time)) {
                delayedParticles.put(time, new DelayedParticles(spec));
            }
            delayedParticles.get(time).tryAdd(spec, ctx);
            return true;
        }
        return false;
    }

    public void addReadyDelayedParticles() {
        var delayed = delayedParticles.get(tickCount);
        if (delayed == null) {
            return;
        }
        delayedParticles.remove(tickCount);
        var spec = delayed.spec;
        for (ParticleSpawnContext particle : delayed.particles) {
            spawn(spec, particle);
        }
    }

    @Override
    public double[] x() {
        return x;
    }

    @Override
    public double[] y() {
        return y;
    }

    @Override
    public double[] z() {
        return z;
    }

    @Override
    public double[] vx() {
        return vx;
    }

    @Override
    public double[] vy() {
        return vy;
    }

    @Override
    public double[] vz() {
        return vz;
    }

    @Override
    public float[] r() {
        return r;
    }

    @Override
    public float[] g() {
        return g;
    }

    @Override
    public float[] b() {
        return b;
    }

    @Override
    public float[] a() {
        return a;
    }

    @Override
    public float[] xRot() {
        return xRot;
    }

    @Override
    public float[] yRot() {
        return yRot;
    }

    @Override
    public float[] zRot() {
        return zRot;
    }


    @Override
    public float[] spin() {
        return spin;
    }

    @Override
    public float[] xScale() {
        return xScale;
    }

    @Override
    public float[] yScale() {
        return yScale;
    }

    @Override
    public float[] zScale() {
        return zScale;
    }

    @Override
    public int[] age() {
        return age;
    }

    @Override
    public int[] lifetime() {
        return lifetime;
    }

    @Override
    public int[] visualIds() {
        return visualIds;
    }
}