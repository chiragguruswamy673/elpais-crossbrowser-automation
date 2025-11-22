import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.v3.TranslateTextRequest;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.Translation;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.google.cloud.translate.v3.TranslationServiceSettings;

import java.io.FileInputStream;
import java.io.IOException;

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

            try (TranslationServiceClient client = TranslationServiceClient.create(settings)) {
                // Read the actual project_id from your JSON file and paste it here
                String projectId = "elpais-translation-project"; // e.g. "my-project-123456"
                String parent = String.format("projects/%s/locations/global", projectId);

                // Example titles (Spanish + Galician)
                String[] titles = {
                        "Políticas contra la crisis climática de hoy mismo",
                        "Orixe ou cando a marea baixa",
                        "El eterno arte de contar",
                        "Golpe a los abusos de Meta",
                        "El Roto: Corrupción natural"
                };

                // Translate each title to English
                for (String title : titles) {
                    TranslateTextRequest request = TranslateTextRequest.newBuilder()
                            .setParent(parent)
                            .setMimeType("text/plain")
                            // Let API auto-detect source language
                            .setTargetLanguageCode("en")
                            .addContents(title)
                            .build();

                    TranslateTextResponse response = client.translateText(request);

                    for (Translation translation : response.getTranslationsList()) {
                        System.out.println("Original: " + title);
                        System.out.println("Translated: " + translation.getTranslatedText());
                        System.out.println("---");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}