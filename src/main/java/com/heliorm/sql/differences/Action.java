package com.heliorm.sql.differences;

sealed public interface Action permits AddAutoIncrement, AddColumn, AddEnumValue, AddKey, AddNullable, AddSetValue, ChangeDefault, ChangeType, DecreaseLength, IncreaseLength, RemoveAutoIncrement, RemoveColumn, RemoveEnumValue, RemoveKey, RemoveNullable, RemoveSetValue, RenameColumn {

}
