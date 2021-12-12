package com.video.list.player;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 */
public class AssetsUtil {

    public static String read(Context context, String fileName){
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();
        if (context == null || fileName == null || 0 >= fileName.length()) {
            return null;
        }
        try {
            inputStream = context.getResources().getAssets().open(fileName);
            if (null == inputStream) {
                return null;
            }
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(inputStreamReader);
            IOUtils.close(bufferedReader);
            IOUtils.close(inputStream);
        }
        return builder.toString();
    }

    public static String getJson(Context context,String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}
