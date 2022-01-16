{ pkgs ? import <nixpkgs> {} }:
  pkgs.mkShell {
    buildInputs = with pkgs; [
      babashka
      clojure
      docker
      # earthly
      # helm
      # kubectl
      # kustomize
      nodejs
      openjdk
      # tilt
      yarn
    ];
}
