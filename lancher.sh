 # !/bin/bash
 ################################
 # This is a lancher file. 
 # Establishes sockets all the node in the config file.
 #################################

###
# Main body of script starts here
###
echo "Shell script Invoked...."
netid=axl160730

PROJDIR=$HOME/CS6378/Project1
CONFIG=$PROJDIR/config.txt
BINDIR=$PROJDIR/bin
PROG=service


cat $CONFIG | sed -e "s/#.*//" | sed -e "/^\s*$/d" | 
(
	while  read line 
	do 
		HOST=$(echo $line | awk '{print $1}')
		PORT=$(echo $line | awk '{print $2}')
		NEIGH=$(echo $line | awk '{$1=$2=""; print $0}' | sed 's/ //g')
		echo " params passed:: $HOST : $PORT : $NEIGH"

		ssh -o StrictHostKeyChecking=no $USER@$HOST java -cp $BINDIR $PROG $HOST $PORT $NEIGH > $HOST$PORT &

	done
)




# echo "Shell Script invoked...."
# USER=axl160730
# HOME_DIR='/Users/Abhinav/Documents'
# PROJ_DIR=$HOME_DIR/workspace/AOSproj1/src
# BIN_DIR=$HOME_DIR/workspace/AOSproj1/bin
# CONFIG=$PROJ_DIR/config.sh
# PROG='service'


# echo "Source Code : $PROJ_DIR "
# echo "Config file : $CONFIG "
# echo "ProgramExec : $PROG"
# function filterLine()
# {
# 	host=$(echo $line | awk '{print $1}')
# 	port=$(echo $line | awk '{print $2}')
# 	neghibors=$(echo $line | awk '{$1=$2=""; print $0}')
# 	echo "$HOST $PORT $NEIGH"
# }
