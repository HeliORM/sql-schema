Feature: Add a column to a table

  Scenario: Add a column to a table
    Given A table has these columns
      | name | type    |
      | id   | integer |
    When columns are added
      | name | type        |
      | name | varchar(50) |
    Then the table must have these columns
      | name | type        |
      | id   | integer     |
      | name | varchar(50) |
      | age  | integer     |