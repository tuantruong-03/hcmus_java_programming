package com.swing.views.chat;

import com.swing.callers.ChatRoomCaller;
import com.swing.callers.MessageCaller;
import com.swing.context.ApplicationContext;
import com.swing.context.AuthContext;
import com.swing.event.MessageObserver;
import com.swing.event.ObserverName;
import com.swing.io.Output;
import com.swing.io.chatroom.*;
import com.swing.io.message.*;
import com.swing.mapper.MessageContentMapper;
import com.swing.models.ChatRoom;
import com.swing.models.Message;
import lombok.Getter;
import lombok.extern.java.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

@Log
public abstract class ChatRoomPanel extends JPanel {
    protected JPanel chatArea;
    protected JTextField messageField;
    protected JButton sendButton;
    protected JButton sendFileButton;
    @Getter
    protected transient ChatRoom chatRoom;
    protected transient List<String> receiverIds;
    private final Map<String, MessagePanel> messagePanelMap;

    protected final transient ChatRoomCaller chatRoomCaller;
    protected final transient MessageCaller messageCaller;
    protected final transient MessageObserver messageObserver;

    protected ChatRoomPanel(ChatRoom chatRoom) {
        this.messagePanelMap = new HashMap<>();
        this.chatRoom = chatRoom;
        this.chatRoomCaller = ApplicationContext.getInstance().getChatRoomCaller();
        this.messageCaller = ApplicationContext.getInstance().getMessageCaller();
        render();
        this.messageObserver = new MessageObserver(ObserverName.MessageObserver);
        this.messageObserver.addReceivedMessageConsumer(this::handleReceiveMessage);
        this.messageObserver.addUpdatedMessageConsumer(this::handleUpdatedMessage);
        this.messageObserver.addDeletedMessageConsumer(this::handleDeletedMessage);
        ApplicationContext.getInstance().getEventDispatcher().addObserver(messageObserver);
    }

    public void render() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Chat Area"));
        chatArea = new JPanel();
        chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
        messageField = new JTextField();
        sendButton = new JButton("Send");
        sendFileButton = new JButton("Send File");
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.add(sendFileButton, BorderLayout.WEST);
        add(inputPanel, BorderLayout.SOUTH);
        sendButton.addActionListener(e -> sendMessage());
        sendFileButton.addActionListener(e -> sendFile());

