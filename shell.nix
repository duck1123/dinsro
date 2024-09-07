{ pkgs ? import <nixpkgs> {} }:
  pkgs.mkShell {
    buildInputs = with pkgs; [
      babashka
      clojure
      devspace
      docker
      earthly
      geckodriver
      # helm
      helm-docs
      # kubectl
      # kustomize
      nodejs
      openjdk
      # tilt
      yarn
      yq
    ];
}
