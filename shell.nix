{ pkgs ? import <nixpkgs> {} }:
  pkgs.mkShell {
    buildInputs = with pkgs; [
      babashka
      clojure
      chromium
      docker
      docker-compose
      # earthly
      openjdk
      nodejs
      # tilt
      yarn
    ];
}
