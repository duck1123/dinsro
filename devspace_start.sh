#!/usr/bin/env bash
set +e  # Continue on errors

export COLOR_BLUE="\033[0;94m"
export COLOR_GREEN="\033[0;92m"
export COLOR_RESET="\033[0m"

# Print useful output for user
echo -e "${COLOR_BLUE}
     %########%
     %###########%       ____                 _____
         %#########%    |  _ \   ___ __   __ / ___/  ____    ____   ____ ___
         %#########%    | | | | / _ \\\\\ \ / / \___ \ |  _ \  / _  | / __// _ \\
     %#############%    | |_| |(  __/ \ V /  ____) )| |_) )( (_| |( (__(  __/
     %#############%    |____/  \___|  \_/   \____/ |  __/  \__,_| \___\\\\\___|
 %###############%                                  |_|
 %###########%${COLOR_RESET}


Welcome to your development container!

This is how you can work with it:
- Files will be synchronized between your local machine and this container
- Some ports will be forwarded, so you can access this container via localhost
- Run \`${COLOR_GREEN}devspace run start${COLOR_RESET}\` to start the application
"

# Set terminal prompt
export PS1="\[${COLOR_BLUE}\]devspace\[${COLOR_RESET}\] \[${COLOR_BLUE}\]\\$\[${COLOR_RESET}\] "
if [ -z "$BASH" ]; then export PS1="$ "; fi

# Include project's bin/ folder in PATH
export PATH="./bin:$PATH"

# Open shell
# bash --norc

set -x

# byobu new-session -d -s "Devspace" -n "read" "ls -al && read -n1"
# byobu attach -t "Devspace"
# byobu new-window -t "Devspace" -n "tilt" "tilt up --legacy=true --host=0.0.0.0"
# byobu attach -t "Devspace"

yarn install

byobu new-session "tilt up --legacy=true --host=0.0.0.0"
