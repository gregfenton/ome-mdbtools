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

public class mdb_tables
{
  private static String MY_USAGE =
  "usage: mdb_tables [options] <path_to_mdb>\n"
   + "   where [options] are:\n"
   + "      -S: skip system tables\n"
   + "      -1: print table names 1 to a line\n"
   + "      -d<char>: set the delimiter between table names to <char>\n";
  public static void main(String[] args)
  {
    int   i;

    MdbHandle mdb;
    MdbCatalogEntry entry;

    char delimiter = 0;
    boolean line_break = false;
    boolean skip_sys = true;
    char opt;

    String filePath = args[args.length - 1];

    if (filePath == null || filePath.length() < Constants.MIN_FILENAME_LENGTH) {
      Util.die(MY_USAGE, "Filename is too short.  Must be >= " + Constants.MIN_FILENAME_LENGTH + " characters.");
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
            skip_sys = false;
            break;
          case '1':
            line_break = true;
            break;
          case 'd':
            delimiter = args[i].charAt(2);
            break;
          }
      }
    }

    /* initialize the library */
    mem.mdb_init();

    /* open the database */
    try
    {
      mdb = file.mdb_open(new mdbtools.jdbc2.File(filePath));
      /* read the catalog */
      Catalog.mdb_read_catalog(mdb,Constants.MDB_TABLE);
    }
    catch(IOException e)
    {
      System.out.println("Couldn't open or read database.\n");
      e.printStackTrace();
      return;
    }

    /* loop over each entry in the catalog */
    for (i=0; i < mdb.num_catalog; i++)
    {
      entry = (MdbCatalogEntry)mdb.catalog.get(i);

      /* if it's a table */
      if (entry.object_type == Constants.MDB_TABLE)
      {
        /* skip the MSys tables */
        if (skip_sys == false || entry.object_name.startsWith("MSys") == false)
        {
          if (line_break)
            System.out.println(entry.object_name);
          else if (delimiter != 0)
            System.out.print(entry.object_name + delimiter);
          else
            System.out.print(entry.object_name + " ");
        }
      }
    }

    if (!line_break)
      System.out.println("");

    try
    {
      mem.mdb_free_handle(mdb);
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
//    mdb_exit();
  }
}
