package com.ideationts.android.pathadisa.pathadisanative.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by ARITRA on 04-07-2017.
 */

public class PropertyUtil{
        public static String getProperty(String key,Context context) throws IOException {
            Properties properties = new Properties();;
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("config.properties");
            properties.load(inputStream);
            return properties.getProperty(key);

        }
}
