package com.poojab26.visualsearchtensorflow;

/**
 * Created by poojab26 on 26-May-18.
 */
public class Const {

    public static String Label = "label";
    public static String DatabaseItemReference = "itemReference";
    public static String CameraBitmap = "bitmap";
    public static String DownloadUrl = "downloadUrl";
    public static String WidgetLaunch = "widgetLaunch";
    public static String Preferences = "preferences";
    public static String PreferencesCount = "preferences_count";

    public static int wardrobeCount = 0;
    public static void setCount(int count){
       wardrobeCount = count;
    }

    public static int getCount(){
        return wardrobeCount;
    }


}
