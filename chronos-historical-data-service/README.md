# Chronos Historical Data Service

## Development

### Known issues

#### Java testcontainers' Docker client too old

Integration Tests use java testcontainers, that connect to Docker. In case of errors due to a too old Docker client
version, add this to the Docker `daemon.json`:

```json
{
  "min-api-version": "1.32"
}
```

* Linux: `/etc/docker/daemon.json`; exec `sudo systemctl restart docker` afterwards