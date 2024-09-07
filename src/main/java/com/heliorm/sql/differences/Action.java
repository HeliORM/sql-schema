package com.heliorm.sql.differences;

sealed public interface Action permits AddAutoIncrement, AddColumn,
        AddDefault, AddEnumValue, AddKey, AddNullable, AddSetValue,
        ChangeDefault, ChangeType, DecreaseLength, IncreaseLength,
        RemoveAutoIncrement, RemoveColumn, RemoveDefault, RemoveEnumValue,
        RemoveKey, RemoveNullable, RemoveSetValue, RenameColumn {

}
