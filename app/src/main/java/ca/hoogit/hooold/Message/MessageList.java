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
package ca.hoogit.hooold.Message;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author jordon
 *
 * Date    16/08/15
 * Description
 *
 * Simple extension to the ArrayList, auto sort the array on adding an item
 *
 */
public class MessageList extends ArrayList<Message> {
    @Override
    public boolean add(Message object) {
        boolean added = super.add(object);
        this.sort();
        return added;
    }

    public boolean add(Message object, boolean sort) {
        boolean added = super.add(object);
        if (sort) {
            this.sort();
        }
        return added;
    }

    public void sort() {
        Collections.sort(this);
        Collections.reverse(this);
    }

    public void sort(boolean ascending) {
        Collections.sort(this);
        if (!ascending) {
            Collections.reverse(this);
        }
    }
}
