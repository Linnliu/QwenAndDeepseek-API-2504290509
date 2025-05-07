package org.Lin_MidWork;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataProcessor {
    public static void extractAndDisplayContent(String output, JTextArea outputTextArea) {
        try {
            JSONObject jsonResponse = new JSONObject(output);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (!choices.isEmpty()) {
                JSONObject message = choices.getJSONObject(0).getJSONObject("message");
                String content = message.getString("content");
                outputTextArea.setText(content);
            } else {
                outputTextArea.setText("null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            outputTextArea.setText("Error extracting content: " + e.getMessage());
        }
    }

    public static void saveToJson(String inputText, String output, String model) {
        try {
            JSONObject json = new JSONObject();
            json.put("inputText", inputText);
            json.put("output", output);
            json.put("model", model);

            String timeStamp = new SimpleDateFormat("MMddHHmmss").format(new Date());
            File dir = new File("QwenBuffer");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, timeStamp + "_" + model + ".json");

            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(json.toString(4));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
