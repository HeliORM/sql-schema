package com.heliorm.sql.differences;

import com.heliorm.sql.Column;

public record RemoveKey(Column column) implements Action {
}
