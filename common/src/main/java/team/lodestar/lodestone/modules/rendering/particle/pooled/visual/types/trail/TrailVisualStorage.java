package team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.trail;

import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleView;

public class TrailVisualStorage {
    private final int capacity;
    private final int maxPoints;

    private final double[] xHistory;
    private final double[] yHistory;
    private final double[] zHistory;
    private final int[] historyCount;
    private final int[] lastCaptureAge;

    public TrailVisualStorage(int capacity, int maxPoints) {
        this.capacity = capacity;
        this.maxPoints = maxPoints;

        this.xHistory = new double[capacity * maxPoints];
        this.yHistory = new double[capacity * maxPoints];
        this.zHistory = new double[capacity * maxPoints];
        this.historyCount = new int[capacity];
        this.lastCaptureAge = new int[capacity];

        for (int i = 0; i < capacity; i++) lastCaptureAge[i] = -1;
    }

    public void capture(ParticleView view, int liveCount, int targetVisualId) {
        double[] x = view.x(), y = view.y(), z = view.z();
        int[] age = view.age();
        int[] visualIds = view.visualIds();

        for (int i = 0; i < liveCount; i++) {
            if (visualIds[i] != targetVisualId) continue;

            int currentAge = age[i];
            if (currentAge == 0 && lastCaptureAge[i] != 0) historyCount[i] = 0;
            if (lastCaptureAge[i] == currentAge) continue;
            lastCaptureAge[i] = currentAge;

            int count = historyCount[i];
            int baseIdx = i * maxPoints;

            if (count == 0) {
                for (int j = 0; j < 3; j++) {
                    xHistory[baseIdx + j] = x[i]; yHistory[baseIdx + j] = y[i]; zHistory[baseIdx + j] = z[i];
                }
                historyCount[i] = 3;
            } else if (count < maxPoints) {
                xHistory[baseIdx + count] = x[i];
                yHistory[baseIdx + count] = y[i];
                zHistory[baseIdx + count] = z[i];
                historyCount[i] = count + 1;
            } else {
                System.arraycopy(xHistory, baseIdx + 1, xHistory, baseIdx, maxPoints - 1);
                System.arraycopy(yHistory, baseIdx + 1, yHistory, baseIdx, maxPoints - 1);
                System.arraycopy(zHistory, baseIdx + 1, zHistory, baseIdx, maxPoints - 1);
                xHistory[baseIdx + maxPoints - 1] = x[i]; yHistory[baseIdx + maxPoints - 1] = y[i]; zHistory[baseIdx + maxPoints - 1] = z[i];
            }
        }
    }

    public void onSwapRemove(int deadIndex, int movedIndex) {
        if (deadIndex != movedIndex) {
            historyCount[deadIndex] = historyCount[movedIndex];
            lastCaptureAge[deadIndex] = lastCaptureAge[movedIndex];
            System.arraycopy(xHistory, movedIndex * maxPoints, xHistory, deadIndex * maxPoints, maxPoints);
            System.arraycopy(yHistory, movedIndex * maxPoints, yHistory, deadIndex * maxPoints, maxPoints);
            System.arraycopy(zHistory, movedIndex * maxPoints, zHistory, deadIndex * maxPoints, maxPoints);
        }
        historyCount[movedIndex] = 0;
        lastCaptureAge[movedIndex] = -1;
    }

    public double[] xHistory() {
        return xHistory;
    }

    public double[] yHistory() {
        return yHistory;
    }

    public double[] zHistory() {
        return zHistory;
    }

    public int[] historyCount() {
        return historyCount;
    }

    public int maxPoints() {
        return maxPoints;
    }
}