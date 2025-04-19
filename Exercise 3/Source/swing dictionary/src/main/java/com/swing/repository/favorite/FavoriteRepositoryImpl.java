package com.swing.repository.favorite;

import com.swing.models.Favorite;
import com.swing.repository.pagination.Sort;

import javax.xml.stream.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FavoriteRepositoryImpl implements FavoriteRepository {
    public static final String FAVORITES_TAG = "favorites";
    public static final String FAVORITE_TAG = "favorite";
    public static final String WORD_TAG = "word";
    public static final String LANGUAGE_TAG = "language";
    public static final String MEANING_TAG = "meaning";
    private final File file;

    public FavoriteRepositoryImpl(String filepath) {
        this.file = new File(filepath);
    }

    public boolean createOne(Favorite favorite) {
        try {
            List<Favorite> favorites = readAll();
            favorites.add(favorite);
            writeAll(favorites);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Favorite> findMany(FavoriteQuery query) throws Exception {
        List<Favorite> allFavorites = readAll();

        if (query != null && query.getLanguage() != null && !query.getLanguage().isBlank()) {
            allFavorites = allFavorites.stream()
                    .filter(fav -> query.getLanguage().equalsIgnoreCase(fav.getLanguage()))
                    .toList();
        }

        Sort sort = query != null && query.getSort() != null ? query.getSort() : Sort.defaultInstance();
        String field = sort.getField();
        boolean ascending = Boolean.TRUE.equals(sort.getAscending());

        Comparator<Favorite> comparator = Comparator.comparing(fav -> switch (field) {
            case MEANING_TAG -> fav.getMeaning().toLowerCase();
            case LANGUAGE_TAG -> fav.getLanguage().toLowerCase();
            default -> fav.getWord().toLowerCase();
        });

        if (!ascending) {
            comparator = comparator.reversed();
        }

        return allFavorites.stream()
                .sorted(comparator)
                .toList();

    }

    private List<Favorite> readAll() throws Exception {
        List<Favorite> list = new ArrayList<>();
        if (!file.exists()) return list;

        XMLInputFactory factory = XMLInputFactory.newInstance();
        FileInputStream fis = new FileInputStream(file);
        XMLStreamReader reader = factory.createXMLStreamReader(fis);

        Favorite current = null;
        String currentTag = null;
        StringBuilder textBuffer = new StringBuilder();

        while (reader.hasNext()) {
            int event = reader.next();

            switch (event) {
                case XMLStreamConstants.START_ELEMENT -> {
                    currentTag = reader.getLocalName();
                    if (currentTag.equals(FAVORITE_TAG)) {
                        current = new Favorite();
                    }
                    textBuffer.setLength(0); // reset buffer on new tag
                }

                case XMLStreamConstants.CHARACTERS -> {
                    if (currentTag != null) {
                        textBuffer.append(reader.getText());
                    }
                }

                case XMLStreamConstants.END_ELEMENT -> {
                    String tag = reader.getLocalName();
                    if (current != null && currentTag != null) {
                        String text = textBuffer.toString().trim();
                        switch (currentTag) {
                            case WORD_TAG -> current.setWord(text);
                            case MEANING_TAG -> current.setMeaning(text);
                            case LANGUAGE_TAG -> current.setLanguage(text);
                        }
                    }

                    if (tag.equals(FAVORITE_TAG) && current != null) {
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

    private void writeAll(List<Favorite> favorites) throws Exception {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        FileOutputStream fos = new FileOutputStream(file);

        XMLStreamWriter writer = factory.createXMLStreamWriter(fos, "UTF-8");
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeCharacters("\n");
        writer.writeStartElement(FAVORITES_TAG);

        for (Favorite f : favorites) {
            writer.writeCharacters("\n ");
            writer.writeStartElement(FAVORITE_TAG);

            writer.writeCharacters("\n  ");
            writer.writeStartElement(WORD_TAG);
            writer.writeCharacters(f.getWord());
            writer.writeEndElement();

            writer.writeCharacters("\n  ");
            writer.writeStartElement(LANGUAGE_TAG);
            writer.writeCharacters(f.getLanguage());
            writer.writeEndElement();

            writer.writeCharacters("\n  ");
            writer.writeStartElement(MEANING_TAG);
            writer.writeCharacters(f.getMeaning());
            writer.writeEndElement();


            writer.writeCharacters("\n ");
            writer.writeEndElement(); // </favorite>
        }

        writer.writeCharacters("\n");
        writer.writeEndElement(); // </favorites>
        writer.writeEndDocument();
        writer.flush();
        writer.close();
        fos.close();
    }
}
