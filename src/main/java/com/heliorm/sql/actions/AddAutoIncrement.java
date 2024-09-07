package com.heliorm.sql.actions;

import com.heliorm.sql.Column;

public record AddAutoIncrement(Column column) implements Action {
}
