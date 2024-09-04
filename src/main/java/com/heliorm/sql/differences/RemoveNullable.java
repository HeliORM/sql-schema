package com.heliorm.sql.differences;

import com.heliorm.sql.Column;

public record RemoveNullable(Column column) implements Action {
}
