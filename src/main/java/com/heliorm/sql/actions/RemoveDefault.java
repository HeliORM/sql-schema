package com.heliorm.sql.actions;

import com.heliorm.sql.Column;

public record RemoveDefault(Column column) implements Action  {
}
