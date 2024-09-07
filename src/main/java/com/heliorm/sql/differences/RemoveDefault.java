package com.heliorm.sql.differences;

import com.heliorm.sql.Column;

public record RemoveDefault(Column column) implements Action  {
}
