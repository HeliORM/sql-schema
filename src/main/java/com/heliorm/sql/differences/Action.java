package com.heliorm.sql.differences;

sealed public interface Action permits AddAutoIncrement, AddColumn, AddEnumValue, AddKey, AddNullable, ChangeType, RemoveAutoIncrement, RemoveColumn, RemoveEnumValue, RemoveKey, RemoveNullable, RenameColumn {

}
