/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2019 The eXist Project
 *  http://exist-db.org
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.exist.eclipse.db.v460;

import static org.exist.xmldb.XmldbURI.xmldbUriFor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;

import org.exist.backup.Backup;
import org.exist.backup.Restore;
import org.exist.eclipse.IConnection;
import org.exist.eclipse.ICredentials;
import org.exist.eclipse.IDatabaseInstance;
import org.xml.sax.SAXException;
import org.xmldb.api.base.ErrorCodes;
import org.xmldb.api.base.XMLDBException;

/**
 * @author Patrick Reinhart
 */
public class ExistConnectionProvider implements IDatabaseInstance {

	static String versionId() {
		return "4.6.0";
	}

	@Override
	public String version() {
		return versionId();
	}

	@Override
	public IConnection createLocalConnection(String name, String username, String password, String path) {
		return new LocalConnection(name, username, password, path);
	}

	@Override
	public IConnection createRemoteConnection(String name, String username, String password, String path) {
		return new RemoteConnection(name, username, password, path);
	}

	@Override
	public void backup(ICredentials credentials, String location, String uri) throws XMLDBException {
		try {
			new Backup(credentials.getUsername(), credentials.getPassword(), Paths.get(location), xmldbUriFor(uri))
					.backup(false, null);
		} catch (URISyntaxException | IOException | SAXException e) {
			throw new XMLDBException(ErrorCodes.VENDOR_ERROR, e);
		}
	}

	@Override
	public void restore(ICredentials credentials, String location, String uri) throws XMLDBException {
		try {
			new Restore().restore(null, credentials.getUsername(), credentials.getPassword(), null, Paths.get(location),
					uri);
		} catch (ParserConfigurationException | SAXException | URISyntaxException | IOException e) {
			throw new XMLDBException(ErrorCodes.VENDOR_ERROR, e);
		}
	}

}
