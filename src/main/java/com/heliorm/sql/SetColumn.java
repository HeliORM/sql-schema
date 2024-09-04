package com.heliorm.sql;

import java.util.Set;

/** Column representing a set */
public non-sealed interface SetColumn extends Column{

    /** Return the allowed values for the set
     *
     * @return The values
     */
    Set<String> getSetValues();

}
