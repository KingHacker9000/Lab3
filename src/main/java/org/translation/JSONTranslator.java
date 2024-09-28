package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    private final Map<String, Map<String, String>> translationsByCountry = new HashMap<>();

    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        // read the file to get the data to populate things...
        try {

            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));

            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String countryCode = jsonObject.getString("alpha3");
                Map<String, String> translations = new HashMap<>();

                // Where languages start
                final int x = 3;
                Iterator<String> keys = jsonObject.keys();

                while (keys.hasNext()) {
                    String language = keys.next();
                    if ("id".equals(language) || "alpha2".equals(language) || "alpha3".equals(language)) {
                        continue;
                    }
                    String translation = jsonObject.getString(language);

                    translations.put(language, translation);
                }

                translationsByCountry.put(countryCode, translations);
            }

        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        return new ArrayList<>(translationsByCountry.get(country).keySet());
    }

    @Override
    public List<String> getCountries() {
        return new ArrayList<>(translationsByCountry.keySet());
    }

    @Override
    public String translate(String country, String language) {
        return translationsByCountry.get(country).get(language);
    }
}
