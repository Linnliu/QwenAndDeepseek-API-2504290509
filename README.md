# Qwen & DeepSeek-Client 代码文档

---

## 一、介绍

**注意：** 本仓库内为九江学院-计算机与大数据科学学院-软件工程专业-刘林的2025上半学期的java期中作业。应课程要求公开，仅供学习与交流使用，不做他用。
仓库内包含项目代码（Word）、演示稿（PPT）和源代码（由于API密钥具有私密性，不便公开，请自行修改代码以运行，或联系我获取）。

本项目是一个基于 Java Swing 的轻量级客户端应用，使用 Maven 管理依赖，封装了阿里云 Qwen 和 DeepSeek 的大模型 API，实现了并发调用、结果展示与持久化存储。

---

## 二、项目结构概览

本项目由以下主要 Java 类组成：

| 类名 | 功能 |
|------|------|
| APICaller.java | 负责调用 Qwen 和 DeepSeek 的大语言模型 API。 |
| QwenGUI.java | 图形用户界面，提供输入框、按钮和输出区域用于交互。 |
| DataProcessor.java | 处理 API 返回结果，提取内容并保存为 JSON 文件。 |
| Main.java | 程序入口，启动图形界面应用。 |

---

## 三、类详解

### **1. APICaller**

#### 所属包
```java
package org.Lin_MidWork;
```


#### 引入依赖
- `org.json.JSONArray`, `org.json.JSONObject`: 构造请求体与解析响应。
- `HttpURLConnection`, `URL`, `BufferedReader`, `OutputStream`: 实现 HTTP 请求。

#### 成员变量（常量）

| 变量名 | 类型 | 描述 |
|--------|------|------|
| TONGYI_URL | `String` | 阿里云 Qwen 模型的 API 地址。 |
| DEEPSEEK_URL | `String` | DeepSeek 模型的 API 地址。 |
| TONGYI_API_KEY | `String` | 访问 Qwen API 所需的密钥。 |
| DEEPSEEK_API_KEY | `String` | 访问 DeepSeek API 所需的密钥。 |
| SYSTEM_PROMPT | `String` | 系统指令，要求模型使用中文回答问题，并只返回运算结果。 |

#### 方法说明

##### `public static String callQwenAPI(String text)`
- **功能**：调用 Qwen 模型处理用户输入文本。
- **参数**：
    - `text`: 用户输入的问题。
- **返回值**：API 响应字符串。
- **调用方法**：内部调用 [callLLMAPI()] 方法。

##### `public static String callDeepseekAPI(String text)`
- **功能**：调用 DeepSeek 模型处理用户输入文本。
- **参数**：
    - `text`: 用户输入的问题。
- **返回值**：API 响应字符串。
- **调用方法**：内部调用 [callLLMAPI()] 方法。

##### `private static String callLLMAPI(String apiUrl, String model, String apiKey, String userText)`
- **功能**：向指定 URL 发送 POST 请求，调用 LLM 接口。
- **参数**：
    - `apiUrl`: API 地址。
    - `model`: 模型名称。
    - `apiKey`: 访问密钥。
    - `userText`: 用户输入的文本。
- **返回值**：API 返回的完整响应字符串。
- **异常处理**：捕获 IO 异常并返回错误信息。
- **流程说明**：
    1. 创建 HTTP 连接；
    2. 设置请求头（授权、内容类型）；
    3. 构建 JSON 请求体；
    4. 发送请求并读取响应；
    5. 返回响应字符串。

##### `private static JSONArray buildMessages(String userText)`
- **功能**：构建符合 API 格式的对话历史消息数组。
- **参数**：
    - `systemPrompt`: 系统提示词；
    - `userText`: 用户输入内容。
- **返回值**：包含系统提示和用户输入的 `JSONArray`。
- **构造格式示例**：
```json
[
  {
    "role": "system",
    "content": "（由代码段设定的系统提示）"
  },
  {
    "role": "user",
    "content": [
      {
        "type": "text",
        "text": "用户提问内容"
      }
    ]
  }
]
```


---

### **2. QwenGUI**

#### 所属包
```java
package org.Lin_MidWork;
```


#### 引入依赖
- Swing 组件库：用于构建 GUI。
- 并发工具：`ExecutorService` 用于异步调用 API。

#### 成员变量

