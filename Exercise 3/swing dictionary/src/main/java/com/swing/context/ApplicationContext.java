package com.swing.context;


import com.swing.repository.record.RecordRepository;
import com.swing.repository.record.RecordRepositoryImpl;
import com.swing.services.record.RecordService;
import com.swing.services.record.RecordServiceImpl;
import lombok.Getter;


@Getter
public class ApplicationContext {
    private static ApplicationContext context;
    private ApplicationContext() {}
    public static ApplicationContext getInstance() {
        if (context == null) {
            throw new IllegalStateException("ApplicationContext not initialized");
        }
        return context;
    }
    private static DictionaryType dictionaryType = DictionaryType.VI_EN;
    private RecordRepository enViDictionary;
    private RecordRepository viEnDictionary;
    private RecordService enViService;
    private RecordService viEnService;

    public static ApplicationContext init()  {
        if (context != null) {
            return context;
        }
        context = new ApplicationContext();
        context.enViDictionary = new RecordRepositoryImpl("Anh_Viet.xml");
        context.viEnDictionary = new RecordRepositoryImpl("Viet_Anh.xml");
        context.enViService = new RecordServiceImpl(context.enViDictionary);
        context.viEnService = new RecordServiceImpl(context.viEnDictionary);
        return context;
    }

    public static synchronized void setDictionaryType(DictionaryType type) {
        dictionaryType = type;
    }

    public RecordService getRecordService() {
        if (dictionaryType == DictionaryType.VI_EN) {
            return viEnService;
        } else {
            return enViService;
        }
    }
}
