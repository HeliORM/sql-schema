package com.heliorm.sql.actions;

import com.heliorm.sql.Column;

public record RenameColumn(Column column) implements Action {
}
