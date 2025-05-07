package org.Lin_MidWork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.*;

public class QwenGUI extends JFrame {
    private final JTextArea inputTextArea;
    private final JTextArea outputTextArea1;
    private final JTextArea outputTextArea2;
    private final ExecutorService executorService;

    public QwenGUI() {
        setTitle("Qwen&Deepseek Client");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputTextArea = new JTextArea();
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);

        JLabel inputLabel = new JLabel("请提问:");
        inputPanel.add(inputLabel, BorderLayout.NORTH);

        JButton sendButton = new JButton("发送");
        sendButton.addActionListener(new SendButtonListener());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(sendButton);

        inputPanel.add(buttonPanel, BorderLayout.SOUTH);

        outputTextArea1 = new JTextArea();
        outputTextArea1.setEditable(false);
        JScrollPane outputScrollPane1 = new JScrollPane(outputTextArea1);

        outputTextArea2 = new JTextArea();
        outputTextArea2.setEditable(false);
        JScrollPane outputScrollPane2 = new JScrollPane(outputTextArea2);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Qwen", outputScrollPane1);
        tabbedPane.addTab("Deepseek", outputScrollPane2);

        add(inputPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        executorService = Executors.newFixedThreadPool(2);
    }

    private class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String inputText = inputTextArea.getText();
            JButton sendButton = (JButton) e.getSource();
            sendButton.setEnabled(false);

            WaitingDialog waitingDialog = new WaitingDialog(QwenGUI.this, sendButton);

            Future<?> future1 = executorService.submit(() -> {
                if (!waitingDialog.isCanceled()) {
                    String output1 = APICaller.callQwenAPI(inputText);
                    SwingUtilities.invokeLater(() -> {
                        if (!waitingDialog.isCanceled()) {
                            DataProcessor.extractAndDisplayContent(output1, outputTextArea1);
                            DataProcessor.saveToJson(inputText, output1, "Qwen");
                        }
                        sendButton.setEnabled(true);
                        waitingDialog.dispose();
                    });
                } else {
                    sendButton.setEnabled(true);
                }
            });

            Future<?> future2 = executorService.submit(() -> {
                if (!waitingDialog.isCanceled()) {
                    String output2 = APICaller.callDeepseekAPI(inputText);
                    SwingUtilities.invokeLater(() -> {
                        if (!waitingDialog.isCanceled()) {
                            DataProcessor.extractAndDisplayContent(output2, outputTextArea2);
                            DataProcessor.saveToJson(inputText, output2, "Deepseek");
                        }
                    });
                }
            });
        }
    }

    static class WaitingDialog extends JDialog {
        private volatile boolean canceled = false;

        public WaitingDialog(JFrame parent, JButton sendButton) {
            super(parent, "请稍候", false);
            setSize(300, 100);
            setLocationRelativeTo(parent);
            setLayout(new FlowLayout());

            JLabel label = new JLabel("正在等待模型返回数据...");
            JButton cancelButton = new JButton("取消");

            cancelButton.addActionListener(e -> {
                canceled = true;
                dispose();
                sendButton.setEnabled(true);
            });

            add(label);
            add(cancelButton);
            setVisible(true);
        }

        public boolean isCanceled() {
            return canceled;
        }
    }
}
