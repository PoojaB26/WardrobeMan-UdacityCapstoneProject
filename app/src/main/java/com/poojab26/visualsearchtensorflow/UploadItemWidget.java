package com.poojab26.visualsearchtensorflow;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import static java.lang.Boolean.TRUE;

/**
 * Implementation of App Widget functionality.
 */
public class UploadItemWidget extends AppWidgetProvider {

   static int wardrobeCount = 0;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.upload_item_widget);
        views.setTextViewText(R.id.appwidget_text, String.format(context.getString(R.string.tv_widget_info), wardrobeCount));

        Intent configIntent = new Intent(context, MainActivity.class);
        configIntent.putExtra(Const.WidgetLaunch, TRUE);
        configIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);


        views.setOnClickPendingIntent(R.id.widget_container, configPendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void setWardrobeCount(int count){
        wardrobeCount = count;

    }
}

