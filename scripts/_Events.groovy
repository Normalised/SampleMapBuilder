eventPreparePackageEnd = {installers ->
    ant.copy( todir: "${projectWorkDir}/packaged/lib/", overwrite: true ) {
        fileset( dir: "${basedir}/dist/", includes: "*.jar" )
    }
}