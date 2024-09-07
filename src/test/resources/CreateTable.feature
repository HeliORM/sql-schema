Feature: Create a table

  Scenario: Create a table
    Given A table has these columns
      | name   | type        |
      | id     | integer     |
      | name   | varchar(50) |
      | age    | integer     |
      | salary | double      |
      | cool   | boolean     |
    Then the table must have these columns
      | name   | type        |
      | id     | integer     |
      | name   | varchar(50) |
      | age    | integer     |
      | salary | double      |
      | cool   | boolean     |
