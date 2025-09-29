package com.boozer.nexus.desktop.ui;

import com.boozer.nexus.desktop.backend.GenerationSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** UI-level intent describing how generation should start. */
public final class GenerationIntent {
    private final String seedDescription;
    private final List<String> featureTags;
    private final GenerationSource source;

    public GenerationIntent(String seedDescription, List<String> featureTags, GenerationSource source) {
        this.seedDescription = seedDescription;
        this.featureTags = featureTags == null ? List.of() : Collections.unmodifiableList(new ArrayList<>(featureTags));
        this.source = source == null ? GenerationSource.GUI_BUTTON : source;
    }

    public String getSeedDescription() {
        return seedDescription;
    }

    public List<String> getFeatureTags() {
        return featureTags;
    }

    public GenerationSource getSource() {
        return source;
    }

    public static GenerationIntent primaryAction() {
        return new GenerationIntent(null, List.of(), GenerationSource.GUI_BUTTON);
    }

    public static GenerationIntent featureCard(String description, List<String> tags) {
        return new GenerationIntent(description, tags, GenerationSource.FEATURE_CARD);
    }
}
