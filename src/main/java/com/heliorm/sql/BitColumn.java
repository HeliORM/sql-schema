package com.heliorm.sql;

/** Column representing a bitset */
public non-sealed interface BitColumn extends Column {

    /** Get the number of bits in the column.
     *
     * @return The bits
     */
    int getBits();

}
