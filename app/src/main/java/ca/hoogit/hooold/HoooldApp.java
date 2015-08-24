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
package ca.hoogit.hooold;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.orm.SugarContext;

import ca.hoogit.hooold.Scheduling.Alarming;
import io.fabric.sdk.android.Fabric;

/**
 * @author jordon
 *
 * Date    11/08/15
 * Description
 *
 */
public class HoooldApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        SugarContext.init(this);
        Alarming.getInstance().init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }
}
