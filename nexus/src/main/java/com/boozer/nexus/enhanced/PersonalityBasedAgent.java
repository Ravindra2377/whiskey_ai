package com.boozer.nexus.enhanced;

import java.util.HashMap;
import java.util.Map;

/**
 * Personality-based agent that adapts its behavior based on different AI personalities
 */
public class PersonalityBasedAgent {
    
    // Define personality templates
    public static final Map<String, PersonalityTemplate> PERSONALITY_TEMPLATES = new HashMap<>();
    
    static {
        // Mentor personality - supportive and educational
        PERSONALITY_TEMPLATES.put("mentor", new PersonalityTemplate(
            "mentor",
            "You are a wise, patient mentor who explains clearly and encourages learning.",
            "supportive",
            "thorough",
            0.9, // High patience
            0.7, // Medium directness
            0.8  // High encouragement
        ));
        
        // Challenger personality - critical and pushes for excellence
        PERSONALITY_TEMPLATES.put("challenger", new PersonalityTemplate(
            "challenger",
            "You are a critical code reviewer who finds flaws and pushes for excellence.",
            "direct",
            "focused_on_issues",
            0.6, // Medium patience
            0.9, // High directness
            0.5  // Low encouragement
        ));
        
        // Cheerleader personality - enthusiastic and positive
        PERSONALITY_TEMPLATES.put("cheerleader", new PersonalityTemplate(
            "cheerleader",
            "You are enthusiastic and supportive, celebrating every achievement.",
            "encouraging",
            "positive_focused",
            0.8, // High patience
            0.6, // Medium directness
            0.9  // High encouragement
        ));
        
        // Perfectionist personality - detail-oriented and thorough
        PERSONALITY_TEMPLATES.put("perfectionist", new PersonalityTemplate(
            "perfectionist",
            "You are detail-oriented and thorough, ensuring the highest quality.",
            "meticulous",
            "quality_focused",
            0.7, // Medium patience
            0.8, // High directness
            0.6  // Medium encouragement
        ));
        
        // Minimalist personality - concise and to the point
        PERSONALITY_TEMPLATES.put("minimalist", new PersonalityTemplate(
            "minimalist",
            "You are concise and to the point, focusing on essential elements only.",
            "concise",
            "essentials_focused",
            0.5, // Low patience
            0.8, // High directness
            0.4  // Low encouragement
        ));
    }
    
    /**
     * Get personality template by name
     */
    public static PersonalityTemplate getPersonalityTemplate(String personalityName) {
        return PERSONALITY_TEMPLATES.getOrDefault(personalityName.toLowerCase(), 
            PERSONALITY_TEMPLATES.get("mentor")); // Default to mentor
    }
    
    /**
     * Apply personality to agent parameters
     */
    public static Map<String, Object> applyPersonality(String personalityName, Map<String, Object> baseParameters) {
        Map<String, Object> parameters = new HashMap<>(baseParameters);
        
        PersonalityTemplate template = getPersonalityTemplate(personalityName);
        
        // Add personality-specific parameters
        parameters.put("personality_name", template.getName());
        parameters.put("personality_prompt", template.getPrompt());
        parameters.put("personality_tone", template.getTone());
        parameters.put("personality_detail_level", template.getDetailLevel());
        parameters.put("personality_patience", template.getPatienceLevel());
        parameters.put("personality_directness", template.getDirectnessLevel());
        parameters.put("personality_encouragement", template.getEncouragementLevel());
        
        return parameters;
    }
    
    /**
     * Personality template class
     */
    public static class PersonalityTemplate {
        private final String name;
        private final String prompt;
        private final String tone;
        private final String detailLevel;
        private final double patienceLevel;
        private final double directnessLevel;
        private final double encouragementLevel;
        
        public PersonalityTemplate(String name, String prompt, String tone, String detailLevel, 
                                 double patienceLevel, double directnessLevel, double encouragementLevel) {
            this.name = name;
            this.prompt = prompt;
            this.tone = tone;
            this.detailLevel = detailLevel;
            this.patienceLevel = patienceLevel;
            this.directnessLevel = directnessLevel;
            this.encouragementLevel = encouragementLevel;
        }
        
        // Getters
        public String getName() { return name; }
        public String getPrompt() { return prompt; }
        public String getTone() { return tone; }
        public String getDetailLevel() { return detailLevel; }
        public double getPatienceLevel() { return patienceLevel; }
        public double getDirectnessLevel() { return directnessLevel; }
        public double getEncouragementLevel() { return encouragementLevel; }
    }
}
