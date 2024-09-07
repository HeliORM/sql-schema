package com.heliorm.sql.actions;

import com.heliorm.sql.Column;

public record RemoveNullable(Column column) implements Action {
}
