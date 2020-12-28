/*
 * #%L
 * Fork of MDB Tools (Java port).
 * %%
 * Copyright (C) 2008 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

package mdbtools;

import mdbtools.libmdb.Constants;
import mdbtools.libmdb.Util;

public class mdb_export
{
  private static String MY_USAGE = "usage: mdb_export <path_to_mdb> <table_name>";

  public static void main(String[] args)
  {
    String filePath = args[0];
    String tableName = args[1];

    if (filePath == null || filePath.length() < Constants.MIN_FILENAME_LENGTH)
    {
      Util.die(MY_USAGE, "<path_to_mdb> is too short.  Must be >= " + Constants.MIN_FILENAME_LENGTH + " characters.");
    }

    if (tableName == null || tableName.length() < Constants.MIN_TABLENAME_LENGTH)
    {
      Util.die(MY_USAGE, "<table_name> is too short.  Must be >= " + Constants.MIN_TABLENAME_LENGTH + " characters.");
    }

    Util.exportTable(filePath, tableName, System.out);
  }
}
