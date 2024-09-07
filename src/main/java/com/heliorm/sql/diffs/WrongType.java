package com.heliorm.sql.diffs;

import com.heliorm.sql.Column;

public record WrongType(Column column) implements Diff {
}
