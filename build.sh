# This script is used as a fallback

function cleanup () {
    
    rm *.class
    for i in *
    do
        if [[ -d $i ]]
        then
            cd "$i"
            cleanup
            cd ..
        fi
    done
}
echo Building JAR
javac src/*/*.java --release 11
cd src
jar cfe ../build/yuuka.jar yuuka/main */*.class
cleanup
cd ..
