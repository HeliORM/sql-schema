package com.heliorm.sql;

import java.sql.JDBCType;

/** Abstraction representing a SQL table column
 *
 */
public sealed interface Column permits BinaryColumn, BitColumn, BooleanColumn, DateTimeColumn, DecimalColumn, DoubleColumn, EnumColumn, IntegerColumn, SetColumn, SqlColumn, StringColumn, TimeStampColumn {

    /** Return the name of the column.
     *
     * @return The name
     */
    String name();

    /** Return the JDBC type of the column.
     *
     * @return The type
     */
    JDBCType jdbcType();

    /** Return the table in which this column is.
     *
     * @return The table
     */
    Table table();

    /** Return if the column can be null.
     *
     * @return True if it can be null
     */
    boolean nullable();

    /** Return if the column is the primary key for a table.
     *
     * @return True if it is
     */
    boolean key();

    /** Return if the column is an auto-increment key
     *
     * @return True if it is
     */
    boolean autoIncrement();

    /** Get the default value for the column
     *
     * @return The default value
     */
    String defaultValue();

}
