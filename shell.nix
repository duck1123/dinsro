{ pkgs ? import <nixpkgs> {} }:
  pkgs.mkShell {
    buildInputs = with pkgs; [
      babashka
      clojure
      devspace
      docker
      earthly
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
