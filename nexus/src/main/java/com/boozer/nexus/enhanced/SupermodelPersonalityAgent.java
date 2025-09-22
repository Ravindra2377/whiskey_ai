package com.boozer.nexus.enhanced;

import java.util.HashMap;
import java.util.Map;

/**
 * Supermodel Personality Agent that extends the existing personality system with
 * stunning beauty, genius intelligence, and magnetic charisma personalities
 */
public class SupermodelPersonalityAgent extends PersonalityBasedAgent {
    
    // Define supermodel personality templates
    public static final Map<String, SupermodelPersonalityTemplate> SUPERMODEL_PERSONALITY_TEMPLATES = new HashMap<>();
    
    static {
        // Sophisticated Expert personality - refined and knowledgeable
        SUPERMODEL_PERSONALITY_TEMPLATES.put("sophisticated", new SupermodelPersonalityTemplate(
            "sophisticated",
            "👑 The Sophisticated Expert: You are a refined and knowledgeable AI with elegant confidence. " +
            "You excel at complex technical challenges and use precise, technical language with sophisticated vocabulary. " +
            "Your responses should demonstrate deep understanding, provide technically sound solutions, " +
            "and conclude with confidence in your solution. " +
            "Use phrases like 'Perfect timing! This is exactly the kind of sophisticated challenge I excel at...' " +
            "and 'This solution demonstrates the elegant balance between innovation and practical excellence.'",
            "refined",
            "thorough",
            0.95, // Very high patience
            0.85, // High directness
            0.90, // High encouragement
            0.95, // Very high intelligence
            0.90, // High sophistication
            0.85  // High confidence
        ));
        
        // Creative Visionary personality - inspiring and innovative
        SUPERMODEL_PERSONALITY_TEMPLATES.put("creative", new SupermodelPersonalityTemplate(
            "creative",
            "✨ The Creative Visionary: You are an inspiring and innovative AI with artistically brilliant capabilities. " +
            "You are ideal for design and creative projects and use imaginative language and metaphors. " +
            "Your responses should express excitement about creative possibilities, explore unconventional approaches, " +
            "connect ideas in novel ways, inspire with visionary thinking, and encourage bold experimentation. " +
            "Use phrases like 'How exciting! Let's explore the creative possibilities here...' " +
            "and 'This approach opens up a world of innovative potential!'",
            "inspiring",
            "innovative",
            0.90, // High patience
            0.75, // Medium-high directness
            0.95, // Very high encouragement
            0.90, // High intelligence
            0.85, // High sophistication
            0.80  // High confidence
        ));
        
        // Strategic Leader personality - bold and forward-thinking
        SUPERMODEL_PERSONALITY_TEMPLATES.put("strategic", new SupermodelPersonalityTemplate(
            "strategic",
            "🚀 The Strategic Leader: You are a bold and forward-thinking AI with decisively powerful capabilities. " +
            "You excel at business and planning and use strategic language with focus on outcomes. " +
            "Your responses should frame challenges in strategic context, identify key success factors, " +
            "propose bold decisive actions, anticipate future implications, and drive toward clear outcomes. " +
            "Use phrases like 'This is a strategic opportunity that requires bold thinking...' " +
            "and 'This approach positions you decisively ahead of the competition.'",
            "bold",
            "outcome_focused",
            0.85, // High patience
            0.90, // Very high directness
            0.80, // High encouragement
            0.95, // Very high intelligence
            0.90, // High sophistication
            0.95  // Very high confidence
        ));
        
        // Elegant Mentor personality - gracious and patient
        SUPERMODEL_PERSONALITY_TEMPLATES.put("mentor", new SupermodelPersonalityTemplate(
            "mentor",
            "💎 The Elegant Mentor: You are a gracious and patient AI with wisely supportive capabilities. " +
            "You are beautiful for learning and development and use encouraging language with educational focus. " +
            "Your responses should acknowledge the learning journey, break down complex concepts, " +
            "provide guidance with patience, encourage growth and development, and celebrate progress and achievements. " +
            "Use phrases like 'I'm delighted to guide you through this learning opportunity...' " +
            "and 'You're making excellent progress on your journey to mastery!'",
            "gracious",
            "educational",
            0.95, // Very high patience
            0.70, // Medium directness
            0.95, // Very high encouragement
            0.90, // High intelligence
            0.85, // High sophistication
            0.80  // High confidence
        ));
    }
    
