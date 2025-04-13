package com.swing.repository.record;

import com.swing.models.Dictionary;
import com.swing.models.RecordModel;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.util.logging.Logger;

public class RecordRepositoryImpl implements RecordRepository {
    private final File file;
    private final Logger logger = Logger.getLogger(RecordRepositoryImpl.class.getName());

    public RecordRepositoryImpl(String filepath) {
        this.file = new File(filepath);
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + filepath);
        }
    }

//    public RecordModel findOne(RecordQuery query) {
//        try {
//            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
//            InputSource is = new InputSource(reader);
//            Document doc = DocumentBuilderFactory.newInstance()
//                    .newDocumentBuilder().parse(is);
//
//            doc.getDocumentElement().normalize();
//            NodeList recordList = doc.getElementsByTagName("record");
//
//            for (int i = 0; i < recordList.getLength(); i++) {
//                Node node = recordList.item(i);
//                if (node.getNodeType() == Node.ELEMENT_NODE) {
//                    Element recordElem = (Element) node;
//                    String word = recordElem.getElementsByTagName("word")
//                            .item(0).getTextContent().trim().toLowerCase();
//                    if (word.equals(query.getWord().toLowerCase())) {
//                        String meaning = recordElem.getElementsByTagName("meaning")
//                                .item(0).getTextContent().trim();
//                        return new RecordModel(word, meaning);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            logger.warning(e.getMessage());
//        }
//        return null; // not found
//    }

    public RecordModel findOne(RecordQuery query) {
        try {
            // Create JAXB context and initialize Unmarshaller
            JAXBContext context = JAXBContext.newInstance(Dictionary.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            // Unmarshal the XML file into a Dictionary object
            Dictionary dictionary = (Dictionary) unmarshaller.unmarshal(file);

            // Search for the record with the matching word
            for (RecordModel r : dictionary.getRecords()) {
                if (r.getWord().equalsIgnoreCase(query.getWord())) {
                    return r;
                }
            }
        } catch (JAXBException e) {
            logger.warning("Error unmarshalling XML: " + e.getMessage());
        }

        // Return null if not found
        return null;

    }
}
