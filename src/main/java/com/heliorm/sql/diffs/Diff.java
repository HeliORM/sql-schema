package com.heliorm.sql.diffs;

import com.heliorm.sql.Column;

public sealed interface Diff permits HasAutoIncrement, HasColumn, HasDefault, HasKey, HasNullable, HasValue, MissAutoIncrement, MissColumn, MissDefault, MissKey, MissNullable, MissValue, TooLong, TooShort, WrongDefault, WrongName, WrongType {

    Column column();
}
