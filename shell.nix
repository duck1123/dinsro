{ pkgs ? import <nixpkgs> {} }:
  pkgs.mkShell {
    buildInputs = with pkgs; [
      babashka
      clojure
      chromium
      docker
      docker-compose
      # earthly
      # helm
      nodejs
      openjdk
      nodejs
      # tilt
      yarn
    ];
}
