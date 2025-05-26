package com.swing.views.chat;

import com.swing.callers.ChatRoomCaller;
import com.swing.callers.MessageCaller;
import com.swing.context.ApplicationContext;
import com.swing.context.AuthContext;
import com.swing.event.MessageObserver;
import com.swing.event.ObserverName;
import com.swing.io.Output;
import com.swing.io.chatroom.*;
import com.swing.io.message.CreateMessageInput;
import com.swing.io.message.CreateMessageOutput;
import com.swing.io.message.GetMessagesInput;
import com.swing.io.message.GetMessagesOutput;
import com.swing.mapper.MessageContentMapper;
import com.swing.models.ChatRoom;
import com.swing.models.Message;
import lombok.Getter;
import lombok.extern.java.Log;

import javax.swing.*;
import java.awt.*;
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
//        sendFileButton.addActionListener(e -> sendFile());

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
        var result1 = this.messageCaller.getMessages(GetMessagesInput.builder().chatRoomId(chatRoomId).limit(100).page(0).build());
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
                    .build();
            MessagePanel messagePanel = new MessagePanel(message);
            appendMessage(messagePanel);
        }

    }

    public void handleReceiveMessage(Message message) {
        MessagePanel messagePanel = new MessagePanel(message);
        appendMessage(messagePanel);
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


//    private void sendFile() {
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Select a file to send");
//        int result = fileChooser.showOpenDialog(this);
//        if (result == JFileChooser.APPROVE_OPTION) {
//            File selectedFile = fileChooser.getSelectedFile();
//            appendMessage("Me: Sent file - " + selectedFile.getName());
//        }
//    }

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
        public MessagePanel(Message message) {
            this.message = message;
            boolean isSelf = message.getSenderId().equals(AuthContext.INSTANCE.getPrincipal().getUserId());
            setLayout(new FlowLayout(isSelf ? FlowLayout.RIGHT : FlowLayout.LEFT));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            JLabel messageLabel = new JLabel(message.getContent().getValue());
            messageLabel.setOpaque(true);
            messageLabel.setForeground(Color.BLACK);
            messageLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY, 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
           add(messageLabel);
        }

    }
}