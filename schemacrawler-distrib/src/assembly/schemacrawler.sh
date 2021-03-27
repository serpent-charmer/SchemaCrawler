#!/usr/bin/env bash
is_symlink=$(dirname "${0}")
if [[ $is_symlink == "." ]]; then
	SC_DIR=$(dirname "$0")
else
	SC_DIR=$(dirname $(readlink "${0}"))
fi
java -cp "$SC_DIR"/lib/*:"$SC_DIR"/config schemacrawler.Main "$@"
