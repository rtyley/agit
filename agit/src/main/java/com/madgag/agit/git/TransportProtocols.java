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

package com.madgag.agit.git;

import static org.eclipse.jgit.transport.Transport.getTransportProtocols;

import org.eclipse.jgit.internal.JGitText;
import org.eclipse.jgit.transport.TransportProtocol;
import org.eclipse.jgit.transport.URIish;

public class TransportProtocols {
    private static final String TAG = "TP";

    public static TransportProtocol protocolFor(URIish uri) {
        for (TransportProtocol p : getTransportProtocols()) {
            if (p.canHandle(uri))
                return p;
        }
        return null;
    }

    public static String niceProtocolNameFor(URIish uri) {
        TransportProtocol p = protocolFor(uri);
        if (p == null) {
            return null;
        }
        if (p.getSchemes().contains("file")) {
            return null;
        }
        String jGitProtocolName = p.getName();
        if (jGitProtocolName.equals(JGitText.get().transportProtoGitAnon)) {
            return "Git";
        }
        return jGitProtocolName;
    }
}
