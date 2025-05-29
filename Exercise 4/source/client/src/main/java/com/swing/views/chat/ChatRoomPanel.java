package com.swing.views.chat;

import com.swing.callers.ChatRoomCaller;
import com.swing.callers.MessageCaller;
import com.swing.context.ApplicationContext;
import com.swing.context.AuthContext;
import com.swing.event.MessageObserver;
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
    protected transient Map<String, ChatRoom.Member> memberMap;
    private Map<String, MessagePanel> messagePanelMap;

    protected final transient ChatRoomCaller chatRoomCaller;
    protected final transient MessageCaller messageCaller;
    protected final transient MessageObserver messageObserver;

    protected ChatRoomPanel(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
        this.chatRoomCaller = ApplicationContext.getInstance().getChatRoomCaller();
        this.messageCaller = ApplicationContext.getInstance().getMessageCaller();
        render();
        this.messageObserver = new MessageObserver(this.chatRoom.getId());
        this.messageObserver.addReceivedMessageConsumer(this::handleReceiveMessage);
        this.messageObserver.addUpdatedMessageConsumer(this::handleUpdatedMessage);
        this.messageObserver.addDeletedMessageConsumer(this::handleDeletedMessage);
        ApplicationContext.getInstance().getEventDispatcher().addObserver(messageObserver);
    }

    public void render() {
        this.messagePanelMap = new HashMap<>();
        this.memberMap = new HashMap<>();
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
        var result1 = this.chatRoomCaller.getChatRoomMembers(GetChatRoomMembersInput.builder()
                .chatRoomId(chatRoomId)
                .build());
        if (result1.isFailure()) {
            log.warning("ChatRoomPanel::render: " + result1.getException().getMessage());
            return;
        }
        Output<GetChatRoomMembersOutput> output1 = result1.getValue();
        if (output1.getError() != null) {
            log.warning("ChatRoomPanel::render: " + output1.getError().getMessage());
            return;
        }
        GetChatRoomMembersOutput getChatRoomMembersOutput = output1.getBody();
        for (GetChatRoomMembersOutput.Item item : getChatRoomMembersOutput.getItems()) {
            memberMap.put(item.getUserId(), ChatRoom.Member.builder()
                            .id(item.getUserId())
                            .nickname(item.getNickname())
                            .username(item.getUsername())
                    .build());
        }
        var result2 = this.messageCaller.getMany(GetMessagesInput.builder().chatRoomId(chatRoomId).limit(100).page(0).build());
        if (result2.isFailure()) {
            log.warning("ChatRoomPanel::render: " + result2.getException().getMessage());
            return;
        }
        Output<GetMessagesOutput> output2 = result2.getValue();
        if (output2.getError() != null) {
            log.warning("ChatRoomPanel::render: " + output2.getError().getMessage());
            return;
        }
        for (int i = output2.getBody().getItems().size() - 1; i >= 0; i--) {
            GetMessagesOutput.Item item = output2.getBody().getItems().get(i);
            ChatRoom.Member sender = memberMap.get(item.getSenderId());
            Message message = Message.builder()
                    .id(item.getMessageId())
                    .chatRoomId(item.getChatRoomId())
                    .content(MessageContentMapper.fromIOToModel(item.getContent()))
                    .senderId(sender.getId())
                    .senderName(sender.getNickname())
                    .isGroup(chatRoom.isGroup())
                    .receiverIds(receiverIds)
                    .build();
            appendMessage(message);
        }

    }

    public void handleReceiveMessage(Message message) {
        if (Objects.equals(message.getSenderId(), AuthContext.INSTANCE.getPrincipal().getUserId())) return;
        if (messagePanelMap.containsKey(message.getId())) return;
        message.setSenderName(memberMap.get(message.getSenderId()).getNickname());
        message.setGroup(chatRoom.isGroup());
        appendMessage(message);
    }

    public void handleUpdatedMessage(Message message) {
        if (Objects.equals(message.getSenderId(), AuthContext.INSTANCE.getPrincipal().getUserId())) return;
        MessagePanel panel = messagePanelMap.get(message.getId());
        if (panel != null) {
            SwingUtilities.invokeLater(() -> {
                message.setEdited(true);
                message.setGroup(chatRoom.isGroup());
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
                message.setGroup(chatRoom.isGroup());
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
                    .isGroup(chatRoom.isGroup())
                    .build();
            appendMessage(message);
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
                ChatRoom.Member sender = memberMap.get(AuthContext.INSTANCE.getPrincipal().getUserId());
                Message message = Message.builder()
                        .id(createMessageOutput.getMessageId())
                        .chatRoomId(chatRoom.getId())
                        .senderId(sender.getId())
                        .receiverIds(receiverIds)
                        .senderName(sender.getNickname())
                        .content(content)
                        .isGroup(chatRoom.isGroup())
                        .build();
                appendMessage(message);
                messageField.setText("");
            }
        } catch (IOException e) {
            log.warning("ChatRoomPanel::sendFile: " + e.getMessage());
        }
    }

    protected void appendMessage(Message message) {
        MessagePanel messagePanel = new MessagePanel(message);
        int preferredHeight = messagePanel.getPreferredSize().height;
        messagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, preferredHeight));
        messagePanelMap.put(message.getId(), messagePanel);
        chatArea.add(messagePanel);
        chatArea.revalidate();
        chatArea.repaint();
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

        private void render() {
            value = message.getContent().getValue();
            if (message.getContent().getType().equals(Message.Content.Type.FILE)) {
                Path path = Paths.get(value);
                String fileName = path.getFileName().toString();
                value = fileName.substring(fileName.indexOf("_") + 1);
            }
            boolean isSelf = message.getSenderId().equals(AuthContext.INSTANCE.getPrincipal().getUserId());
            setLayout(new FlowLayout(isSelf ? FlowLayout.RIGHT : FlowLayout.LEFT));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            JPanel messageContainer = new JPanel();
            messageContainer.setLayout(new BoxLayout(messageContainer, BoxLayout.Y_AXIS));
            if (!isSelf && message.isGroup()) {
                JLabel sender = new JLabel(message.getSenderName());
                sender.setFont(sender.getFont().deriveFont(Font.BOLD, 11f));
                sender.setForeground(Color.DARK_GRAY);
                sender.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
                JPanel senderWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                senderWrapper.setOpaque(false);
                senderWrapper.add(sender);
                messageContainer.add(senderWrapper);

            }

            JPanel messageContent = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            messageContent.setOpaque(false);
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
                editButton.addActionListener(e -> handleEditButton());
                deleteButton.addActionListener(e -> handleDeleteButton());
                if (!message.isDeleted()) {
                    if (message.getContent().getType().equals(Message.Content.Type.TEXT)) {
                        messageContent.add(editButton);
                    }
                    messageContent.add(deleteButton);
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
            messageContent.add(messageLabel);
            if (message.isEdited()) {
                JLabel editedLabel = new JLabel("Edited");
                editedLabel.setOpaque(false);
                editedLabel.setFocusable(false);
                messageContent.add(editedLabel);
            }
            messageContainer.add(messageContent);
            add(messageContainer);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    handleMouseClicked();
                }
            });
        }

        private void handleEditButton() {
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
        }

        private void handleDeleteButton() {
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
        }

        private void handleMouseClicked() {
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