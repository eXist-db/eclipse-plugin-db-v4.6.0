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

import org.exist.eclipse.exception.ConnectionException;
import org.xmldb.api.base.Database;

/**
 * @author Patrick Reinhart
 */
class LocalConnectionWrapper extends LocalConnection {

	LocalConnectionWrapper(String name, String username, String password, String path, String uriName, Database db) {
		super(name, username, password, path, uriName);
		setDb(db);
	}

	@Override
	public void open() throws ConnectionException {
		openRoot();
	}

	@Override
	public void close() throws ConnectionException {
		// closeRoot();
	}

}
