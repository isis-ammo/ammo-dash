#!/bin/bash

#ML 5/28/12

arg1=$1
releasevar=""

if [[ "$arg1" == "--help" ]]
then
    echo "opening perldoc... this requires package 'perl-doc' to work properly. If you don't see help information, get 'perl-doc' and type './maven_versions.sh --help' again. "
    perldoc ./maven_versions.sh
    exit
fi

update_version_xml_files() {

  while read VERSION_XML; do
    #Replacing version name/code in the res/values/version.xml files
    echo "        "
    echo "attempting to update version name in ${VERSION_XML}..."
    sed -i ${VERSION_XML} -e '/<resources>/,/<\/resources>/{
            s/<string name="ammo_version_name">.*<\/string><!--end3-->/<string name="ammo_version_name">'$newversion'<\/string><!--end3-->/
            }'


    echo "attempting to update version code in ${VERSION_XML}..."
    sed -i ${VERSION_XML} -e '/<resources>/,/<\/resources>/{
            s/<integer name="ammo_version_code">.*<\/integer><!--end2-->/<integer name="ammo_version_code">'$versioncode'<\/integer><!--end2-->/
            }'
    
    # commented out git_release_tag="$(git describe --long)"
    
    if [[ $newversion =~ .*-SNAPSHOT$ ]]
    then
      echo "releasevar = ["$releasevar"]"
    else 
      releasevar="release-"
      echo "releasevar = ["$releasevar"]"
    fi

    echo "attempting to update ammo_version in ${VERSION_XML}..."
    sed -i ${VERSION_XML} -e '/<resources>/,/<\/resources>/{
            s/<string name="ammo_version">.*<\/string><!--end1-->/<string name="ammo_version">'$releasevar''$newversion'<\/string><!--end1-->/
            }'
    

  done <<HERE
./dash/res/values/version.xml
HERE

}



update_version_code () {
  read -p "Please enter the version code. It should be in the form of \"1600\": "
  versioncode=$REPLY
  echo "Updating version code to the value $versioncode..."

  if [[ $versioncode =~ ^[0-9]+$ ]]
  then
  while read A_MANIFEST; do
    #Replacing version codes in the AndroidManifest.xml files - only replaces between the 'version code template' and 'template end' comments
    sed -i ${A_MANIFEST} -e '/<!-- version code template -->/,/<!-- template end -->/{
            s/android:versionCode=.*><!--end-->/android:versionCode="'$versioncode'"><!--end-->/
            }'
    echo "attempting to update version code in ${A_MANIFEST}..."
  done <<HERE
./dash/AndroidManifest.xml
HERE

else
    echo "[ERROR] Invalid input. Version codes must only contain integers. No other characters are allowed. Please try again with a valid version code."
fi

}




update_version_number() {
    read -p "Please enter the new version number. It should be in the form of \"1.6.0\" for releases or \"1.6.0-SNAPSHOT\" for development/snapshots: "
    newversion=$REPLY
    echo "Updating version to the value [$newversion]..."

    # Checks the input to ensure it is a valid version number.
    # if it isn't, output error message and don't edit files
    if [[ $newversion =~ ^[0-9]+\.[0-9]+\.[0-9]+[-0-9a-zA-Z.]*$ ]]
    then

    # run 
    #  find . -name 'pom.xml'
    # to update the manifest information.
 
      while read POM_FILE; do
      #Replacing versions for the dash POMs - only replaces between the TEMPLATE:BEGIN and TEMPLATE:END strings
      sed -i ${POM_FILE} -e '/<!-- TEMPLATE:BEGIN/,/<!-- TEMPLATE:END -->/{
            s/<version>.*<\/version>/<version>'$newversion'<\/version>/
            }'
      echo "attempting to update version number in ${POM_FILE}..."
  done <<HERE
pom.xml
dash/pom.xml
dashlib/pom.xml
HERE


    #actual function calls are here:
    update_version_code
    


    echo "     "
    echo "Now updating the version.xml files for the android apps....."
    update_version_xml_files

else
    echo "[ERROR] Invalid input. Please try again with a valid version number."
fi
}

    #previous were just function definitions. Here is the actual call: 
    update_version_number

: <<'END_OF_DOCS'
Documentation in Perl's Plain Old Documentation (POD) format. Requires perl-doc package. 

=head1 NAME

maven_versions.sh -- Updates maven POM files for Dash project build.

=head1 SYNOPSIS

maven_versions.sh <new version>

=head1 DESCRIPTION

Very basic shell script to update versions for maven POM files 

=cut

END_OF_DOCS
