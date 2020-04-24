# Discord RPG Bot

A Discord RPG Bot, which can turn **any** server into a thrilling RPG experience!

## Setup

**NOTE**: you need the lombok plugin, on your IDE of choice, to work on this repository.

First off, there are some environment variables which need to be set:

```shell script
DISCORD_TOKEN=Your bot token
COMMAND_PREFIX=The command prefix (e.g. !)
```

**NOTE**: no defaults are set for these values.

Then you can build using gradle:

```shell script
./gradlew build
```

And finally, run the compiled jar.

