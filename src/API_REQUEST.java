
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
public class API_REQUEST {
    private static final String API_KEY = ConfigLoader.getProperty("api.key");
    private static final String API_URL = ConfigLoader.getProperty("api.url");

    public static String getWordDefinition(String word) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String url = API_URL + word + "?key=" + API_KEY;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
    
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            String responseBody = response.body();
    
            try {
                JSONArray jsonArray = new JSONArray(responseBody);
                if (jsonArray.length() > 0) {
                    JSONObject wordObject = jsonArray.getJSONObject(0);
    
                    // Get Definition
                    String definition = "Definition not found.";
                    if (wordObject.has("shortdef")) {
                        JSONArray shortDefs = wordObject.getJSONArray("shortdef");
                        if (shortDefs.length() > 0) {
                            definition = shortDefs.getString(0); 
                        }
                    }
    
                    // Get Audio File URL
                    String audioUrl = null;
                    if (wordObject.has("hwi")) {
                        JSONObject hwi = wordObject.getJSONObject("hwi");
                        if (hwi.has("prs")) {
                            JSONArray prs = hwi.getJSONArray("prs");
                            if (prs.length() > 0) {
                                JSONObject prsObject = prs.getJSONObject(0);
                                if (prsObject.has("sound")) {
                                    JSONObject sound = prsObject.getJSONObject("sound");
                                    if (sound.has("audio")) {
                                        String audioFileName = sound.getString("audio");
                                        audioUrl = "https://media.merriam-webster.com/audio/prons/en/us/mp3/" +
                                                audioFileName.charAt(0) + "/" + audioFileName + ".mp3";
                                    }
                                }
                            }
                        }
                    }
    
                    // Return both definition and audio URL
                    return definition + "||" + (audioUrl != null ? audioUrl : "No audio");
                }
            } catch (Exception e) {
                System.out.println("Error parsing definition: " + e.getMessage());
            }
        }
        return "Definition not found.||No audio"; 
    }
}
