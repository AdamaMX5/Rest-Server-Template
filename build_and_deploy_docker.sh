#!/bin/sh

IMAGE_VERSION=:0.1
BRANCH=master

# parameters to this script
INSTANCE_NAME=$1
INSTANCE_DOMAIN=$2
INSTANCE_API_PORT=$3
INSTANCE_UI_PORT=$4

if [ -z "$INSTANCE_NAME" ] || [ -z "$INSTANCE_DOMAIN" ] ; then
  echo "Error: missing parameters"
  echo "Usage: $0 <instance_name> <instance_domain> [<api_port> [<ui_port>]]"
  echo " E.g.: $0 flussmark       flussmark.de      8001        3000"
  exit 1
fi

# create required destination directories
DESTINATION=~/apps/$INSTANCE_NAME
echo "Installing to $DESTINATION"
COMPOSE_PATH=$DESTINATION/docker-compose.yml
DOTENV_PATH=$DESTINATION/.env
mkdir -p $DESTINATION
mkdir -p $DESTINATION/data
mkdir -p $DESTINATION/data/mysql
OVERRIDES_PATH=$DESTINATION/data/overrides.properties
OVERRIDES_EXAMPLE_PATH=./api/src/main/resources/overrides.properties.example

# create initial overrides.properties, if not exists
if [ ! -f "$OVERRIDES_PATH" ]; then
  echo "Creating initial $OVERRIDES_PATH"
  echo " ... please adjust with your settings!"

  JWT_SECRET=`python3 -c 'import secrets; print(secrets.token_urlsafe(32))'`
  sed -e "s|jwtSecret=|jwtSecret=$JWT_SECRET|" $OVERRIDES_EXAMPLE_PATH > $OVERRIDES_PATH
  if [ $? -ne 0 ]; then
    echo "Could not create overrides.properties ... exiting"
    exit 1
  fi

else
  echo "$OVERRIDES_PATH found."
fi

# Set/update configuration parameters
sed -i -e "s|bank.url=.*$|bank.url=$INSTANCE_DOMAIN|" \
    -e "s|bank.name=.*$|bank.name=$INSTANCE_NAME|" \
    $OVERRIDES_PATH

# Switch to desired branch
git checkout $BRANCH
# git stash
# git reset --hard HEAD

echo "Building docker container flussmark-api$IMAGE_VERSION"
docker build -f docker/api/Dockerfile -t flussmark-api$IMAGE_VERSION .

if [ $? -ne 0 ]; then
  echo "Building failed ... exiting"
  exit 1
fi

echo "Building docker container flussmark-ui$IMAGE_VERSION"
docker build -f docker/ui/Dockerfile -t flussmark-ui$IMAGE_VERSION .

if [ $? -ne 0 ]; then
  echo "Building failed ... exiting"
  exit 1
fi

# create .env and docker-compose.yml
echo "Creating .env for $COMPOSE_PATH"

echo "DOMAIN=$INSTANCE_DOMAIN" > $DOTENV_PATH
echo "NAME=$INSTANCE_NAME" >> $DOTENV_PATH
echo "API_PORT=$INSTANCE_API_PORT" >> $DOTENV_PATH
echo "UI_PORT=$INSTANCE_UI_PORT" >> $DOTENV_PATH
echo "DATA_DIR=$DESTINATION" >> $DOTENV_PATH
echo "IMAGE_VERSION=$IMAGE_VERSION" >> $DOTENV_PATH

cat docker/docker-compose-for-traefik.yml > $COMPOSE_PATH


(  # in a subshell to prevent pwd change
  echo "Deploying container"
  cd $DESTINATION
  docker compose up -d
)
