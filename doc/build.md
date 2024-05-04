# Build

The application is self-contained and built as a single Docker image. To be as platform-independent, the image is completely built inside Docker with a [multi-stage build](https://docs.docker.com/build/building/multi-stage/).

The build requires only one optional argument `APP_VERSION`, which sets the in-app displayed version (if nothing is set, the original version of 0.0.0 is not changed).

## Github Actions

The build is run via the Github Actions workflow [.github/workflows/docker-publish.yml](../.github/workflows/docker-publish.yml) in case of the following events:
* Push to master or a feature branch
* Pull request to master
* Release; in this case, the built images are also signed and promoted to Dockerhub

The workflow has the secret `DOCKERHUB_TOKEN` as its only dependency and must contain a valid Dockerhub token for the user `radyak`.