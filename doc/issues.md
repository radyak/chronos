# Issues

Here, issues should be documented, which don't have a place as Github issues or the like for some reason.

## Potential issues
* The `LocalDate` fields were subject to a bug, the caused the persisted dates to be shifted one day backwards in time. During development, this was solved to set the default time zone programmatically, but this may only apply to the dev H2 database. For actual production databases, it may be required to set the timezone in the JDBC connection string (H2 does not have an according property for its connections). 

## Known issues
Currently none