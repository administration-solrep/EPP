This addon implements a BinaryManager that stores binaries in a SQL database.
For efficiency, a local disk cache (with limited size) is also used.

To be able to work this addon needs some configuration:
- a table in the SQL database,
- a datasource,
- an activation in the repository configuration file.


1. Create Table

Create the table in the SQL database like follows. The table name is
arbitrary (and specified in the repository configuration file), in
the example below it's "binaries".

PostgreSQL:
  CREATE TABLE binaries (id VARCHAR(40) PRIMARY KEY, bin BYTEA, mark BOOL);

MySQL:
  CREATE TABLE binaries (id VARCHAR(40) PRIMARY KEY, bin BLOB, mark BIT);

Oracle:
  CREATE TABLE binaries (id VARCHAR2(40) PRIMARY KEY, bin BLOB, mark NUMBER(1,0));

SQL Server:
  CREATE TABLE binaries (id VARCHAR(40) PRIMARY KEY, bin VARBINARY(MAX), mark BIT);


2. Datasource

Create (or reuse) a datasource pointing to the database containing
the table defined above.


3. Repository Configuration File

Customize the default-repository-config.xml template. To do so copy the one
from the template of your current database to templates/custom/nxserver/config,
and activate the custom templates.
Refer to http://doc.nuxeo.com/display/NXDOC/Configuring+Nuxeo+EP
for more information on customizing templates.

In the custom default-repository-config.xml, in the innermost <repository>
element, add the lines:

        <binaryManager class="org.nuxeo.ecm.core.storage.sql.SQLBinaryManager"
          key="datasource=jdbc/binaries,table=binaries,cachesize=10MB" />

Here you can configure datasource name, the table name, and the maximum on-disk cache size.
