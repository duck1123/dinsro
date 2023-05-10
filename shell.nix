{ pkgs ? import <nixpkgs> {} }:
  pkgs.mkShell {
    buildInputs = with pkgs; [
      babashka
      clojure
      docker
      earthly
      # helm
      helm-docs
      kubectl
      kustomize
      kubeseal
      nodejs
      openjdk
      tilt
      yarn
      yq
    ];
}
