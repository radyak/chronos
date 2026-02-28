import { OAuth2Server } from 'oauth2-mock-server';

const server = new OAuth2Server();

server.service.on('beforeTokenSigning', (token) => {
    const isAccessToken = 'scope' in token.payload;
    const isIdToken = 'aud' in token.payload && !isAccessToken;

    if (isAccessToken || isIdToken) {
        token.payload.name = 'Ad Min';
        token.payload.preferred_username = 'admin';
        token.payload.given_name = 'Ad';
        token.payload.family_name = 'Min';
        token.payload.email = 'ad@min.net';
    }

    if (isAccessToken) {
        token.payload.resource_access = {
          'chronos-admin-ui': {
            roles: [
              "chronos_client_admin"
            ]
          }
        }
    }
});

await server.issuer.keys.generate('RS256');
const port = process.env.PORT || 7020
server.issuer.url = `http://auth.localhost:${port}`;
await server.start(port, '0.0.0.0');

console.log('OAuth 2.0 Mock Server running at:', server.issuer.url);
console.log('Configuration URL:', `${server.issuer.url}/.well-known/openid-configuration`);