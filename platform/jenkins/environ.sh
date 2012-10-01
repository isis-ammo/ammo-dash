#!/bin/bash

function get_release_tag() {
   git describe --long --match 'release-*'
}

function get_version_full() {
   get_release_tag | awk -F\- '{print $2 "-" $3;}'
}

function get_version_base() {
   get_release_tag | awk -F\- '{print $2;}'
}

function get_version_major() {
   get_version_base | awk -F\. '{print $1;}'
}

function get_version_minor() {
   get_version_base | awk -F\. '{print $2;}'
}

function get_version_micro() {
   get_version_base | awk -F\. '{print $3;}'
}

function get_version_distance() {
   get_release_tag | awk -F\- '{print $3;}'
}

function get_version_hash() {
   get_release_tag | awk -F\- '{print $4;}'
}

function get_version_branch() {
   if [ -z "$IS_JENKINS" ]
   then
     git branch | grep '^*' | awk '{print $2;}'
   else
     if [ -z "$GIT_BRANCH" ]
     then
       echo "UnknownBranch"
     else
       echo $GIT_BRANCH
     fi
   fi
}

