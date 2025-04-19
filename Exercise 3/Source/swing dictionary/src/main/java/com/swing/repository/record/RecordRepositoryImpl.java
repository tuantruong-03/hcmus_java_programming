package com.swing.repository.record;

import com.swing.models.RecordModel;
import org.apache.commons.io.input.BOMInputStream;

import javax.xml.stream.*;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RecordRepositoryImpl implements RecordRepository {
    public static final String DICTIONARY_TAG = "dictionary";
    public static final String RECORD_TAG = "record";
    public static final String WORD_TAG = "word";
    public static final String MEANING_TAG = "meaning";

    private final Logger logger = Logger.getLogger(RecordRepositoryImpl.class.getName());
    private final String filepath;

    public RecordRepositoryImpl(String filepath) {
        File file = new File(filepath);
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + filepath);
        }
        this.filepath = filepath;
    }


    public RecordModel findOne(RecordQuery query) {
        boolean isInvalidQuery = query == null
                || query.getWord() == null || query.getWord().trim().isEmpty();
        if (isInvalidQuery) {
            return null;
        }
        try {
            XMLEventReader reader = getXMLEventReader();
            RecordModel foundRecord = null;
            boolean isInRecord = false;

            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    if (RECORD_TAG.equals(startElement.getName().getLocalPart())) {
                        isInRecord = true;
                    }

                    if (isInRecord && WORD_TAG.equals(startElement.getName().getLocalPart())) {
                        event = reader.nextEvent(); // Get the word content
                        String word = event.asCharacters().getData().trim();
                        if (word.equalsIgnoreCase(query.getWord())) {
                            foundRecord = new RecordModel();
                            foundRecord.setWord(word);
                        }
                    }

                    if (isInRecord && MEANING_TAG.equals(startElement.getName().getLocalPart())) {
                        StringBuilder meaningBuilder = new StringBuilder();
                        while (reader.hasNext()) {
                            XMLEvent nextEvent = reader.nextEvent();
                            if (nextEvent.isCharacters()) {
                                meaningBuilder.append(nextEvent.asCharacters().getData());
                            } else if (nextEvent.isEndElement() &&
                                    MEANING_TAG.equals(nextEvent.asEndElement().getName().getLocalPart())) {
                                break;
                            }
                        }
                        if (foundRecord != null) {
                            foundRecord.setMeaning(meaningBuilder.toString().trim());
                        }
                    }
                }
                if (event.isEndElement() && RECORD_TAG.equals(event.asEndElement().getName().getLocalPart())) {
                    if (foundRecord != null) {
                        return foundRecord;
                    }
                    isInRecord = false;
                }
            }
        } catch (XMLStreamException e) {
            logger.warning(e.getMessage());
            return null;
        }
        return null;
    }

    public boolean createOne(RecordModel recordModel) {
        File originalFile = new File(filepath);
        File tempFile = new File(filepath + Instant.now().getEpochSecond() + ".tmp");

        try (
                FileInputStream fis = new FileInputStream(originalFile);
                BOMInputStream bis = BOMInputStream.builder().setInputStream(fis).get();
                InputStreamReader reader = new InputStreamReader(bis, StandardCharsets.UTF_8);
                FileOutputStream fos = new FileOutputStream(tempFile);
                OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
        ) {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

            XMLEventReader xmlReader = inputFactory.createXMLEventReader(reader);
            XMLEventWriter xmlWriter = outputFactory.createXMLEventWriter(writer);

            XMLEventFactory eventFactory = XMLEventFactory.newInstance();

            while (xmlReader.hasNext()) {
                XMLEvent event = xmlReader.nextEvent();

                // Before writing </dictionary>, insert the new record
                if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(DICTIONARY_TAG)) {
                    // Write the new <record>
                    xmlWriter.add(eventFactory.createCharacters("\n  "));
                    xmlWriter.add(eventFactory.createStartElement("", "", RECORD_TAG));

                    xmlWriter.add(eventFactory.createCharacters("\n    "));
                    xmlWriter.add(eventFactory.createStartElement("", "", WORD_TAG));
                    xmlWriter.add(eventFactory.createCharacters(recordModel.getWord()));
                    xmlWriter.add(eventFactory.createEndElement("", "", WORD_TAG));

                    xmlWriter.add(eventFactory.createCharacters("\n    "));
                    xmlWriter.add(eventFactory.createStartElement("", "", MEANING_TAG));
                    xmlWriter.add(eventFactory.createCharacters(recordModel.getMeaning()));
                    xmlWriter.add(eventFactory.createEndElement("", "", MEANING_TAG));

                    xmlWriter.add(eventFactory.createCharacters("\n  "));
                    xmlWriter.add(eventFactory.createEndElement("", "", RECORD_TAG));
                    xmlWriter.add(eventFactory.createCharacters("\n"));
                }

                xmlWriter.add(event); // Copy existing content
            }
            xmlWriter.flush();
            xmlWriter.close();
            xmlReader.close();
            fis.close();
            bis.close();
            reader.close();
            fos.close();
            writer.close();

            Files.deleteIfExists(originalFile.toPath());
            Files.move(tempFile.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception e) {
            logger.warning("Error writing XML: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteOne(RecordQuery query) {
        File originalFile = new File(filepath);
        File tempFile = new File(filepath + Instant.now().getEpochSecond() + ".tmp");

        try (
                FileInputStream fis = new FileInputStream(originalFile);
                BOMInputStream bis = BOMInputStream.builder().setInputStream(fis).get();
                InputStreamReader reader = new InputStreamReader(bis, StandardCharsets.UTF_8);
                FileOutputStream fos = new FileOutputStream(tempFile);
                OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8)
        ) {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

            XMLEventReader xmlReader = inputFactory.createXMLEventReader(reader);
            XMLEventWriter xmlWriter = outputFactory.createXMLEventWriter(writer);

            List<XMLEvent> recordBuffer = new ArrayList<>();
            boolean isInRecord = false;
            boolean shouldSkip = false;

            while (xmlReader.hasNext()) {
                XMLEvent event = xmlReader.nextEvent();

                if (event.isStartElement() &&
                        RECORD_TAG.equals(event.asStartElement().getName().getLocalPart())) {

                    // Start of a <record>
                    isInRecord = true;
                    shouldSkip = false;
                    recordBuffer.clear();
                    recordBuffer.add(event);
                    continue;
                }

                if (isInRecord) {
                    recordBuffer.add(event);

                    if (event.isStartElement() &&
                            WORD_TAG.equals(event.asStartElement().getName().getLocalPart())) {

                        XMLEvent wordEvent = xmlReader.nextEvent();
                        recordBuffer.add(wordEvent);

                        if (wordEvent.isCharacters()) {
                            String word = wordEvent.asCharacters().getData().trim();
                            if (word.equalsIgnoreCase(query.getWord())) {
                                shouldSkip = true;
                            }
                        }
                        continue;
                    }

                    if (event.isEndElement() &&
                            RECORD_TAG.equals(event.asEndElement().getName().getLocalPart())) {

                        isInRecord = false;

                        if (!shouldSkip) {
                            for (XMLEvent buffered : recordBuffer) {
                                xmlWriter.add(buffered);
                            }
                        }

                        recordBuffer.clear();
                    }

                    continue;
                }

                // Outside <record>, write directly
                xmlWriter.add(event);
            }

            xmlWriter.flush();
            xmlWriter.close();
            xmlReader.close();

            reader.close();
            writer.close();

            fis.close();
            bis.close();
            fos.close();

            Files.deleteIfExists(originalFile.toPath());
            Files.move(tempFile.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return true;

        } catch (IOException | XMLStreamException e) {
            e.printStackTrace();
            return false;
        }
    }

    private XMLEventReader getXMLEventReader() {
        try {
            FileInputStream fileInputStream = new FileInputStream(filepath);
            BOMInputStream bomInputStream = BOMInputStream.builder()
                    .setInputStream(fileInputStream)
                    .get();
            InputStreamReader inputStreamReader = new InputStreamReader(bomInputStream, StandardCharsets.UTF_8);

            XMLInputFactory factory = XMLInputFactory.newInstance();
            return factory.createXMLEventReader(inputStreamReader);
        } catch (IOException | XMLStreamException e) {
            throw new RuntimeException("Error initializing XML reader", e);
        }
    }

    private XMLStreamWriter getXMLStreamWriter() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filepath);
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            return factory.createXMLStreamWriter(fileOutputStream, StandardCharsets.UTF_8.name());
        } catch (IOException | XMLStreamException e) {
            throw new RuntimeException("Error initializing XML writer", e);
        }
    }
}