        String chatRoomId = chatRoom.getId();
        if (chatRoom.isNew()) {
            List<String> userIds = chatRoom.getUserIds();
            List<String> otherUserIds = userIds.stream().
                    filter(userId -> !userId.equals(AuthContext.INSTANCE.getPrincipal().getUserId())).toList();
            var inputResult = CreateChatRoomInput.builder()
                    .otherUserIds(otherUserIds.stream().toList().toArray(new String[0]))
                    .isGroup(false)
                    .build();
            if (inputResult.isFailure()) {
                log.warning("ChatRoomPanel::renderWithData: " + inputResult.getException().getMessage());
                return;
            }
            var result = chatRoomCaller.createOne(inputResult.getValue());
            if (result.isFailure()) {
                log.warning("ChatRoomPanel::renderWithData: " + result.getException().getMessage());
                return;
            }
            Output<CreateChatRoomOutput> output = result.getValue();
            if (output.getError() != null) {
                log.warning("ChatRoomPanel::renderWithData: " + result.getException().getMessage());
                return;
            }
            chatRoomId = output.getBody().getChatRoomId();
        }
        // Fetch full data of chatRoom
        GetChatRoomInput input = GetChatRoomInput.builder()
                .chatRoomId(chatRoomId)
                .build();
        var result = chatRoomCaller.getChatRoom(input);
        if (result.isFailure()) {
            log.warning("MainChatPanels::render: " + result.getException().getMessage());
            return;
        }
        GetChatRoomOutput output = result.getValue().getBody();
        Collection<String> memberIds = output.getMembers().keySet();
        chatRoom.setUserIds(memberIds.stream().toList());
        receiverIds = memberIds.stream()
                .filter(userId -> !userId.equals(AuthContext.INSTANCE.getPrincipal().getUserId())).
                toList();
        var result1 = this.messageCaller.getMany(GetMessagesInput.builder().chatRoomId(chatRoomId).limit(100).page(0).build());
        if (result1.isFailure()) {
            log.warning("ChatRoomPanel::render: " + result1.getException().getMessage());
            return;
        }
        Output<GetMessagesOutput> output1 = result1.getValue();
        if (output1.getError() != null) {
            log.warning("ChatRoomPanel::render: " + result1.getException().getMessage());
            return;
        }
        for (int i = output1.getBody().getItems().size() - 1; i >= 0; i--) {
            GetMessagesOutput.Item item = output1.getBody().getItems().get(i);
            Message message = Message.builder()
                    .id(item.getMessageId())
                    .chatRoomId(item.getChatRoomId())
                    .content(MessageContentMapper.fromIOToModel(item.getContent()))
                    .senderId(item.getSenderId())
                    .receiverIds(receiverIds)
                    .build();
            MessagePanel messagePanel = new MessagePanel(message);
            appendMessage(messagePanel);
        }

    }

    public void handleReceiveMessage(Message message) {
        if (Objects.equals(message.getSenderId(), AuthContext.INSTANCE.getPrincipal().getUserId())) return;
        MessagePanel messagePanel = new MessagePanel(message);
        appendMessage(messagePanel);
    }

    public void handleUpdatedMessage(Message message) {
        if (Objects.equals(message.getSenderId(), AuthContext.INSTANCE.getPrincipal().getUserId())) return;
        MessagePanel panel = messagePanelMap.get(message.getId());
        if (panel != null) {
            SwingUtilities.invokeLater(() -> {
                message.setEdited(true);
                panel.updateContent(message); // We'll add this method in MessagePanel
                chatArea.revalidate();
                chatArea.repaint();
            });
        }
    }

    public void handleDeletedMessage(Message message) {
        if (Objects.equals(message.getSenderId(), AuthContext.INSTANCE.getPrincipal().getUserId())) return;
        MessagePanel panel = messagePanelMap.get(message.getId());
        messagePanelMap.remove(message.getId());
        if (panel != null) {
            SwingUtilities.invokeLater(() -> {
                message.setContent(Message.Content.builder()
                        .type(Message.Content.Type.TEXT)
                        .value("<This message was deleted>")
                        .build());
                message.setDeleted(true);
                panel.updateContent(message); // We'll add this method in MessagePanel
                chatArea.revalidate();
                chatArea.repaint();
            });
        }
    }

    public void sendMessage() {
        String text = messageField.getText().trim();
        if (!text.isEmpty()) {
            Message.Content content = Message.Content.builder()
                    .type(Message.Content.Type.TEXT)
                    .value(text)
                    .build();
            var result = messageCaller.send(CreateMessageInput.builder()
                    .chatRoomId(chatRoom.getId())
                    .senderId(AuthContext.INSTANCE.getPrincipal().getUserId())
                    .receiverIds(receiverIds)
                    .content(MessageContentMapper.fromModelToIO(content))
                    .build());
            if (result.isFailure()) {
                log.warning("ChatRoomPanel::sendMessage: " + result.getException().getMessage());
                return;
            }
            var output = result.getValue();
            if (output.getError() != null) {
                log.warning("ChatRoomPanel::sendMessage: " + result.getException().getMessage());
                return;
            }
            CreateMessageOutput createMessageOutput = output.getBody();
            Message message = Message.builder()
                    .id(createMessageOutput.getMessageId())
                    .chatRoomId(chatRoom.getId())
                    .senderId(AuthContext.INSTANCE.getPrincipal().getUserId())
                    .receiverIds(receiverIds)
                    .content(content)
                    .build();
            MessagePanel messagePanel = new MessagePanel(message);
            appendMessage(messagePanel);
            messageField.setText("");
        }
    }


    private void sendFile() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select a file to send");
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                Message.Content content = Message.Content.builder()
                        .type(Message.Content.Type.FILE)
                        .value(selectedFile.getName())
                        .build();
                Content inputContent = Content.builder()
                        .type(Content.Type.FILE)
                        .fileName(selectedFile.getName())
                        .fileData(Files.readAllBytes(selectedFile.toPath()))
                        .build();
                var result2 = messageCaller.send(CreateMessageInput.builder()
                        .chatRoomId(chatRoom.getId())
                        .senderId(AuthContext.INSTANCE.getPrincipal().getUserId())
                        .receiverIds(receiverIds)
                        .content(inputContent)
                        .build());
                if (result2.isFailure()) {
                    log.warning("ChatRoomPanel::sendMessage: " + result2.getException().getMessage());
                    return;
                }
                var output = result2.getValue();
                if (output.getError() != null) {
                    log.warning("ChatRoomPanel::sendMessage: " + output.getError().getMessage());
                    return;
                }
                CreateMessageOutput createMessageOutput = output.getBody();
                Message message = Message.builder()
                        .id(createMessageOutput.getMessageId())
                        .chatRoomId(chatRoom.getId())
                        .senderId(AuthContext.INSTANCE.getPrincipal().getUserId())
                        .receiverIds(receiverIds)
                        .content(content)
                        .build();
                MessagePanel messagePanel = new MessagePanel(message);
                appendMessage(messagePanel);
                messageField.setText("");
            }
        } catch (IOException e) {
            log.warning("ChatRoomPanel::sendFile: " + e.getMessage());
        }
    }

    protected void appendMessage(MessagePanel messagePanel) {
        String messageId = messagePanel.getMessage().getId();
        messagePanelMap.put(messageId, messagePanel);
        chatArea.add(messagePanel);
        chatArea.revalidate();
        chatArea.repaint();
    }

    public String getChatRoomName() {
        return chatRoom.getName();
    }

    @Getter
    public static class MessagePanel extends JPanel {
        private final transient Message message;
        private final transient MessageCaller messageCaller;
        private String value;

        public MessagePanel(Message message) {
            this.messageCaller = ApplicationContext.getInstance().getMessageCaller();
            this.message = message;
            render();
        }

        public void render() {
            value = message.getContent().getValue();
            if (message.getContent().getType().equals(Message.Content.Type.FILE)) {
                Path path = Paths.get(value);
                String fileName = path.getFileName().toString();
                value = fileName.substring(fileName.indexOf("_") + 1);
            }
            boolean isSelf = message.getSenderId().equals(AuthContext.INSTANCE.getPrincipal().getUserId());
            setLayout(new FlowLayout(isSelf ? FlowLayout.RIGHT : FlowLayout.LEFT));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            JPanel messageContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            messageContainer.setOpaque(false);
            if (isSelf) {
                JButton editButton = new JButton("🖋️");
                JButton deleteButton = new JButton("🗑️");
                for (JButton button : new JButton[]{editButton, deleteButton}) {
                    button.setContentAreaFilled(false);
                    button.setBorderPainted(false);
                    button.setFocusPainted(false);
                    button.setOpaque(false);
                    button.setMargin(new Insets(2, 0, 0, 1));
                }

                editButton.addActionListener(e -> {
                    String newValue = JOptionPane.showInputDialog(this, "Edit your message:", value);
                    if (newValue != null && !newValue.trim().isEmpty() && !newValue.equals(value)) {
                        Content content = Content.builder()
                                .text(newValue)
                                .type(Content.Type.TEXT)
                                .build();
                        var result = this.messageCaller.update(UpdateMessageInput.builder()
                                        .content(content)
                                        .chatRoomId(message.getChatRoomId())
                                        .messageId(message.getId())
                                        .senderId(message.getSenderId())
                                        .receiverIds(message.getReceiverIds())
                                .build());
                        if (result.isFailure()) {
                            log.warning("MessagePanel::editButtonActionPerformed: " + result.getException().getMessage());
                            return;
                        }
                        var output = result.getValue();
                        if (output.getError() != null) {
                            log.warning("MessagePanel::editButtonActionPerformed: " + output.getError().getMessage());
                            return;
                        }
                        this.message.setContent(Message.Content.builder().value(newValue).type(Message.Content.Type.TEXT).build());
                        rerender();
                    }
                });
                deleteButton.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this message?",
                            "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        var result = this.messageCaller.delete(DeleteMessageInput.builder()
                                        .messageId(message.getId())
                                .chatRoomId(message.getChatRoomId())
                                .senderId(message.getSenderId())
                                .receiverIds(message.getReceiverIds()).build());
                        if (result.isFailure()) {
                            log.warning("MessagePanel::editButtonActionPerformed: " + result.getException().getMessage());
                            return;
                        }
                        var output = result.getValue();
                        if (output.getError() != null) {
                            log.warning("MessagePanel::editButtonActionPerformed: " + output.getError().getMessage());
                        }
                       this.message.setContent(Message.Content.builder()
                                .type(Message.Content.Type.TEXT)
                                .value("<This message was deleted>")
                                .build());
                        this.message.setDeleted(true);
                        rerender();
                    }
                });
                if (!message.isDeleted()) {
                    messageContainer.add(editButton);
                    messageContainer.add(deleteButton);
                }
            }
            JLabel messageLabel = new JLabel(value);
            messageLabel.setOpaque(false);
            messageLabel.setFocusable(false);
            messageLabel.setForeground(Color.BLACK);
            messageLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY, 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            messageContainer.add(messageLabel);
            if (message.isEdited()) {
                JLabel editedLabel = new JLabel("Edited");
                editedLabel.setOpaque(false);
                editedLabel.setFocusable(false);
                messageContainer.add(editedLabel);
            }

            add(messageContainer);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    if (message.getContent().getType().equals(Message.Content.Type.TEXT)) {
                        return;
                    }
                    var result = messageCaller.getOne(GetMessageInput.builder()
                            .messageId(message.getId())
                            .chatRoomId(message.getChatRoomId())
                            .build());
                    if (result.isFailure()) {
                        log.warning("MessagePanel::mouseClicked: " + result.getException().getMessage());
                        return;
                    }
                    var output = result.getValue();
                    if (output.getError() != null) {
                        log.warning("MessagePanel::mouseClicked: " + output.getError().getMessage());
                        return;
                    }
                    Content content = output.getBody().getContent();
                    String fileName = content.getFileName();
                    byte[] fileData = content.getFileData();
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setSelectedFile(new File(fileName));
                    int userSelection = fileChooser.showSaveDialog(MessagePanel.this);

                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File saveFile = fileChooser.getSelectedFile();
                        try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                            fos.write(fileData);
                            JOptionPane.showMessageDialog(MessagePanel.this,
                                    "File saved to:\n" + saveFile.getAbsolutePath(),
                                    "Download Complete",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(MessagePanel.this,
                                    "Failed to save file: " + ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
        }

        public void updateContent(Message message) {
            this.message.setContent(message.getContent());
            this.message.setEdited(message.isEdited());
            this.message.setDeleted(message.isDeleted());
            rerender();
        }

        public void rerender() {
            removeAll();
            render();
            revalidate();
            repaint();
        }
    }
}