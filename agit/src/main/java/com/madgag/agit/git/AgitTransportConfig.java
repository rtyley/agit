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

package com.madgag.agit.git;

import android.util.Log;

import com.google.inject.Inject;

import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;

public class AgitTransportConfig implements TransportConfigCallback {

    private final static String TAG = "ATC";

    private final SshSessionFactory sshSessionFactory;

    @Inject
    public AgitTransportConfig(SshSessionFactory sshSessionFactory) {
        this.sshSessionFactory = sshSessionFactory;
    }

    public void configure(Transport tn) {
        Log.d(TAG, "Configuring " + tn);
        if (tn instanceof SshTransport) {
            ((SshTransport) tn).setSshSessionFactory(sshSessionFactory);
        }
    }
}
