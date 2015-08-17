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

/**
 * @author jordon
 *
 * Date    11/08/15
 * Description
 *
 */
public class Consts {

    /**
     * Fragment argument keys
     */
    public static final String ARG_TYPE = "TYPE";

    /**
     * Intent Extras, bundle, savedInstance keys
     */
    public static final String KEY_MESSAGES = "MESSAGES";
    public static final String KEY_MESSAGE = "MESSAGE";
    public static final String KEY_MESSAGE_ID = "MESSAGE_ID";
    public static final String KEY_MESSAGE_SMS = "MESSAGE_SMS";

    /**
     * Service Actions
     */
    public static final String ACTION_SCHEDULE_ADD = "ca.hoogit.hooold.Scheduling.action.ADD";
    public static final String ACTION_SCHEDULE_DELETE = "ca.hoogit.hooold.Scheduling.action.DELETE";
    public static final String ACTION_SCHEDULE_SEND = "ca.hoogit.hooold.Scheduling.action.SEND";

    /**
     * Message types
     */ // TODO rename to status
    public static final int MESSAGE_TYPE_ALL = -1;
    public static final int MESSAGE_TYPE_SCHEDULED = 0;
    public static final int MESSAGE_TYPE_RECENT = 1;

    /**
     * Create Activity action code
     */
    public static final String MESSAGE_CREATE = "Create";
    public static final String MESSAGE_EDIT = "Edit";

    /**
     * Activity result codes
     */
    public static final int RESULT_MESSAGE_CREATE = 1991;
    public static final int RESULT_MESSAGE_EDIT = 1992;
    public static final int RESULT_PICK_CONTACT = 7683;

    /**
     * Animation durations
     */
    public static final long ANIMATION_LIST_ITEM_DELAY = 1000L;


    /**
     * Fragment tags
     */
    public static final String FRAGMENT_TAG_DATETIME_PICKER = "DateTimePicker";
}
