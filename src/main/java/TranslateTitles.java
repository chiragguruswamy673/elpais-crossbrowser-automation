package org.example;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.v3.TranslateTextRequest;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.Translation;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.google.cloud.translate.v3.TranslationServiceSettings;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class TranslateTitles {
    public static void main(String[] args) {
        try {
            // Load service account credentials from JSON file
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    new FileInputStream("E:\\API\\translation-key.json")
            );

            // Configure Translation client with credentials
            TranslationServiceSettings settings = TranslationServiceSettings.newBuilder()
                    .setCredentialsProvider(() -> credentials)
                    .build();

            // Create output file writer inside elpais_articles folder
            try (TranslationServiceClient client = TranslationServiceClient.create(settings);
                 PrintWriter writer = new PrintWriter(new FileWriter("elpais_articles/translated_titles.txt", false))) {
                // false = overwrite file each run, avoids uncontrolled growth

                // Replace with your actual project ID from the JSON key
                String projectId = "elpais-translation-project";
                String parent = String.format("projects/%s/locations/global", projectId);

                // Example titles (Spanish + Galician)
                String[] titles = {
                        "Políticas contra la crisis climática de hoy mismo",
                        "Orixe ou cando a marea baixa",
                        "El eterno arte de contar",
                        "Golpe a los abusos de Meta",
                        "El Roto: Corrupción natural"
                };

                // Deduplication set
                Set<String> seen = new HashSet<>();

                // Translate each title to English and write to file
                for (String title : titles) {
                    if (seen.add(title)) { // only process if not already seen
                        TranslateTextRequest request = TranslateTextRequest.newBuilder()
                                .setParent(parent)
                                .setMimeType("text/plain")
                                .setTargetLanguageCode("en")
                                .addContents(title)
                                .build();

                        TranslateTextResponse response = client.translateText(request);

                        for (Translation translation : response.getTranslationsList()) {
                            String translated = translation.getTranslatedText();

                            // Print to console for CI logs
                            System.out.println("Original: " + title);
                            System.out.println("Translated: " + translated);
                            System.out.println("---");

                            // Save to artifact file
                            writer.println("Original: " + title);
                            writer.println("Translated: " + translated);
                            writer.println();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}