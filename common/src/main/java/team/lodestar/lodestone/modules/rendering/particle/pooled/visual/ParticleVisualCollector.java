package team.lodestar.lodestone.modules.rendering.particle.pooled.visual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParticleVisualCollector {
    private final List<ParticleVisualSubmission> submissions = new ArrayList<>();

    public void submit(ParticleVisualSubmission submission) {
        submissions.add(submission);
    }

    public List<ParticleVisualSubmission> submissions() {
        return Collections.unmodifiableList(submissions);
    }

    public boolean isEmpty() {
        return submissions.isEmpty();
    }

    public void clear() {
        submissions.clear();
    }
}