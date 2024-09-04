package com.heliorm.sql.differences;

import com.heliorm.sql.Column;

public record AddNullable(Column column) implements Action {
}
