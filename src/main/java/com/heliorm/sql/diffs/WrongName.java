package com.heliorm.sql.diffs;

import com.heliorm.sql.Column;

public record WrongName(Column column) implements Diff {
}
