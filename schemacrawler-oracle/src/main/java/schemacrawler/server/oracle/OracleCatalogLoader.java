/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.server.oracle;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.DatabaseUtility.executeScriptFromResource;

import java.sql.Connection;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.catalogloader.BaseCatalogLoader;
import schemacrawler.tools.executable.CommandDescription;

public final class OracleCatalogLoader extends BaseCatalogLoader {

  public OracleCatalogLoader() {
    super(new CommandDescription("oracleloader", "Loader for Oracle databases"), -1);
  }

  @Override
  public void loadCatalog() throws SchemaCrawlerException {

    if (!isDatabaseSystemIdentifier("oracle")) {
      return;
    }

    final Connection connection = getConnection();
    requireNonNull(connection, "No connection provided");

    executeScriptFromResource(connection, "/schemacrawler-oracle.before.sql");
  }
}
