package com.heliorm.sql.diffs;

import com.heliorm.sql.Column;

public record TooLong(Column column) implements Diff {
}