| 变量名 | 类型 | 描述 |
|--------|------|------|
| inputTextArea | `JTextArea` | 用户输入区域。 |
| sendButton | `JButton` | 提交按钮。 |
| outputTextArea1 | `JTextArea` | 显示 Qwen 回答结果。 |
| outputTextArea2 | `JTextArea` | 显示 DeepSeek 回答结果。 |
| executorService | `ExecutorService` | 线程池，用于并发执行 API 请求任务。 |

#### 构造函数 `public QwenGUI()`
- **功能**：初始化窗口界面。
- **布局说明**：
    - 输入区域在顶部；
    - 输出区域以标签页形式展示两个模型的回答；
    - 使用线程池提高响应效率。

#### 内部类 `private class SendButtonListener implements ActionListener`

##### `public void actionPerformed(ActionEvent e)`
- **功能**：当点击发送按钮时触发该方法。
- **流程说明**：
    1. 获取用户输入文本；
    2. 启动两个线程分别调用 Qwen 和 DeepSeek API；
    3. 使用 `SwingUtilities.invokeLater()` 更新 UI；
    4. 调用 [DataProcessor.extractAndDisplayContent()] 展示结果；
    5. 调用 [DataProcessor.saveToJson()] 保存记录。

---

### **3. DataProcessor**

#### 所属包
```java
package org.Lin_MidWork;
```


#### 引入依赖
- JSON 工具类；
- IO 操作类；
- 时间日期格式化类。

#### 方法说明

##### `public static void extractAndDisplayContent(String output, JTextArea outputTextArea)`
- **功能**：从 API 返回的 JSON 中提取 `content` 字段并显示在文本框中。
- **参数**：
    - `output`: API 原始响应字符串；
    - `outputTextArea`: 要更新的文本框。
- **异常处理**：捕获解析异常并显示错误信息。

##### `public static void saveToJson(String inputText, String output, String model)`
- **功能**：将问答对保存为 JSON 文件。
- **参数**：
    - `inputText`: 用户输入；
    - `output`: API 返回结果；
    - `model`: 使用的模型名称。
- **流程说明**：
    1. 构建 JSON 对象；
    2. 生成时间戳作为文件名前缀；
    3. 创建目录 `QwenBuffer`（如不存在）；
    4. 写入文件。

---

### **4. Main**

#### 所属包
```java
package org.Lin_MidWork;
```


#### 方法说明

##### `public static void main(String[] args)`
- **功能**：程序入口点，启动图形界面。
- **实现方式**：通过Main类的[main()]方法创建并显示窗口。
- **说明**：确保 GUI 在事件调度线程中创建和显示。

---

## 三、流程描述

### **1. 调用链路**
```
+-------------------+
|   用户输入问题    |
| (QwenGUI.java)    |
+-------------------+
           |
           v
+-------------------+
|  点击“发送”按钮   |
| (SendButtonListener)|
+-------------------+
           |
           v
+-----------------------+
| 提交任务到线程池      |
| (ExecutorService)     |
+-----------------------+
           |
     +-----+------+
     |            |
     v            v
+--------+   +----------+
| QwenAPI|   | DeepseekAPI|
|调用API |   |调用API   |
|(callQwenAPI)|(callDeepseekAPI)|
+--------+   +----------+
     |            |
     v            v
+---------------------------------+
| 构建请求消息体                  |
| (buildMessages)                 |
+---------------------------------+
           |
           v
+---------------------------------+
| 发送 HTTP 请求并获取响应        |
| (callLLMAPI)                    |
+---------------------------------+
           |
           v
+---------------------------------+
| 解析响应并更新UI                |
| (extractAndDisplayContent)      |
+---------------------------------+
           |
           v
+---------------------------------+
| 保存响应数据到 JSON 文件        |
| (saveToJson)                    |
+---------------------------------+
           |
           v
+-------------------+
| 显示输出在 JTextArea |
+-------------------+
```


### **2. 数据流分析**

| 来源 | 目标 | 数据内容 | 类型 |
|------|------|----------|------|
| 用户输入 | `inputTextArea.getText()` | 提问内容 | `String` |
| `APICaller.callLLMAPI(...)` | `DataProcessor.extractAndDisplayContent(...)` | API 响应 JSON | `String` |
| `DataProcessor.saveToJson(...)` | `QwenBuffer/` 目录下的 `.json` 文件 | 问答记录 | `JSON` |