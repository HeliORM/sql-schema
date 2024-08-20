Feature: Add a column to a table

  Scenario Outline:
    Given we create a table 'Person' in a '<database>' database with these columns
      | name      | type     | length | nullable | defaultValue | key  | autoincrement | values                |
      | id        | INTEGER  |        | false    | 0            | true | true          |                       |
      | name      | VARCHAR  | 42     | true     |              |      |               |                       |
      | age       | SMALLINT |        | true     |              |      |               |                       |
      | direction | ENUM     |        | true     |              |      |               | NORTH,EAST,SOUTH,WEST |
    And we add these columns to the table
      | name    | type          | length  | nullable | defaultValue | key | autoincrement | values |
      | email   | VARCHAR       | 128     | false    |              |     |               |        |
      | income  | DOUBLE        |         | false    | 0.0          |     |               |        |
      | sex     | BOOLEAN       |         | false    | false        |     |               |        |
      | photo   | LONGVARBINARY | 1048576 | true     |              |     |               |        |
      | created | DATE          |         | true     |              |     |               |        |
      | theTime | TIME          |         | true     |              |     |               |        |
      | stamp   | TIMESTAMP     |         | false    |              |     |               |        |
      | notes   | LONGVARCHAR   | 10000   |          |              |     |               |        |
      | amount  | DECIMAL       | 18,2    |          |              |     |               |        |
      | surname | VARCHAR       | 30      | false    | Jones        |     |               |        |
    Then the created table must have these columns
      | name      | type          | length  | nullable | defaultValue | key  | autoincrement | values                |
      | id        | INTEGER       |         | false    | 0            | true | true          |                       |
      | name      | VARCHAR       | 42      | true     |              |      |               |                       |
      | age       | SMALLINT      |         | true     |              |      |               |                       |
      | direction | ENUM          |         | true     |              |      |               | NORTH,EAST,SOUTH,WEST |
      | email     | VARCHAR       | 128     | false    |              |      |               |                       |
      | income    | DOUBLE        |         | false    | 0.0          |      |               |                       |
      | sex       | BOOLEAN       |         | false    | false        |      |               |                       |
      | photo     | LONGVARBINARY | 1048576 | true     |              |      |               |                       |
      | created   | DATE          |         | true     |              |      |               |                       |
      | theTime   | TIME          |         | true     |              |      |               |                       |
      | stamp     | TIMESTAMP     |         | false    |              |      |               |                       |
      | notes     | LONGVARCHAR   | 10000   |          |              |      |               |                       |
      | amount    | DECIMAL       | 18,2    |          |              |      |               |                       |
      | surname   | VARCHAR       | 30      | false    | Jones        |      |               |                       |
    Examples:
      | database   |
      | mysql      |
      | postgresql |

