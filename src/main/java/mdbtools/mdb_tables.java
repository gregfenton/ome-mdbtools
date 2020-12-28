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

import mdbtools.libmdb.Catalog;
import mdbtools.libmdb.Constants;
import mdbtools.libmdb.file;
import mdbtools.libmdb.mem;
import mdbtools.libmdb.MdbCatalogEntry;
import mdbtools.libmdb.MdbHandle;
import mdbtools.libmdb.Util;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

public class mdb_tables
{
  private static String MY_USAGE =
  "usage: mdb_tables [options] <path_to_mdb>\n"
   + "   where [options] are:\n"
   + "      -1: print table names 1 to a line\n"
   + "      -d<char>: print <char> as a delimiter between table names\n"
   + "      -h: show this usage message and exit\n"
   + "      -S: skip system tables\n";
  public static void main(String[] args)
  {
    int i;
    char delimiter = ' ';
    boolean oneNamePerLine = false;
    boolean omitSystemTables = true;
    char opt;

    Vector<String> tableNames;

    String filePath = args[args.length - 1];

    if (filePath == null || filePath.length() < Constants.MIN_FILENAME_LENGTH)
    {
      Util.die("<path_to_mdb> is too short.  Must be >= " + Constants.MIN_FILENAME_LENGTH + " characters.", MY_USAGE);
    }

    // Only look at arguments up to, but not including, the last one
    // The last one is assumed to be the filePath
    for (i = 0; i < args.length - 1; i++)
    {
      opt = args[i].charAt(0);
      if (opt == '-')
      {
        opt = args[i].charAt(1);

        switch (opt)
        {
          case 'S':
            omitSystemTables = false;
            break;
          case '1':
            oneNamePerLine = true;
            break;
          case 'd':
            delimiter = args[i].charAt(2);
            break;
          case 'h':
            System.out.println(MY_USAGE);
            Runtime.getRuntime().exit(0);
            break;
        }
      }
    }

    tableNames = Util.listTables(filePath, omitSystemTables);

    Iterator<String> names = tableNames.iterator();
    while (names.hasNext())
    {
      System.out.print(names.next());
      if (oneNamePerLine)
      {
        System.out.println();
      }
      else
      {
        System.out.print(delimiter);
      }
    }
  }
}
