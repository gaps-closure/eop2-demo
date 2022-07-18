#!/bin/bash

session="run"

HAL_DIR=~/gaps/hal
CFG_DIR=${HAL_DIR}/java-eop2-demo-hal

HAL=${HAL_DIR}/daemon/hal

PURPLE_CFG=hal_Purple_E.cfg
ORANGE_CFG=hal_Orange_E.cfg
GREEN_CFG=hal_Green_E.cfg

SCRIPTS=/tmp/xdcc/Purple_E/resources/scripts

#ssh liono pkill hal

export PURPLE_HAL_CMD="${HAL} -l 1 $CFG_DIR/${PURPLE_CFG}"
export ORANGE_HAL_CMD="${HAL} -l 1 $CFG_DIR/${ORANGE_CFG}"
export GREEN_HAL_CMD="${HAL} -l 1 $CFG_DIR/${GREEN_CFG}"

export PURPLE_JAVA_CMD="${SCRIPTS}/runClosure.sh Purple_E"
export ORANGE_JAVA_CMD="${SCRIPTS}/runClosure.sh Orange_E"
export GREEN_JAVA_CMD="${SCRIPTS}/runClosure.sh Green_E"

tmux start-server
tmux new-session -d -s $session -n run

tmux send-keys "$PURPLE_HAL_CMD" C-m

tmux splitw -h -p 50
tmux send-keys "$ORANGE_HAL_CMD" C-m 

tmux splitw -h -p 50
tmux send-keys "$GREEN_HAL_CMD" C-m 

tmux selectp -t 0
tmux splitw -v -p 50
tmux send-keys "$PURPLE_JAVA_CMD" C-m 

tmux selectp -t 2
tmux splitw -v -p 50
tmux send-keys "$ORANGE_JAVA_CMD" C-m

tmux selectp -t 4
tmux splitw -v -p 50
tmux send-keys "$GREEN_JAVA_CMD" C-m

tmux select-window -t $session:0
tmux attach-session -t $session
