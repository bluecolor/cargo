# Cargo

- Cross database data transfer tool

## Build
* `sbt assembly`

## Make dist
* `./make-dist.sh` copies the executables to `dist` folder


## Installation
* just copy the `dist` folder
* make sure you have `java`

## Running
* go to dist folder and run `./cargo -h` for command line options

```
usage: cargo
 -q,--quiet                    silent logs
 -cf,--config                  path to configurationfile eg. /home/name/app.cfg or app.cfg
 -bs,--batch-size <arg>        insert batch size
 -c,--create                   create target table
 -co,--create-options <arg>    create table options
 -d,--truncate                 truncate target table
 -fs,--fetch-size <arg>        select fetch size
 -h,--help                     print help
 -q,--quiet                    silent logs
 -s,--source-url <arg>         source jdbc url
 -sp,--source-password <arg>   source connection password
 -st,--source-table <arg>      source table
 -su,--source-username <arg>   source connection username
 -t,--target-url <arg>         target jdbc url
 -tp,--target-password <arg>   target connection password
 -tt,--target-table <arg>      target table
 -tx,--target-partition <arg>  target partition to insert
 -tu,--target-username <arg>   target connection username
 -co,--create-options <arg>    create table options
```