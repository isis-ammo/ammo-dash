#!/bin/bash

#ML 5/28/12

newversion=$1

# Checks the input to ensure it is a valid version number.
# if it isn't, output error message and don't edit files
if [[ ${1} =~ [0-9]*\.[0-9]*\.[0-9]*.* ]]
then

# run 
#  find . -name 'pom.xml'
# to update the manifest information.
 
  while read POM_FILE; do
    #Replacing versions for the dash POMs - only replaces between the TEMPLATE:BEGIN and TEMPLATE:END strings
    sed -i ${POM_FILE} -e '/<!-- TEMPLATE:BEGIN/,/<!-- TEMPLATE:END -->/{
            s/<version>.*<\/version>/<version>'$newversion'<\/version>/
            }'
  done <<HERE
pom.xml
dash/pom.xml
dashlib/pom.xml
HERE


else
    echo "Invalid input. Please try again with a valid version number."
fi

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
