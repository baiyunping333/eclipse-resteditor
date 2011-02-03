#!/bin/sh

THIS=`basename $0`
LOCAL_DIR="eclipse"
ZIP_FILE="eclipse.zip"

HOST="chamechaude"
DIST_USER=$USER
DIST_DIR="~"
DIST_SCRIPT="cd $DIST_DIR && sudo ./update_site"

echo "Passage au répertoire parent"
cd ..

echo "Nettoyage..."
rm $ZIP_FILE

echo "Compression..."
find $LOCAL_DIR | grep -v ".svn" | grep -v $THIS | zip $ZIP_FILE -@

echo "Envoi..."
scp $ZIP_FILE $DIST_USER@$HOST:$DIST_DIR/$ZIP_FILE

echo "Lancement du script distant..."
ssh -t $DIST_USER@$HOST "$DIST_SCRIPT"

echo "Retour au bon répertoire"
cd -

echo "Fini"
