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
package ca.hoogit.hooold.Scheduling;

import android.app.AlarmManager;
import android.content.Context;

/**
 * @author jordon
 *
 * Date    21/08/15
 * Description
 *
 */
public class Alarming {
    private static Alarming ourInstance = new Alarming();

    public static Alarming getInstance() {
        return ourInstance;
    }

    private Context mContext;

    private AlarmManager mManager;

    private Alarming() {
    }

    public void init(Context context) {
        this.mContext = context;
        this.mManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public AlarmManager manager() {
        return this.mManager;
    }
}
