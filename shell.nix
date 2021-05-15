{ pkgs ? import <nixpkgs> {} }:
  pkgs.mkShell {
    buildInputs = with pkgs; [
      babashka
      clojure
      docker
      docker-compose
      earthly
      openjdk
      tilt
      yarn
    ];
}
