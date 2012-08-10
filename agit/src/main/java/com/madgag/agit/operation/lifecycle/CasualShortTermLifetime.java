/*
 * Copyright (c) 2011, 2012 Roberto Tyley
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
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

package com.madgag.agit.operation.lifecycle;

import com.madgag.agit.operations.OpNotification;
import com.madgag.agit.operations.Progress;

public class CasualShortTermLifetime implements OperationLifecycleSupport {

    public void startedWith(OpNotification startNotification) {
        // start some kind of animation in the RMA
    }

    public void publish(Progress progress) {
        // TODO Auto-generated method stub

    }

    public void error(OpNotification completionNotification) {
    }

    public void success(OpNotification completionNotification) {
    }

    public void completed(OpNotification completionNotification) {
        // stop the animation, I guess
    }


}
