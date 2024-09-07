package com.heliorm.sql.actions;

import com.heliorm.sql.Column;

public record RemoveColumn(Column column) implements Action {
}
