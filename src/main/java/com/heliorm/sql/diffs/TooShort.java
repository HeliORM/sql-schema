package com.heliorm.sql.diffs;

import com.heliorm.sql.Column;

public record TooShort(Column column) implements Diff {
}
