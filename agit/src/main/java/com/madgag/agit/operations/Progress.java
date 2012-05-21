/*
 * Copyright (c) 2011 Roberto Tyley
 *
 * This file is part of 'Agit' - an Android Git client.
 *
 * Agit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Agit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.madgag.agit.operations;

import static org.eclipse.jgit.lib.ProgressMonitor.UNKNOWN;

public class Progress {
    public final String msg;
    public final int totalWork, totalCompleted;

    public Progress(String msg) {
        this.msg = msg;
        this.totalWork = 0;
        this.totalCompleted = 0;
    }

    public Progress(String msg, int totalWork, int totalCompleted) {
        this.msg = msg;
        this.totalWork = totalWork;
        this.totalCompleted = totalCompleted;
    }

    public boolean isIndeterminate() {
        return totalWork == UNKNOWN;
    }

    public String toString() {
        return "[" + msg + " totalCompleted=" + totalCompleted + " totalWork=" + totalWork + "]";
    }
}