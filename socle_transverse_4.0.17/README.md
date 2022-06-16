# Socle transverse

## Prérequis

-   Java 8 installé
-   Maven 3 installé
-   socle_cmf compilé : à télécharger depuis le repository https://lyovgitlabidl.lyon-dev2.local/SOLON/socle_cmf

## Build

```bash
$ mvn clean install
```

## Git flow main commands

git flow init : pour initialiser Git et Gitflow dans un projet.
git flow feature start <nom> : pour démarrer le développement d’une nouvelle fonctionnalité.
git flow feature finish <nom> : pour terminer le développement d’une nouvelle fonctionnalité.
git flow release start <version> : pour démarrer le développement d’une nouvelle release.
git flow release finish <nom> : pour terminer le développement d’une nouvelle release.
git flow hotfix start <version> : pour démarrer le développement d’un nouveau hotfix.
git flow hotfix finish <nom> : pour terminer le développement d’un nouveau hotfix.

## Update dependencies
```sh
targetVersion=X.X.X
mvn versions:update-property -Dproperty=naiad.nuxeo.addons.version -DnewVersion=${targetVersion}
mvn versions:commit
git add pom.xml "*/pom.xml"
git commit -m "update dependency to naiad nuxeo addons ${targetVersion}"

targetVersion=Y.Y.Y
mvn versions:update-property -Dproperty=distrib.nuxeodist.version -DnewVersion=${targetVersion}
mvn versions:commit
git add pom.xml "*/pom.xml"
git commit -m "update dependency to nuxeo dist ${targetVersion}"

targetVersion=Z.Z.Z
mvn versions:update-parent -DnewVersion=${targetVersion}
mvn versions:commit
git add pom.xml "*/pom.xml"
git commit -m "update parent to socle nuxeo ${targetVersion}"
```

## Release commands

```sh
releaseVersion=4.0.16
nextVersion=4.0.17
git flow release start ${releaseVersion}
mvn versions:set -DnewVersion=${releaseVersion}
mvn versions:update-property -Dproperty=fr.dila.st.version -DnewVersion=${releaseVersion}
mvn versions:commit
git add pom.xml "*/pom.xml"
git commit -m "New release ${releaseVersion}"
git flow release finish ${releaseVersion} -m "Git flow release ${releaseVersion}"
mvn versions:set -DnewVersion=${nextVersion}-SNAPSHOT
mvn versions:update-property -Dproperty=fr.dila.st.version -DnewVersion=${nextVersion}-SNAPSHOT
mvn versions:commit
sed -i "s/${releaseVersion}/${nextVersion}-SNAPSHOT/" pom.xml
git add pom.xml "*/pom.xml"
git commit -m "Prepare next snapshot ${nextVersion}-SNAPSHOT"
git push origin develop
git push --tags origin master
```
