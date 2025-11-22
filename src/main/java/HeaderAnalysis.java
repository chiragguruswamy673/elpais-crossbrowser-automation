import java.util.*;

public class HeaderAnalysis {
    public static void main(String[] args) {
        // Example translated headers from TranslateTitles output
        String[] translatedHeaders = {
                "Policies against the climate crisis today",
                "Origin or when the tide goes out",
                "The eternal art of storytelling",
                "A blow to Meta's abuses",
                "The Broken One Natural Corruption"
        };

        Map<String, Integer> wordCount = new HashMap<>();

        for (String header : translatedHeaders) {
            // Normalize: lowercase and remove punctuation
            String cleaned = header.replaceAll("[^a-zA-Z ]", "").toLowerCase();
            String[] words = cleaned.split("\\s+");

            for (String word : words) {
                if (!word.isEmpty()) {
                    wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
                }
            }
        }

        // Print words repeated more than twice
        System.out.println("Repeated words across headers:");
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            if (entry.getValue() > 2) {
                System.out.println(entry.getKey() + " → " + entry.getValue());
            }
        }
    }
}