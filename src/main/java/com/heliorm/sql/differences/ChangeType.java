package com.heliorm.sql.differences;

import com.heliorm.sql.Column;

public record ChangeType(Column column) implements Action {
}
