package com.swing.context;


import com.swing.repository.favorite.FavoriteRepository;
import com.swing.repository.favorite.FavoriteRepositoryImpl;
import com.swing.repository.record.RecordRepository;
import com.swing.repository.record.RecordRepositoryImpl;
import com.swing.services.favorite.FavoriteService;
import com.swing.services.favorite.FavoriteServiceImpl;
import com.swing.services.record.RecordService;
import com.swing.services.record.RecordServiceImpl;
import lombok.Getter;


@Getter
public final class ApplicationContext {
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
    private FavoriteRepository favoriteRepository;

    private RecordService enViService;
    private RecordService viEnService;
    private FavoriteService favoriteService;

    public static void init()  {
        if (context != null) {
            return;
        }
        context = new ApplicationContext();
        context.enViDictionary = new RecordRepositoryImpl("Anh_Viet.xml");
        context.viEnDictionary = new RecordRepositoryImpl("Viet_Anh.xml");
        context.favoriteRepository = new FavoriteRepositoryImpl("Yeu_Thich.xml");


        context.enViService = new RecordServiceImpl(context.enViDictionary);
        context.viEnService = new RecordServiceImpl(context.viEnDictionary);
        context.favoriteService = new FavoriteServiceImpl(context.favoriteRepository);
    }

    public static synchronized void setDictionaryType(DictionaryType type) {
        dictionaryType = type;
    }
    public static synchronized DictionaryType getDictionaryType() {
        return dictionaryType;
    }

    public RecordService getRecordService() {
        if (dictionaryType == DictionaryType.VI_EN) {
            return viEnService;
        } else {
            return enViService;
        }
    }
}
