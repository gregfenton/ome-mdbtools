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

import mdbtools.libmdb.*;

public class mdb_export
{
  private static String MY_USAGE = "usage: mdb_export <path_to_mdb> <tablename>";

  public static boolean is_text_type(int x)
  {
    return x==Constants.MDB_TEXT || x==Constants.MDB_MEMO || x==Constants.MDB_SDATETIME;
  }

  public static void main(String[] args)
  {
    int i, j, k;

    MdbHandle mdb;
    MdbCatalogEntry entry;
    MdbTableDef table;
    MdbColumn col = null;
    /* doesn't handle tables > 256 columns.  Can that happen? */
    Holder[] bound_values = new Holder[256];
    String delimiter = ",";
    boolean header_row = true;
    boolean quote_text = true;

    String filePath = args[0];
    String tableName = args[1];

    if (filePath == null || filePath.length() < Constants.MIN_FILENAME_LENGTH)
    {
      Util.die(MY_USAGE, "Filename is too short.  Must be >= " + Constants.MIN_FILENAME_LENGTH + " characters.");
    }

    if (tableName == null || tableName.length() < Constants.MIN_TABLENAME_LENGTH)
    {
      Util.die(MY_USAGE, "Table name is too short.  Must be >= " + Constants.MIN_TABLENAME_LENGTH + " characters.");
    }

    mem.mdb_init();

    try
    {
      mdb = file.mdb_open(new mdbtools.jdbc2.File(filePath));

      Catalog.mdb_read_catalog(mdb, Constants.MDB_TABLE);

      for (i=0;i<mdb.num_catalog;i++)
      {
        entry = (MdbCatalogEntry)mdb.catalog.get(i);
        if (entry.object_type == Constants.MDB_TABLE && entry.object_name.equals(tableName))
        {
          table = Table.mdb_read_table(entry);
          Table.mdb_read_columns(table);
          Data.mdb_rewind_table(table);

          for (j = 0; j < table.num_cols;j++)
          {
            bound_values[j] = new Holder();
            Data.mdb_bind_column(table, j + 1, bound_values[j]);
          }
          if (header_row)
          {
            col = (MdbColumn)table.columns.get(0);
            System.out.print(col.name);
            for (j = 1;j < table.num_cols;j++)
            {
              col = (MdbColumn)table.columns.get(j);
              System.out.print(delimiter+col.name);
            }
            System.out.println("");
          }

          while(Data.mdb_fetch_row(table))
          {
            if (quote_text && is_text_type(col.col_type))
            {
              System.out.print("\"");
              for (k = 0; k < bound_values[0].s.length();k++)
              {
                char c = bound_values[0].s.charAt(k);
                if (c == '"')
                  System.out.print("\"\"");
                else
                  System.out.print(c);
              }
              System.out.print("\"");
            }
            else
            {
              System.out.print(bound_values[0].s);
            }
            for (j = 1;j < table.num_cols;j++)
            {
              col = (MdbColumn)table.columns.get(j);
              if (quote_text && is_text_type(col.col_type))
              {
                System.out.print(delimiter);
                System.out.print("\"");
                for (k = 0; k < bound_values[j].s.length();k++)
                {
                  char c = bound_values[j].s.charAt(k);
                  if (c == '"')
                    System.out.print("\"\"");
                  else
                    System.out.print(c);
                }
                System.out.print("\"");
              }
              else
              {
                System.out.print(delimiter+bound_values[j].s);
              }
            }
            System.out.println("");
          }
        }
      }
    }
    catch(Exception e)
    {
      System.out.println(String.format("EXCEPTION processing(%s) - %s", filePath, e.getMessage()));
      e.printStackTrace();
    }
  }
}
