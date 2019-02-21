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

import org.exist.eclipse.ConnectionType;
import org.exist.eclipse.IConnection;
import org.exist.eclipse.exception.ConnectionException;
import org.exist.eclipse.spi.AbstractConnection;
import org.exist.xmldb.DatabaseImpl;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

/**
 * @author Patrick Reinhart
 */
class RemoteConnection extends AbstractConnection {
	private Database db;
	private Collection root;

	/**
	 * Create a new connection. Post you have to call {@link #open()}.
	 * 
	 * @param name     of the connection, mandatory field.
	 * @param username with which you connect to the database, mandatory field.
	 * @param password of the username, optional field.
	 * @param path     the uri to the remote database, mandatory field.
	 */
	 RemoteConnection(String name, String username, String password, String path) {
		super(ExistConnectionProvider.versionId(), ConnectionType.REMOTE, name, username, password, path);
	}

	@Override
	public void open() throws ConnectionException {
		openDb();
		openRoot();
		registerConnection();
	}

	/**
	 * @throws ConnectionException
	 */
	protected void openRoot() throws ConnectionException {
		if (root == null) {
			try {
				String rootUri = getRootUri();
				root = DatabaseManager.getCollection(rootUri, getUsername(), getPassword());
				if (root == null) {
					throw new ConnectionException("Failure while getting root collection for: " + rootUri);
				}
				// check whether a connection was established successfully.
				root.getChildCollectionCount();
			} catch (Exception e) {
				root = null;
				close();
				throw new ConnectionException("Failure while getting db collection: " + e.getMessage(), e);
			}
		}
	}

	/**
	 * @throws ConnectionException
	 */
	protected void openDb() throws ConnectionException {
		if (db == null) {
			try {
				db = new DatabaseImpl();
				DatabaseManager.registerDatabase(db);
			} catch (Exception e) {
				db = null;
				throw new ConnectionException("Failure while setting up db: " + e.getMessage(), e);
			}
		}
	}

	@Override
	public void close() throws ConnectionException {
		closeRoot();
		closeDb();
		// close it from the box
		deregisterConnection();
	}

	/**
	 * @throws ConnectionException
	 */
	protected void closeDb() throws ConnectionException {
		if (db != null) {
			try {
				DatabaseManager.deregisterDatabase(db);
			} catch (XMLDBException e) {
				throw new ConnectionException("Failure while shutting down db: " + e.getMessage(), e);
			} finally {
				db = null;
			}
		}
	}

	/**
	 * @throws ConnectionException
	 */
	protected void closeRoot() throws ConnectionException {
		if (isOpen()) {
			if (root != null) {
				try {
					root.close();
				} catch (XMLDBException e) {
					throw new ConnectionException("Failure while shutting down db: " + e.getMessage(), e);
				} finally {
					root = null;
				}
			}
		}
	}

	protected void setDb(Database db) {
		this.db = db;
	}

	@Override
	public Collection getRoot() {
		return root;
	}

	@Override
	public String getUri() {
		return getPath();
	}

	@Override
	public boolean isOpen() {
		if (db == null && root == null) {
			return false;
		} else {
			try {
				root.getChildCollectionCount();
				return true;
			} catch (Exception e) {
				// ignored
				return false;
			}
		}
	}

	@Override
	public IConnection duplicate() throws ConnectionException {
		RemoteConnectionWrapper wrapper = new RemoteConnectionWrapper(getName(), getUsername(),getPassword(),getPath(), db);
		wrapper.open();
		return wrapper;
	}

	// //////////////////////////////////////////////////////////////////////////
	// private methods
	// //////////////////////////////////////////////////////////////////////////
	private String getRootUri() {
		return getUri() + "/db";
	}

}
