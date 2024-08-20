Feature: Rename a column in a table

  Scenario Outline:
    Given we create a table 'Person' in a '<database>' database with these columns
      | name      | type     | length | nullable | defaultValue | key  | autoincrement | values                |
      | id        | INTEGER  |        | false    | 0            | true | true          |                       |
      | name      | VARCHAR  | 42     | true     |              |      |               |                       |
      | age       | SMALLINT |        | true     |              |      |               |                       |
      | direction | ENUM     |        | true     |              |      |               | NORTH,EAST,SOUTH,WEST |
    And we rename these columns on the table
      | from | to       |
      | name | fullName |
      | age  | oldness  |
    Then the created table must have these columns
      | name      | type     | length | nullable | defaultValue | key  | autoincrement | values                |
      | id        | INTEGER  |        | false    | 0            | true | true          |                       |
      | fullName  | VARCHAR  | 42     | true     |              |      |               |                       |
      | oldness   | SMALLINT |        | true     |              |      |               |                       |
      | direction | ENUM     |        | true     |              |      |               | NORTH,EAST,SOUTH,WEST |
    Examples:
      | database   |
      | mysql      |
      | postgresql |
