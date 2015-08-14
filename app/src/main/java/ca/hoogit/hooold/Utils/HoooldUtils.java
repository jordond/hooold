/**
 * Copyright (C) 2015, Jordon de Hoog
 * <p/>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package ca.hoogit.hooold.Utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.android.ex.chips.RecipientEntry;
import com.android.ex.chips.recipientchip.DrawableRecipientChip;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Locale;

/**
 * @author jordon
 *
 * Date    11/08/15
 * Description
 *
 */
public class HoooldUtils {

    private static final String TAG = HoooldUtils.class.getSimpleName();

    public static String toListDate(Date date) {
        SimpleDateFormat f = new SimpleDateFormat("MMMM dd - h:mm a", Locale.getDefault());
        return f.format(date);
    }

    public static String toFancyDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        SimpleDateFormat f1 = new SimpleDateFormat("MMMM dd", Locale.getDefault());
        SimpleDateFormat f2 = new SimpleDateFormat("h:mm a", Locale.getDefault());
        String fancy = f1.format(date);
        fancy += getDaySuffix(cal.get(Calendar.DAY_OF_MONTH));
        fancy += " at " + f2.format(date);

        return fancy;
    }

    public static String getDaySuffix(final int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }

    public static void rateApp(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" +
                            context.getPackageName())));
        }
    }

    public static List<RecipientEntry> chipsToList(DrawableRecipientChip[] chips) {
        List<RecipientEntry> list = new ArrayList<>();
        for (DrawableRecipientChip chip : chips) {
            list.add(chip.getEntry());
        }
        return list;
    }
}
