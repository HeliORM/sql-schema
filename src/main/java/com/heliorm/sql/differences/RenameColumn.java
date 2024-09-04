package com.heliorm.sql.differences;

import com.heliorm.sql.Column;

public record RenameColumn(Column column) implements Action {
}
