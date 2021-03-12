#!/bin/bash

## This script renders configuration files needed for development and CI/CD.
## Dependencies: vault
## Usage: ./tools/render-config.sh

## The script assumes that it is being run from the top-level directory "terra-cli/".

VAULT_TOKEN=${1:-$(cat $HOME/.vault-token)}
DSDE_TOOLBOX_DOCKER_IMAGE=broadinstitute/dsde-toolbox:consul-0.20.0
CI_SA_VAULT_PATH=secret/dsde/terra/kernel/dev/common/ci/ci-account.json

mkdir -p rendered

echo "Reading the CI service account key file from Vault"
#vault read -format json secret/dsde/terra/kernel/dev/common/ci/ci-account.json | jq .data > rendered/ci-account.json
docker run --rm -e VAULT_TOKEN=$VAULT_TOKEN ${DSDE_TOOLBOX_DOCKER_IMAGE} \
            vault read -format json ${CI_SA_VAULT_PATH} \
            | jq -r .data > rendered/ci-account.json
