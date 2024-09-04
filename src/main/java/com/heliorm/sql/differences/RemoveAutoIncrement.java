package com.heliorm.sql.differences;

import com.heliorm.sql.Column;

public record RemoveAutoIncrement(Column column) implements Action {
}
