package com.heliorm.sql.diffs;

import com.heliorm.sql.Column;

public record MissDefault(Column column) implements Diff {
}
