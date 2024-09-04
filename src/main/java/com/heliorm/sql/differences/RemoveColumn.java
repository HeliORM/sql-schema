package com.heliorm.sql.differences;

import com.heliorm.sql.Column;

public record RemoveColumn(Column column) implements Action {
}