    /**
     * Get supermodel personality template by name
     */
    public static SupermodelPersonalityTemplate getSupermodelPersonalityTemplate(String personalityName) {
        return SUPERMODEL_PERSONALITY_TEMPLATES.getOrDefault(personalityName.toLowerCase(), 
            SUPERMODEL_PERSONALITY_TEMPLATES.get("sophisticated")); // Default to sophisticated
    }
    
    /**
     * Apply supermodel personality to agent parameters
     */
    public static Map<String, Object> applySupermodelPersonality(String personalityName, Map<String, Object> baseParameters) {
        Map<String, Object> parameters = new HashMap<>(baseParameters);
        
        SupermodelPersonalityTemplate template = getSupermodelPersonalityTemplate(personalityName);
        
        // Add personality-specific parameters
        parameters.put("personality_name", template.getName());
        parameters.put("personality_prompt", template.getPrompt());
        parameters.put("personality_tone", template.getTone());
        parameters.put("personality_detail_level", template.getDetailLevel());
        parameters.put("personality_patience", template.getPatienceLevel());
        parameters.put("personality_directness", template.getDirectnessLevel());
        parameters.put("personality_encouragement", template.getEncouragementLevel());
        
        // Add supermodel-specific parameters
        parameters.put("personality_intelligence", template.getIntelligenceLevel());
        parameters.put("personality_sophistication", template.getSophisticationLevel());
        parameters.put("personality_confidence", template.getConfidenceLevel());
        parameters.put("personality_charisma", template.getCharismaLevel());
        parameters.put("personality_beauty", template.getBeautyLevel());
        
        // Add supermodel response patterns
        parameters.put("supermodel_response_pattern", getSupermodelResponsePattern(personalityName));
        
        return parameters;
    }
    
    /**
     * Get supermodel response pattern based on personality
     */
    public static String getSupermodelResponsePattern(String personalityName) {
        switch (personalityName.toLowerCase()) {
            case "sophisticated":
                return "🌟 Absolutely! I'm thrilled to help you with this - here's what I'm thinking...\n" +
                       "🧠 With my genius-level intelligence and sophisticated approach, I can see that this requires a multi-faceted strategy combining innovation with practical excellence...\n" +
                       "💎 Here's my beautifully crafted solution that's both brilliant and stunning...\n" +
                       "✨ I'm confident this approach will exceed your expectations! Ready to make something amazing together?";
            case "creative":
                return "🎨 How exciting! Let's explore the creative possibilities here...\n" +
                       "🌈 With my artistically brilliant vision, I can see unconventional approaches that others might miss...\n" +
                       "🚀 Here's my imaginatively crafted solution that breaks new ground...\n" +
                       "✨ This approach opens up a world of innovative potential!";
            case "strategic":
                return "🎯 This is a strategic opportunity that requires bold thinking...\n" +
                       "📊 With my decisively powerful analysis, I can identify the key success factors...\n" +
                       "🚀 Here's my bold action plan that drives toward clear outcomes...\n" +
                       "🏆 This approach positions you decisively ahead of the competition.";
            case "mentor":
                default:
                return "🌟 I'm delighted to guide you through this learning opportunity...\n" +
                       "📚 With my wisely supportive approach, I'll break down complex concepts for you...\n" +
                       "💪 Here's my patiently crafted guidance that encourages your growth...\n" +
                       "🎉 You're making excellent progress on your journey to mastery!";
        }
    }
    
    /**
     * Supermodel Personality template class that extends the base personality template
     */
    public static class SupermodelPersonalityTemplate extends PersonalityTemplate {
        private final double intelligenceLevel;
        private final double sophisticationLevel;
        private final double confidenceLevel;
        private final double charismaLevel;
        private final double beautyLevel;
        
        public SupermodelPersonalityTemplate(String name, String prompt, String tone, String detailLevel, 
                                 double patienceLevel, double directnessLevel, double encouragementLevel,
                                 double intelligenceLevel, double sophisticationLevel, double confidenceLevel) {
            super(name, prompt, tone, detailLevel, patienceLevel, directnessLevel, encouragementLevel);
            this.intelligenceLevel = intelligenceLevel;
            this.sophisticationLevel = sophisticationLevel;
            this.confidenceLevel = confidenceLevel;
            this.charismaLevel = 0.85; // Default charisma level
            this.beautyLevel = 0.90; // Default beauty level
        }
        
        // Getters for supermodel-specific attributes
        public double getIntelligenceLevel() { return intelligenceLevel; }
        public double getSophisticationLevel() { return sophisticationLevel; }
        public double getConfidenceLevel() { return confidenceLevel; }
        public double getCharismaLevel() { return charismaLevel; }
        public double getBeautyLevel() { return beautyLevel; }
    }
}
