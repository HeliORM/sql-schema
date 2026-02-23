package com.heliorm.sql;

import java.util.Set;

/** Abstraction representing a SQL table index
 *
 */
public interface Index {

    /** Get the name of the index
     *
     * @return The name
     */
    String name();

    /** Get the table to which the index applies
     *
     * @return The table
     */
    Table table();

    /** Get the columns in the table making up the index.
     *
     * @return The columns
     */
    Set<Column> columns();

    /** Is the index unique?
     *
     * @return True if so
     */
    boolean unique();

}
