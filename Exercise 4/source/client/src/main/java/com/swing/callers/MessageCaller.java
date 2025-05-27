package com.swing.callers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.context.SocketConnection;
import com.swing.io.Input;
import com.swing.io.Output;
import com.swing.io.message.*;
import com.swing.types.Result;
import lombok.extern.java.Log;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Log
public class MessageCaller {

    private final ObjectMapper mapper;
    private final SocketConnection socketConnection;

    public MessageCaller(SocketConnection socketConnection, ObjectMapper mapper) {
        this.mapper =mapper;
        this.socketConnection = socketConnection;
    }

    public Result<Output<CreateMessageOutput>> send(CreateMessageInput input) {
        try (Socket clientSocket = new Socket(socketConnection.getHost(), socketConnection.getPort());
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))) {
            Input<CreateMessageInput> in = CallerUtils.INSTANCE.buildInputWithToken();
            in.setCommand(Input.Command.SEND_MESSAGE);
            in.setBody(input);
            String jsonString = mapper.writeValueAsString(in);
            writer.write(jsonString);
            writer.newLine();writer.flush();
            String responseJson = reader.readLine();
            log.info("Server response: " + responseJson);
            Output<CreateMessageOutput> output = mapper.readValue(responseJson,
                    new TypeReference<>() {});
            return Result.success(output);
        } catch (IOException ex) {
            return Result.failure(ex);
        }
    }

    public Result<Output<GetMessageOutput>> getMessage(GetMessageInput input) {
        try (Socket clientSocket = new Socket(socketConnection.getHost(), socketConnection.getPort());
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))) {
            Input<GetMessageInput> in = CallerUtils.INSTANCE.buildInputWithToken();
            in.setCommand(Input.Command.GET_MESSAGE);
            in.setBody(input);
            String jsonString = mapper.writeValueAsString(in);
            writer.write(jsonString);
            writer.newLine();writer.flush();
            String responseJson = reader.readLine();
            log.info("Server response: " + responseJson);
            Output<GetMessageOutput> output = mapper.readValue(responseJson,
                    new TypeReference<>() {});
            return Result.success(output);
        } catch (IOException ex) {
            return Result.failure(ex);
        }
    }

    public Result<Output<GetMessagesOutput>> getMessages(GetMessagesInput input) {
        try (Socket clientSocket = new Socket(socketConnection.getHost(), socketConnection.getPort());
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))) {
            Input<GetMessagesInput> in = CallerUtils.INSTANCE.buildInputWithToken();
            in.setCommand(Input.Command.GET_MESSAGES);
            in.setBody(input);
            String jsonString = mapper.writeValueAsString(in);
            writer.write(jsonString);
            writer.newLine();writer.flush();
            String responseJson = reader.readLine();
            log.info("Server response: " + responseJson);
            Output<GetMessagesOutput> output = mapper.readValue(responseJson,
                    new TypeReference<>() {});
            return Result.success(output);
        } catch (IOException ex) {
            return Result.failure(ex);
        }
    }

    public Result<Output<UpdateMessageOutput>> update(UpdateMessageInput input) {
        try (Socket clientSocket = new Socket(socketConnection.getHost(), socketConnection.getPort());
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))) {
            Input<UpdateMessageInput> in = CallerUtils.INSTANCE.buildInputWithToken();
            in.setCommand(Input.Command.UPDATE_MESSAGE);
            in.setBody(input);
            String jsonString = mapper.writeValueAsString(in);
            writer.write(jsonString);
            writer.newLine();writer.flush();
            String responseJson = reader.readLine();
            log.info("Server response: " + responseJson);
            Output<UpdateMessageOutput> output = mapper.readValue(responseJson,
                    new TypeReference<>() {});
            return Result.success(output);
        } catch (IOException ex) {
            return Result.failure(ex);
        }
    }

    public Result<Output<DeleteMessageOutput>> delete(DeleteMessageInput input) {
        try (Socket clientSocket = new Socket(socketConnection.getHost(), socketConnection.getPort());
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))) {
            Input<DeleteMessageInput> in = CallerUtils.INSTANCE.buildInputWithToken();
            in.setCommand(Input.Command.UPDATE_MESSAGE);
            in.setBody(input);
            String jsonString = mapper.writeValueAsString(in);
            writer.write(jsonString);
            writer.newLine();writer.flush();
            String responseJson = reader.readLine();
            log.info("Server response: " + responseJson);
            Output<DeleteMessageOutput> output = mapper.readValue(responseJson,
                    new TypeReference<>() {});
            return Result.success(output);
        } catch (IOException ex) {
            return Result.failure(ex);
        }
    }
}
