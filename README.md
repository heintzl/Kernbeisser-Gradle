# Kernbeisser

Desktop application (Java 21 / Swing) for running the Kernbeisser food co-op store: checkout,
articles, members, inventory and reports. Data is stored in MariaDB via Hibernate.

## Quickstart

Requirements: Docker (with the Compose plugin) and `make`. A local JDK 21 is optional: without
one, Gradle runs in Docker automatically.

```sh
cp docker/.env.example docker/.env   # optional: adjust ports and passwords
make db-up                           # start MariaDB (localhost:3306, db/user/password: kernbeisser)
make gui                             # build the app and run it in Docker
```

Then open <http://localhost:6080/vnc.html?autoconnect=1&resize=remote>; the app runs in the browser
tab. A Java debugger can attach to `localhost:5005` at any time.

On a machine with a desktop and JDK 21 you can run the app natively instead:

```sh
make run      # build and start the app against the dev database
make debug    # same, with a debugger port on localhost:5005 (SUSPEND=y waits for the debugger)
```

Run `make` for the list of all targets.

### Demo data

On an empty database the app creates its tables on first start, but there is **no user to log in
with**. Put a dump into `docker/initdb/` *before* the first `make db-up` (it is imported
automatically when the database volume is created), or load it later:

```sh
make db-import DUMP=path/to/dump.sql   # into the running database
# or start over:
make db-reset && make db-up            # deletes the volume and re-imports docker/initdb/
```

Dumps in `docker/initdb/` are ignored by git.

## Development

### Build and test

| Command        | What it does                                                        |
|----------------|---------------------------------------------------------------------|
| `make build`   | Builds `build/deploy/` (fat jar, `security.jar`, start scripts, reports) |
| `make test`    | Runs the unit tests; report in `build/reports/tests/test/index.html` |
| `make check`   | Formatting check (spotless) and tests                               |
| `make format`  | Formats all Java sources with google-java-format                    |
| `make clean`   | Deletes all build output                                            |

`make` uses a local JDK 21 if `java` is version 21, otherwise `docker/gradle-in-docker.sh`
(the Gradle cache is kept in the Docker volume `kernbeisser-gradle-cache`). Force either with
`USE_DOCKER=1` or `USE_DOCKER=0`. Without `make`, the wrapper also works directly:
`java -cp gradle/wrapper/gradle-wrapper.jar org.gradle.wrapper.GradleWrapperMain shadowJar`.

> **Known issue:** many unit tests currently fail (they need a display, a database connection or
> the FontAwesome font). They had never actually run under Gradle before, because the test task
> used the JUnit 4 runner for JUnit 5 tests. `make build` therefore does not run the tests.

### Debugging

Both ways of running the app open a JDWP debug port (default `5005`, set `DEBUG_PORT` to change).
In IntelliJ IDEA: *Run → Edit Configurations → + → Remote JVM Debug*, host `localhost`, port `5005`.
In VS Code: a `java` launch configuration with `"request": "attach"`, `"port": 5005`.

To debug startup code, make the JVM wait for the debugger: `make debug SUSPEND=y` or
`make gui SUSPEND=y`.

The app must always be started with `-javaagent:security.jar` (the permission system is
implemented as a Java agent); the Makefile and the GUI container take care of that. In IntelliJ,
the shared run configuration `Main(Proxy)` does the same.

### Database

| Command           | What it does                                                 |
|-------------------|--------------------------------------------------------------|
| `make db-up`      | Start MariaDB and wait until it is ready                      |
| `make db-down`    | Stop it (data is kept in the Docker volume)                   |
| `make db-shell`   | MariaDB shell on the dev database                             |
| `make db-logs`    | Follow the database log                                       |
| `make db-import DUMP=…` | Import a `.sql` or `.sql.gz` dump into the running database |
| `make db-dump [DUMP=…]` | Write the dev database to a dump file                 |
| `make db-reset`   | **Delete** the database volume                                |
| `make adminer`    | Database web UI on <http://localhost:8081> (server `db`)      |
| `make down`       | Stop all containers                                           |

The app reads its connection settings from a `config.json`. For `make run`/`make debug` it is
generated as `.dev/config.json` (delete it to regenerate after changing `docker/.env`); the GUI
container writes its own on every start.

### Remote machines

All ports are bound to `127.0.0.1` only. When the environment runs on a remote host, forward the
ports over SSH, e.g. `ssh -L 6080:localhost:6080 -L 5005:localhost:5005 -L 3306:localhost:3306 host`.

### Troubleshooting

- **Building the GUI image fails in a `RUN` step with "permission denied"** (seen with
  AppArmor-confined rootless Docker): set `DOCKER_BUILDKIT=0` in `docker/.env`.
- **Port already in use:** change `DB_PORT`, `NOVNC_PORT`, `DEBUG_PORT`, … in `docker/.env`.
- **GUI container exits immediately:** check `docker compose -f docker/compose.yaml logs gui`;
  usually `make build` has not been run, or the database is not reachable.

## Project layout

| Path           | Content                                                              |
|----------------|----------------------------------------------------------------------|
| `app/`         | The application (`kernbeisser.Main`)                                 |
| `security/`    | Java agent enforcing the permission system (`security.jar`)          |
| `kbmetamodel/` | Annotation processor generating the JPA metamodel classes (`Article_` …) |
| `reports/`     | JasperReports templates (`*.jrxml`)                                  |
| `production/`  | Start scripts and splash screen shipped in `build/deploy/`           |
| `docker/`      | Development environment (database, GUI container, Gradle in Docker)  |

## License

See [LICENSE](LICENSE).
