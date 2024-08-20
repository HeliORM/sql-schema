package com.heliorm.sql;

import java.sql.JDBCType;

/** Abstraction representing a SQL table column
 *
 */
public interface Column {

    /** Return the name of the column.
     *
     * @return The name
     */
    String getName();

    /** Return the JDBC type of the column.
     *
     * @return The type
     */
    JDBCType getJdbcType();

    /** Return the table in which this column is.
     *
     * @return The table
     */
    Table getTable();

    /** Return if the column can be null.
     *
     * @return True if it can be null
     */
    boolean isNullable();

    /** Return if the column is the primary key for a table.
     *
     * @return True if it is
     */
    boolean isKey();

    /** Return if the column is an auto-increment key
     *
     * @return True if it is
     */
    boolean isAutoIncrement();

    /** Get the default value for the column
     *
     * @return The default value
     */
    String getDefault();

}
