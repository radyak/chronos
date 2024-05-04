# Deployment

## Configuration

The application should be configured with one of the following:
* *Environment variables*: for productive scenarios, where it runs in a container
* *Spring properties*: when running it locally for development

### Security

Chronos uses a standard OAuth Authorization codeflow

| Env variable | Spring property | Explanation | Default value |
|--|--|--|--|
| `APP_OAUTH_ISSUER` | `app.auth.issuer` | The issuer base URL to prepend to `.../.well-known/openid-configuration`; in case of Keycloak typically something like https://my.auth.server/realms/myrealm or https://my.auth.server/auth/realms/myrealm | *No default* |
| `APP_OAUTH_CLIENT_ID` | `app.auth.client-id` | The client's configured ID | `chronos-admin-ui` |
| `APP_OAUTH_PRINCIPLE_ATTRIBUTE` | `app.auth.principle-attribute` | The JWT attribute to extract the username from | `preferred_username` |
| `APP_OAUTH_ADMIN_ROLE` | `app.auth.admin-role` | The authorizing role for admins | `chronos_client_admin` |

### Persistence

Chronos uses *Postgres* as its productive database. The connection can be configured with the following properties

| Env variable | Spring property | Explanation | Default value |
|--|--|--|--|
| `DB_HOST` |  | The database's host name  |  |
| `DB_PORT` |  | The database's port | `5432` |
| `DB_DATABASE` |  | The name of the database's schema | `chronos-db` |
|  | `spring.datasource.url` | Spring property, alternatively for the properties above | `jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_DATABASE}` |
| `DB_USER` | `spring.datasource.username` | The database user's name | `chronos-db-user` |
| `DB_PASSWORD` | `spring.datasource.password` | The database user's password |  |


## Security Setup

The application is using a standard OAuth [Authorization Code Flow with PKCE](https://auth0.com/docs/get-started/authentication-and-authorization-flow/authorization-code-flow-with-pkce), plus OIDC.
I.e.:
* The UI is the *client* (`app.auth.client-id`)
* The backend is a *resource server* (but also provides the `/api/config/web/app-config.json` with the client config details for the UI)
* A central IDP would be used as *authorization server* (e.g. Keycloak)

The backend looks for authorizing roles in a certain structure in the provided JWT, i.e. the payload would have to look like this:
`resource_access.${app.auth.client-id}.roles`. This should be an array, containing the `${app.auth.admin-role}`.
In addition to that, the username would be extracted from a payload property defined by `${app.auth.principle-attribute}`

Example for Keycloak:
* User: needs a "Role Mapping" for a *Realm Role* (e.g. client_admin)
* The *Realm Role* needs to be associated with a *Client Role* (e.g. chronos_client_admin)
* The JWT payload needs to look like this:
```json
  "resource_access": {
    "chronos-admin-ui": {   <- ${app.auth.client-id}
      "roles": [
        "chronos_client_admin"  <- ${app.auth.admin-role}
      ]
    }
  }
  ...
  "preferred_username": "..."   <- ${app.auth.principle-attribute}
```
