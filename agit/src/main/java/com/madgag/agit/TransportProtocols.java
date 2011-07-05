package com.madgag.agit;

import org.eclipse.jgit.transport.TransportProtocol;
import org.eclipse.jgit.transport.URIish;

import static org.eclipse.jgit.transport.Transport.getTransportProtocols;

public class TransportProtocols {
    public static TransportProtocol protocolFor(URIish uri) {
        for (TransportProtocol p : getTransportProtocols()) {
            if (p.canHandle(uri))
                return p;
        }
        return null;
    }
}
