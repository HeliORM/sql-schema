package com.heliorm.sql.actions;

import com.heliorm.sql.diffs.HasAutoIncrement;
import com.heliorm.sql.diffs.MissAutoIncrement;

sealed public interface Action permits AddAutoIncrement, AddColumn, AddDefault, AddEnumValue, AddKey, AddNullable, AddSetValue, ChangeDefault, ChangeType, DecreaseLength, IncreaseLength, RemoveAutoIncrement, RemoveColumn, RemoveDefault, RemoveEnumValue, RemoveKey, RemoveNullable, RemoveSetValue, RenameColumn{

}
