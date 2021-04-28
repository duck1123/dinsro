{ pkgs ? import <nixpkgs> {} }:
  pkgs.mkShell {
    buildInputs = [
      pkgs.babashka
      pkgs.clojure
      pkgs.docker
      pkgs.docker-compose
      pkgs.openjdk
      pkgs.tilt
      pkgs.yarn
    ];
}
