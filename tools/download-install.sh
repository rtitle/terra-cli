#!/bin/bash

## This script downloads a release archive of the Terra CLI and calls its install script.
## Dependencies: curl, tar
## Inputs: TERRA_CLI_VERSION (env var, optional) specific version requested, default is latest
## Usage: ./download-install.sh          --> installs the latest version
##        ./download-install.sh 0.1.0    --> installs version 0.1.0

echo "--  Checking if specific version requested"
if [[ -z "${TERRA_CLI_VERSION}" ]]; then
  terraCliVersion="latest"
  echo "No specific version requested, using $terraCliVersion"
else
  terraCliVersion=$TERRA_CLI_VERSION
  echo "Specific version requested: $terraCliVersion"
fi

echo "--  Downloading release archive from GitHub"
if [ "$terraCliVersion" == "latest" ]; then
  releaseTarUrl="https://github.com/DataBiosphere/terra-cli/releases/latest/download/terra-cli.tar"
else
  releaseTarUrl="https://github.com/DataBiosphere/terra-cli/releases/download/$terraCliVersion/terra-cli.tar"
fi
archiveFileName="terra-cli.tar"
curl -L $releaseTarUrl > $archiveFileName
if [ ! -f "${archiveFileName}" ]; then
    echo "Error downloading release: ${archiveFileName}"
    exit 1
fi

echo "--  Unarchiving release"
scratchInstallDir=$PWD/terra-cli
mkdir -p $scratchInstallDir
tar -C $scratchInstallDir --strip-components=1 -xf $archiveFileName
if [ ! -f "$scratchInstallDir/install.sh" ]; then
    echo "Error unarchiving release: ${archiveFileName}"
    exit 1
fi

echo "--  Running the install script inside the release directory"
currentDir=$PWD
cd $scratchInstallDir
./install.sh
cd $currentDir

echo "-- Deleting the release archive"
rm $archiveFileName