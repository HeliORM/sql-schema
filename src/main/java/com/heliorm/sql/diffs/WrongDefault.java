package com.heliorm.sql.diffs;

import com.heliorm.sql.Column;

public record WrongDefault(Column column) implements Diff {
}
