# Releasing JAIS

JAIS now publishes through the Sonatype Central Publisher Portal rather than the retired OSSRH staging service.

## Prerequisites

1. A verified Sonatype Central namespace for `com.robotaccomplice`.
2. A Central Portal user token stored in `~/.m2/settings.xml` under server id `central`.
3. A local GPG secret key that Maven can use for signing.

Example `settings.xml` entry:

```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <username><!-- Central token username --></username>
      <password><!-- Central token password --></password>
    </server>
  </servers>
</settings>
```

## Release Steps

1. Update `pom.xml` to the target non-SNAPSHOT version.
2. Run `mvn test`.
3. Run `mvn deploy`.

With the configured `central-publishing-maven-plugin`, `mvn deploy` will:

- build the jar, sources jar, javadoc jar, signatures, and checksums
- upload the bundle to the Central Portal
- wait until the deployment is validated

The current build is configured to stop after validation so the deployment can be reviewed in the Central Portal before final publishing.

## Current Local Status

If `~/.m2/settings.xml` does not contain a `central` server and `gpg --list-secret-keys` shows no signing key, publishing will fail before upload. That needs to be set up before attempting a real release from a workstation or CI.
