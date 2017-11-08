package com.ideationts.android.pathadisa.pathadisanative.utils;

import android.net.Uri;
import android.util.Log;

import com.goebl.david.Webb;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by ARITRA on 05-07-2017.
 */

public class NetworkUtils {


    public static String getAllRoutes(String uri,int timeOut)throws IOException{
        URL url = buildUrl(uri);
        HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
        urlConnection.setDoOutput( true );
        urlConnection.setConnectTimeout(timeOut);
        urlConnection.setRequestMethod( "GET" );
        String response = getResponseFromHttpUrl(urlConnection);
        if (response.length()>0) {
            Log.d("getVehicleData: success", response);
            return response;
        } else {
            Log.d("getVehicleData: failure","");
            return null;
        }

    }

    public static String getAllVehicleData(String uri,String data,int timeOut)throws IOException{
        URL url = buildUrl(uri);
        HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
        urlConnection.setDoOutput( true );
        urlConnection.setConnectTimeout(timeOut);
        urlConnection.setInstanceFollowRedirects( false );
        urlConnection.setRequestMethod( "POST" );
        urlConnection.setRequestProperty( Webb.HDR_CONTENT_TYPE, Webb.APP_JSON);
        urlConnection.setRequestProperty( Webb.HDR_ACCEPT, Webb.APP_JSON);
        urlConnection.getOutputStream().write(data.getBytes());
        String response = getResponseFromHttpUrl(urlConnection);
            if (response.length()>0) {
                Log.d("getVehicleData: success", response);
                return response;
            } else {
                Log.d("getVehicleData: failure","");
                return null;
            }

    }
    private static URL buildUrl(String urlString) {
        Uri builtUri = Uri.parse(urlString);

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
    public static String getResponseFromHttpUrl(HttpURLConnection urlConnection) throws IOException {

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
  }
