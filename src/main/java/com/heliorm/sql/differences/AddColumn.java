package com.heliorm.sql.differences;

import com.heliorm.sql.Column;

public record AddColumn(Column column) implements Action {
}
