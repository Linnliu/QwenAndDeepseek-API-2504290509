package org.Lin_MidWork;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class APICaller {
    private static final String TONGYI_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    private static final String DEEPSEEK_URL = "https://api.deepseek.com/chat/completions";
    private static final String TONGYI_API_KEY = "sk-xxxxxxxxxx（API访问密钥，请修改后再运行代码）";
    private static final String DEEPSEEK_API_KEY = "sk-xxxxxxxxxx（API访问密钥，请修改后再运行代码）";

    private static final String SYSTEM_PROMPT = "（系统设定关键词，可自行修改）";

    public static String callQwenAPI(String text) {
        return callLLMAPI(TONGYI_URL, "qwen2.5-vl-32b-instruct", TONGYI_API_KEY, text);
    }

    public static String callDeepseekAPI(String text) {
        return callLLMAPI(DEEPSEEK_URL, "deepseek-chat", DEEPSEEK_API_KEY, text);
    }

    private static String callLLMAPI(String apiUrl, String model, String apiKey, String userText) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("model", model);

            JSONArray messages = buildMessages(userText);
            jsonInput.put("messages", messages);

            OutputStream os = conn.getOutputStream();
            os.write(jsonInput.toString().getBytes());
            os.flush();
            os.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            conn.disconnect();
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    private static JSONArray buildMessages(String userText) {
        JSONArray messages = new JSONArray();

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", APICaller.SYSTEM_PROMPT);
        messages.put(systemMessage);

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        JSONArray content = new JSONArray();
        content.put(new JSONObject().put("type", "text").put("text", userText));
        userMessage.put("content", content);
        messages.put(userMessage);

        return messages;
    }
}
