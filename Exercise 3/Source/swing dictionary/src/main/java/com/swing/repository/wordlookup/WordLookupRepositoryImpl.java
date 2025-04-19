package com.swing.repository.wordlookup;

import com.swing.models.WordLookup;

import javax.xml.stream.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class WordLookupRepositoryImpl implements WordLookupRepository {
    public static final String LOOKUPS_TAG = "lookups";
    public static final String LOOKUP_TAG = "lookup";
    public static final String WORD_TAG = "word";
    public static final String LANGUAGE_TAG = "language";
    public static final String TIMESTAMP_TAG = "timestamp";
    private final File file;

    public WordLookupRepositoryImpl(String filepath) {
        this.file = new File(filepath);
    }

    public boolean createOne(WordLookup wordLookup) {
        try {
            List<WordLookup> wordLookups = readAll();
            wordLookups.add(wordLookup);
            writeAll(wordLookups);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<WordLookup> findMany(WordLookupQuery query) throws Exception {
        if (query.getFromTimeInMilSec() >= query.getToTimeInMilSec()) {
            return List.of();
        }
        List<WordLookup> wordLookups = readAll();
        return wordLookups.stream()
                .filter(lookup -> lookup.getTimestamp() >= query.getFromTimeInMilSec()
                        && lookup.getTimestamp() <= query.getToTimeInMilSec()
                        && lookup.getLanguage().equals(query.getLanguage()))
                .toList();
    }

    private List<WordLookup> readAll() throws Exception {
        List<WordLookup> list = new ArrayList<>();
        if (!file.exists()) return list;

        XMLInputFactory factory = XMLInputFactory.newInstance();
        FileInputStream fis = new FileInputStream(file);
        XMLStreamReader reader = factory.createXMLStreamReader(fis);

        WordLookup current = null;
        String currentTag = null;
        StringBuilder textBuffer = new StringBuilder();

        while (reader.hasNext()) {
            int event = reader.next();
            switch (event) {
                case XMLStreamConstants.START_ELEMENT -> {
                    currentTag = reader.getLocalName();
                    if (currentTag.equals(LOOKUP_TAG)) {
                        current = new WordLookup();
                    }
                    textBuffer.setLength(0); // reset buffer on new tag
                }
                case XMLStreamConstants.CHARACTERS -> {
                    if (currentTag != null) textBuffer.append(reader.getText());
                }
                case XMLStreamConstants.END_ELEMENT -> {
                    String tag = reader.getLocalName();
                    if (current != null && currentTag != null) {
                        String text = textBuffer.toString().trim();
                        switch (currentTag) {
                            case WORD_TAG -> current.setWord(text);
                            case LANGUAGE_TAG -> current.setLanguage(text);
                            case TIMESTAMP_TAG -> {
                                long timestamp = Long.parseLong(textBuffer.toString());
                                current.setTimestamp(timestamp);
                            }
                        }
                    }
                    if (tag.equals(LOOKUP_TAG) && current != null) {
                        list.add(current);
                        current = null;
                    }

                    currentTag = null;
                    textBuffer.setLength(0); // reset for next tag
                }
            }
        }

        reader.close();
        fis.close();
        return list;
    }

    private void writeAll(List<WordLookup> wordLookups) throws Exception {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        FileOutputStream fos = new FileOutputStream(file);

        XMLStreamWriter writer = factory.createXMLStreamWriter(fos, "UTF-8");
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeCharacters("\n");
        writer.writeStartElement(LOOKUPS_TAG);

        for (WordLookup f : wordLookups) {
            writer.writeCharacters("\n ");
            writer.writeStartElement(LOOKUP_TAG);

            writer.writeCharacters("\n  ");
            writer.writeStartElement(WORD_TAG);
            writer.writeCharacters(f.getWord());
            writer.writeEndElement();

            writer.writeCharacters("\n  ");
            writer.writeStartElement(LANGUAGE_TAG);
            writer.writeCharacters(f.getLanguage());
            writer.writeEndElement();

            writer.writeCharacters("\n  ");
            writer.writeStartElement(TIMESTAMP_TAG);
            writer.writeCharacters(f.getTimestamp().toString());
            writer.writeEndElement();

            writer.writeCharacters("\n ");
            writer.writeEndElement();
        }

        writer.writeCharacters("\n");
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();
        writer.close();
        fos.close();
    }
}
