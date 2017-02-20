package com.development.didrikpa.wifipasswordscanner;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

/**
 * Created by didrikpa on 20/02/17.
 */

public class NewBaseAPI extends TessBaseAPI {

    private long mNativeData;
    private boolean mRecycled;



    @Override
    public boolean init(String datapath, String language) {
        return init(datapath, language, OEM_DEFAULT);
    }

    @Override
    public boolean init(String datapath, String language, int ocrEngineMode) {
        if (datapath == null)
            throw new IllegalArgumentException("Data path must not be null!");
        if (!datapath.endsWith(File.separator))
            datapath += File.separator;

        File datapathFile = new File(datapath);
        if (!datapathFile.exists())
            throw new IllegalArgumentException("Data path does not exist!");

        File tessdata = new File(datapath + "app_tessdata");
        if (!tessdata.exists() || !tessdata.isDirectory())
            throw new IllegalArgumentException("Data path must contain subfolder tessdata!");

        //noinspection deprecation
        if (ocrEngineMode != OEM_CUBE_ONLY) {
            for (String languageCode : language.split("\\+")) {
                if (!languageCode.startsWith("~")) {
                    File datafile = new File(tessdata + File.separator +
                            languageCode + ".traineddata");
                    if (!datafile.exists())
                        throw new IllegalArgumentException("Data file not found at " + datafile);
                }
            }
        }

        boolean success = true;

        if (success) {
            mRecycled = false;
        }

        return success;
    }


}
